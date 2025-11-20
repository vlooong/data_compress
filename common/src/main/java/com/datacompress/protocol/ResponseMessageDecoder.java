package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ResponseMessage解码器
 * 将接收到的字节流解码为ResponseMessage对象
 */
public class ResponseMessageDecoder extends ByteToMessageDecoder {
    
    private static final int HEADER_SIZE = 8 + 8 + 8 + 8 + 1 + 4; // 37 bytes
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节读取类型和消息头 (1 + 37 = 38 bytes)
        if (in.readableBytes() < 38) {
            return;
        }
        
        // 标记当前读取位置
        in.markReaderIndex();
        
        // 读取并检查消息类型
        byte messageType = in.readByte();
        if (messageType != MessageType.RESPONSE) {
            in.resetReaderIndex();
            return;
        }
        
        // 读取时间戳
        long receiveStartTime = in.readLong();
        long receiveEndTime = in.readLong();
        long decompressStartTime = in.readLong();
        long decompressEndTime = in.readLong();
        
        // 读取成功标志
        boolean success = in.readBoolean();
        
        // 读取消息长度
        int messageLength = in.readInt();
        
        // 检查是否有足够的字节读取消息
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }
        
        // 读取消息
        byte[] messageBytes = new byte[messageLength];
        in.readBytes(messageBytes);
        String message = new String(messageBytes, "UTF-8");
        
        // 创建ResponseMessage对象
        ResponseMessage response = new ResponseMessage(
            receiveStartTime, receiveEndTime,
            decompressStartTime, decompressEndTime,
            success, message
        );
        
        out.add(response);
    }
}
