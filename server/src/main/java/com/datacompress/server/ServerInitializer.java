package com.datacompress.server;

import com.datacompress.protocol.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Netty Channel初始化器
 * 配置处理器链（Pipeline）
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        // 使用统一消息解码器（处理所有类型的消息）
        pipeline.addLast("unifiedDecoder", new UnifiedMessageDecoder());
        
        // 添加编码器
        pipeline.addLast("heartbeatEncoder", new HeartbeatMessageEncoder());
        pipeline.addLast("responseMessageEncoder", new ResponseMessageEncoder());
        
        // 添加业务处理器
        pipeline.addLast("serverHandler", new CompressionServerHandler());
    }
}

