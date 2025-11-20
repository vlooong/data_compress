package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * 心跳消息解码器
 * 将字节流解码为HeartbeatMessage对象
 */
public class HeartbeatMessageDecoder extends ByteToMessageDecoder {
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节读取类型和长度字段
        if (in.readableBytes() < 5) { // 1 byte type + 4 bytes length
            return;
        }
        
        // 标记当前读位置
        in.markReaderIndex();
        
        // 读取消息类型
        byte messageType = in.readByte();
        
        // 如果不是心跳消息，重置读位置并跳过
        if (messageType != MessageType.HEARTBEAT) {
            in.resetReaderIndex();
            return;
        }
        
        // 读取消息长度
        int length = in.readInt();
        
        // 检查是否有足够的字节读取完整消息
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        
        // 读取消息字节
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        
        // 反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        HeartbeatMessage msg = (HeartbeatMessage) ois.readObject();
        
        out.add(msg);
    }
}
