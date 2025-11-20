package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * TransferMessage解码器
 * 将接收到的字节流解码为TransferMessage对象
 */
public class TransferMessageDecoder extends ByteToMessageDecoder {
    
    private static final int HEADER_SIZE = 1 + 8 + 8 + 8 + 8 + 8 + 4; // 45 bytes
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节读取消息头
        if (in.readableBytes() < HEADER_SIZE) {
            return;
        }
        
        // 标记当前读取位置
        in.markReaderIndex();
        
        // 读取算法ID
        byte algorithmId = in.readByte();
        
        // 读取原始大小
        long originalSize = in.readLong();
        
        // 读取压缩后大小
        long compressedSize = in.readLong();
        
        // 读取压缩开始时间戳
        long compressStartTime = in.readLong();
        
        // 读取压缩结束时间戳
        long compressEndTime = in.readLong();
        
        // 读取发送开始时间戳
        long sendStartTime = in.readLong();
        
        // 读取压缩数据长度
        int dataLength = in.readInt();
        
        // 检查是否有足够的字节读取压缩数据
        if (in.readableBytes() < dataLength) {
            // 重置读取位置，等待更多数据
            in.resetReaderIndex();
            return;
        }
        
        // 读取压缩数据
        byte[] compressedData = new byte[dataLength];
        in.readBytes(compressedData);
        
        // 创建TransferMessage对象并添加到输出列表
        TransferMessage message = new TransferMessage(
            algorithmId, originalSize, compressedSize,
            compressStartTime, compressEndTime, sendStartTime,
            compressedData
        );
        
        out.add(message);
    }
}
