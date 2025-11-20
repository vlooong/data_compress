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
        // 检查是否有足够的字节读取消息头
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }
        
        // 标记当前读取位置
        in.markReaderIndex();
        
        // 读取接收开始时间戳
        long receiveStartTime = in.readLong();
        
        // 读取接收结束时间戳
        long receiveEndTime = in.readLong();
        
        // 读取解压开始时间戳
        long decompressStartTime = in.readLong();
        
        // 读取解压结束时间戳
        long decompressEndTime = in.readLong();
        
        // 读取成功标志
        boolean success = in.readBoolean();
        
        // 读取消息长度
        int messageLength = in.readInt();
        
        // 检查是否有足够的字节读取消息
        if (in.readableBytes() < messageLength) {
            // 重置读取位置，等待更多数据
            in.resetReaderIndex();
            return;
        }
        
        // 读取消息
        byte[] messageBytes = new byte[messageLength];
        in.readBytes(messageBytes);
        String message = new String(messageBytes, "UTF-8");
        
        // 创建ResponseMessage对象并添加到输出列表
        ResponseMessage response = new ResponseMessage(
            receiveStartTime, receiveEndTime,
            decompressStartTime, decompressEndTime,
            success, message
        );
        
        out.add(response);
    }
}
