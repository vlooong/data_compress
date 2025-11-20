package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * 心跳消息编码器
 * 将HeartbeatMessage对象编码为字节流
 */
public class HeartbeatMessageEncoder extends MessageToByteEncoder<HeartbeatMessage> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, HeartbeatMessage msg, ByteBuf out) throws Exception {
        // 使用Java序列化
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(msg);
        oos.flush();
        
        byte[] bytes = baos.toByteArray();
        
        // 写入消息类型、长度和数据
        out.writeByte(MessageType.HEARTBEAT);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
