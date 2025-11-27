package com.datacompress.client;

import com.datacompress.algorithm.CompressionAlgorithm;
import com.datacompress.algorithm.CompressionFactory;
import com.datacompress.model.PerformanceMetrics;
import com.datacompress.protocol.ResponseMessage;
import com.datacompress.protocol.ResponseMessageDecoder;
import com.datacompress.protocol.TransferMessage;
import com.datacompress.protocol.TransferMessageEncoder;
import com.datacompress.protocol.HeartbeatMessage;
import com.datacompress.protocol.HeartbeatMessageEncoder;
import com.datacompress.protocol.HeartbeatMessageDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端通信类
 * 负责与服务端连接、发送压缩数据并接收响应
 */
public class CompressionClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CompressionClient.class);
    
    private String host;
    private int port;
    private EventLoopGroup group;
    private Channel channel;
    private boolean connected = false;
    
    public CompressionClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * 连接到服务器
     * @return 连接是否成功
     */
    public CompletableFuture<Boolean> connect() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            
                            // 使用统一消息解码器
                            pipeline.addLast("unifiedDecoder", new com.datacompress.protocol.UnifiedMessageDecoder());
                            
                            // 添加编码器
                            pipeline.addLast("heartbeatEncoder", new HeartbeatMessageEncoder());
                            pipeline.addLast("transferMessageEncoder", new TransferMessageEncoder());
                        }
                    });
            
            logger.info("正在连接到服务器 {}:{}", host, port);
            
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (channelFuture1.isSuccess()) {
                    channel = channelFuture1.channel();
                    connected = true;
                    logger.info("成功连接到服务器");
                    future.complete(true);
                } else {
                    logger.error("连接服务器失败", channelFuture1.cause());
                    future.complete(false);
                }
            });
            
        } catch (Exception e) {
            logger.error("连接服务器时发生异常", e);
            future.complete(false);
        }
        
        return future;
    }
    
    /**
     * 发送文件数据
     * @param fileData 文件数据
     * @param fileName 文件名（含扩展名）
     * @param algorithmName 压缩算法名称
     * @param compressionLevel 压缩级别
     * @param progressCallback 进度回调
     * @return 性能指标
     */
    public CompletableFuture<PerformanceMetrics> sendFile(byte[] fileData, String fileName,
                                                          String algorithmName, 
                                                          int compressionLevel,
                                                          ProgressCallback progressCallback) {
        CompletableFuture<PerformanceMetrics> future = new CompletableFuture<>();
        
        if (!connected || channel == null || !channel.isActive()) {
            future.completeExceptionally(new IllegalStateException("未连接到服务器"));
            return future;
        }
        
        // 在新线程中执行压缩和发送操作
        new Thread(() -> {
            try {
                PerformanceMetrics metrics = new PerformanceMetrics();
                metrics.setOriginalSize(fileData.length);
                metrics.setAlgorithmName(algorithmName);
                
                // 获取压缩算法
                CompressionAlgorithm algorithm = CompressionFactory.getAlgorithm(algorithmName);
                if (algorithm == null) {
                    throw new IllegalArgumentException("不支持的压缩算法: " + algorithmName);
                }
                
                metrics.setAlgorithmId(algorithm.getAlgorithmId());
                
                if (progressCallback != null) {
                    progressCallback.onProgress(0.1, "正在压缩数据...");
                }
                
                // 压缩数据（使用指定的压缩级别）
                long compressStartTime = System.currentTimeMillis();
                metrics.setCompressStartTime(compressStartTime);
                
                byte[] compressedData = algorithm.compress(fileData, compressionLevel);
                
                long compressEndTime = System.currentTimeMillis();
                metrics.setCompressEndTime(compressEndTime);
                metrics.setCompressedSize(compressedData.length);
                
                logger.info("压缩完成 - 算法: {}, 级别: {}, 原始大小: {} bytes, 压缩后: {} bytes, 压缩比: {:.2f}%, 耗时: {} ms",
                        algorithmName, compressionLevel, fileData.length, compressedData.length,
                        metrics.getCompressionRatio() * 100, metrics.getCompressionTime());
                
                if (progressCallback != null) {
                    progressCallback.onProgress(0.5, "正在发送数据...");
                }
                
                // 创建传输消息
                long sendStartTime = System.currentTimeMillis();
                metrics.setSendStartTime(sendStartTime);
                
                // 设置发送结束时间（在发送之前）
                long sendEndTime = System.currentTimeMillis();
                metrics.setSendEndTime(sendEndTime);
                
                TransferMessage transferMsg = new TransferMessage(
                        algorithm.getAlgorithmId(),
                        fileData.length,
                        compressedData.length,
                        compressStartTime,
                        compressEndTime,
                        sendStartTime,
                        sendEndTime,
                        fileName,
                        compressedData
                );
                
                // 发送消息并等待响应
                CompletableFuture<ResponseMessage> responseFuture = new CompletableFuture<>();
                
                channel.pipeline().addLast(new SimpleChannelInboundHandler<ResponseMessage>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
                        responseFuture.complete(msg);
                        ctx.pipeline().remove(this);
                    }
                    
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        responseFuture.completeExceptionally(cause);
                        ctx.pipeline().remove(this);
                    }
                });
                
                channel.writeAndFlush(transferMsg).addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        logger.info("数据发送完成，耗时: {} ms", sendEndTime - sendStartTime);
                        
                        if (progressCallback != null) {
                            progressCallback.onProgress(0.8, "等待服务器响应...");
                        }
                    } else {
                        responseFuture.completeExceptionally(channelFuture.cause());
                    }
                });
                
                // 等待响应
                ResponseMessage response = responseFuture.get();
                
                long responseReceivedTime = System.currentTimeMillis();
                metrics.setResponseReceivedTime(responseReceivedTime);
                
                // 设置服务端时间戳
                metrics.setReceiveStartTime(response.getReceiveStartTime());
                metrics.setReceiveEndTime(response.getReceiveEndTime());
                metrics.setDecompressStartTime(response.getDecompressStartTime());
                metrics.setDecompressEndTime(response.getDecompressEndTime());
                
                if (progressCallback != null) {
                    progressCallback.onProgress(1.0, "完成");
                }
                
                logger.info("收到服务器响应 - {}", response.getMessage());
                logger.info("性能指标: {}", metrics);
                
                if (response.isSuccess()) {
                    future.complete(metrics);
                } else {
                    future.completeExceptionally(new IOException("服务器处理失败: " + response.getMessage()));
                }
                
            } catch (Exception e) {
                logger.error("发送文件时发生错误", e);
                future.completeExceptionally(e);
            }
        }).start();
        
        return future;
    }
    
    /**
     * 发送心跳并测量网络延迟
     * @return 往返时间（毫秒），如果失败返回-1
     */
    public CompletableFuture<Long> sendHeartbeat() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        
        if (!connected || channel == null || !channel.isActive()) {
            future.complete(-1L);
            return future;
        }
        
        long sendTime = System.currentTimeMillis();
        HeartbeatMessage heartbeat = new HeartbeatMessage(sendTime);
        
        // 添加临时处理器来接收心跳响应
        CompletableFuture<HeartbeatMessage> responseFuture = new CompletableFuture<>();
        
        // 使用时间戳生成唯一的处理器名称，避免冲突
        String handlerName = "heartbeatResponseHandler-" + sendTime;
        
        channel.pipeline().addLast(handlerName, new SimpleChannelInboundHandler<HeartbeatMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HeartbeatMessage msg) throws Exception {
                responseFuture.complete(msg);
                ctx.pipeline().remove(this);
            }
            
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                responseFuture.completeExceptionally(cause);
                ctx.pipeline().remove(this);
            }
        });
        
        // 发送心跳
        channel.writeAndFlush(heartbeat).addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                responseFuture.completeExceptionally(channelFuture.cause());
            }
        });
        
        // 等待响应并计算延迟
        responseFuture.whenComplete((response, error) -> {
            if (error != null) {
                logger.warn("心跳失败: {}", error.getMessage());
                future.complete(-1L);
            } else {
                long receiveTime = System.currentTimeMillis();
                long latency = receiveTime - sendTime;
                logger.debug("心跳往返时间: {} ms", latency);
                future.complete(latency);
            }
        });
        
        return future;
    }
    
    /**
     * 断开连接
     */
    public void disconnect() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        connected = false;
        logger.info("已断开连接");
    }
    
    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return connected && channel != null && channel.isActive();
    }
    
    /**
     * 进度回调接口
     */
    public interface ProgressCallback {
        void onProgress(double progress, String message);
    }
}
