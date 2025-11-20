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
        // 写入消息类型
        out.writeByte(MessageType.RESPONSE);
        
        // 写入时间戳
        out.writeLong(msg.getReceiveStartTime());
        out.writeLong(msg.getReceiveEndTime());
        out.writeLong(msg.getDecompressStartTime());
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
