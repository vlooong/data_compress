package com.datacompress.server;

import com.datacompress.protocol.ResponseMessageEncoder;
import com.datacompress.protocol.TransferMessageDecoder;
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
        
        // 添加TransferMessage解码器（接收客户端消息）
        pipeline.addLast("transferMessageDecoder", new TransferMessageDecoder());
        
        // 添加ResponseMessage编码器（发送响应给客户端）
        pipeline.addLast("responseMessageEncoder", new ResponseMessageEncoder());
        
        // 添加业务处理器
        pipeline.addLast("serverHandler", new CompressionServerHandler());
    }
}
