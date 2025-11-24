package com.datacompress.server;

import com.datacompress.algorithm.CompressionAlgorithm;
import com.datacompress.algorithm.CompressionFactory;
import com.datacompress.protocol.ResponseMessage;
import com.datacompress.protocol.TransferMessage;
import com.datacompress.server.config.FileStorageConfig;
import com.datacompress.server.util.FileUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Netty业务处理器
 * 处理客户端发送的压缩数据，进行解压并返回响应
 */
public class CompressionServerHandler extends ChannelInboundHandlerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(CompressionServerHandler.class);
    private final FileStorageConfig fileStorageConfig;
    
    public CompressionServerHandler() {
        this.fileStorageConfig = new FileStorageConfig();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端已连接: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端已断开: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 处理心跳消息
        if (msg instanceof com.datacompress.protocol.HeartbeatMessage) {
            com.datacompress.protocol.HeartbeatMessage heartbeat = (com.datacompress.protocol.HeartbeatMessage) msg;
            logger.debug("收到心跳消息，时间戳: {}", heartbeat.getTimestamp());
            // 直接回传心跳消息
            ctx.writeAndFlush(heartbeat);
            return;
        }
        
        if (!(msg instanceof TransferMessage)) {
            logger.warn("收到未知类型的消息: {}", msg.getClass().getName());
            return;
        }
        
        TransferMessage transferMsg = (TransferMessage) msg;
        
        // 记录接收开始时间
        long receiveStartTime = System.currentTimeMillis();
        
        logger.info("收到传输消息 - 算法ID: {}, 原始大小: {} bytes, 压缩后大小: {} bytes",
                transferMsg.getAlgorithmId(),
                transferMsg.getOriginalSize(),
                transferMsg.getCompressedSize());
        
        // 记录接收完成时间（消息已完整接收）
        long receiveEndTime = System.currentTimeMillis();
        
        ResponseMessage response;
        
        try {
            // 获取对应的压缩算法
            CompressionAlgorithm algorithm = CompressionFactory.getAlgorithm(transferMsg.getAlgorithmId());
            
            if (algorithm == null) {
                throw new IllegalArgumentException("不支持的压缩算法ID: " + transferMsg.getAlgorithmId());
            }
            
            logger.info("使用算法: {}", algorithm.getName());
            
            // 记录解压开始时间
            long decompressStartTime = System.currentTimeMillis();
            
            // 解压数据
            byte[] decompressedData = algorithm.decompress(transferMsg.getCompressedData());
            
            // 记录解压结束时间
            long decompressEndTime = System.currentTimeMillis();
            
            // 验证解压后的数据大小是否匹配
            if (decompressedData.length != transferMsg.getOriginalSize()) {
                logger.warn("警告: 解压后大小({}) 与原始大小({}) 不匹配",
                        decompressedData.length, transferMsg.getOriginalSize());
            }
            
            logger.info("解压成功 - 解压后大小: {} bytes, 解压耗时: {} ms",
                    decompressedData.length,
                    decompressEndTime - decompressStartTime);
            
            // 保存解压后的文件到磁盘（使用原始文件名）
            saveDecompressedFile(decompressedData, transferMsg.getFileName(), 
                                algorithm.getName(), receiveStartTime);
            
            // 创建成功响应
            response = new ResponseMessage(
                    receiveStartTime,
                    receiveEndTime,
                    decompressStartTime,
                    decompressEndTime,
                    true,
                    "解压成功"
            );
            
        } catch (Exception e) {
            logger.error("处理数据时发生错误", e);
            
            // 创建失败响应
            response = new ResponseMessage(
                    receiveStartTime,
                    System.currentTimeMillis(),
                    0,
                    0,
                    false,
                    "解压失败: " + e.getMessage()
            );
        }
        
        // 发送响应
        ctx.writeAndFlush(response).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("响应已发送");
            } else {
                logger.error("发送响应失败", future.cause());
            }
        });
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("发生异常: {}", cause.getMessage(), cause);
        ctx.close();
    }
    
    /**
     * 保存解压后的文件
     * 
     * @param data 解压后的数据
     * @param originalFileName 原始文件名（可能为null或空）
     * @param algorithmName 压缩算法名称
     * @param timestamp 时间戳
     */
    private void saveDecompressedFile(byte[] data, String originalFileName, 
                                      String algorithmName, long timestamp) {
        if (!fileStorageConfig.isSaveEnabled()) {
            logger.debug("文件保存功能已禁用，跳过保存");
            return;
        }
        
        try {
            String fileName = FileUtils.generateFileName(originalFileName, algorithmName, timestamp);
            Path savedPath = FileUtils.saveFile(
                fileStorageConfig.getStorageDirectory(), 
                fileName, 
                data
            );
            
            if (savedPath != null) {
                logger.info("解压文件已保存: {}", savedPath.toAbsolutePath());
            }
        } catch (Exception e) {
            // 文件保存失败不应影响响应发送
            logger.error("保存解压文件时发生错误", e);
        }
    }
}
