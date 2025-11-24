package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * TransferMessage编码器
 * 将TransferMessage对象编码为字节流发送
 */
public class TransferMessageEncoder extends MessageToByteEncoder<TransferMessage> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, TransferMessage msg, ByteBuf out) throws Exception {
        // 写入消息类型
        out.writeByte(MessageType.TRANSFER);
        
        // 写入算法ID
        out.writeByte(msg.getAlgorithmId());
        
        // 写入原始大小
        out.writeLong(msg.getOriginalSize());
        
        // 写入压缩后大小
        out.writeLong(msg.getCompressedSize());
        
        // 写入压缩开始时间戳
        out.writeLong(msg.getCompressStartTime());
        
        // 写入压缩结束时间戳
        out.writeLong(msg.getCompressEndTime());
        
        // 写入发送开始时间戳
        out.writeLong(msg.getSendStartTime());
        
        // 写入文件名
        byte[] fileNameBytes = msg.getFileName() != null ? 
            msg.getFileName().getBytes(StandardCharsets.UTF_8) : new byte[0];
        out.writeInt(fileNameBytes.length);
        if (fileNameBytes.length > 0) {
            out.writeBytes(fileNameBytes);
        }
        
        // 写入压缩数据长度
        out.writeInt(msg.getCompressedData().length);
        
        // 写入压缩数据
        out.writeBytes(msg.getCompressedData());
    }
}
