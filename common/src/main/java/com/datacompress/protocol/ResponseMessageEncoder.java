package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ResponseMessage编码器
 * 将ResponseMessage对象编码为字节流发送
 */
public class ResponseMessageEncoder extends MessageToByteEncoder<ResponseMessage> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage msg, ByteBuf out) throws Exception {
        // 写入接收开始时间戳
        out.writeLong(msg.getReceiveStartTime());
        
        // 写入接收结束时间戳
        out.writeLong(msg.getReceiveEndTime());
        
        // 写入解压开始时间戳
        out.writeLong(msg.getDecompressStartTime());
        
        // 写入解压结束时间戳
        out.writeLong(msg.getDecompressEndTime());
        
        // 写入成功标志
        out.writeBoolean(msg.isSuccess());
        
        // 写入消息
        String message = msg.getMessage() != null ? msg.getMessage() : "";
        byte[] messageBytes = message.getBytes("UTF-8");
        out.writeInt(messageBytes.length);
        out.writeBytes(messageBytes);
    }
}
