package com.datacompress.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据压缩测试系统 - 服务端
 * 使用Netty实现高性能的网络服务
 */
public class CompressionServer {
    
    private static final Logger logger = LoggerFactory.getLogger(CompressionServer.class);
    
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    public CompressionServer(int port) {
        this.port = port;
    }
    
    /**
     * 启动服务器
     */
    public void start() throws InterruptedException {
        // Boss线程组用于接受连接
        bossGroup = new NioEventLoopGroup(1);
        // Worker线程组用于处理I/O
        workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            logger.info("服务器启动中，监听端口: {}", port);
            
            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(port).sync();
            
            logger.info("服务器已启动，等待客户端连接...");
            
            // 等待服务器socket关闭
            future.channel().closeFuture().sync();
            
        } finally {
            shutdown();
        }
    }
    
    /**
     * 优雅关闭服务器
     */
    public void shutdown() {
        logger.info("正在关闭服务器...");
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        logger.info("服务器已关闭");
    }
    
    public static void main(String[] args) {
        int port = 8888;
        
        // 从命令行参数获取端口号
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.error("无效的端口号: {}, 使用默认端口 8888", args[0]);
            }
        }
        
        CompressionServer server = new CompressionServer(port);
        
        try {
            server.start();
        } catch (InterruptedException e) {
            logger.error("服务器启动被中断", e);
            Thread.currentThread().interrupt();
        }
    }
}
