package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 统一消息解码器
 * 根据消息类型分发到相应的解码逻辑
 */
public class UnifiedMessageDecoder extends ByteToMessageDecoder {
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(UnifiedMessageDecoder.class);
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 至少需要1个字节来读取消息类型
        if (in.readableBytes() < 1) {
            return;
        }
        
        // 标记当前读位置
        in.markReaderIndex();
        
        // 读取消息类型
        byte messageType = in.readByte();
        
        // 根据消息类型分发
        switch (messageType) {
            case MessageType.HEARTBEAT:
                decodeHeartbeat(in, out);
                break;
            case MessageType.TRANSFER:
                decodeTransfer(in, out);
                break;
            case MessageType.RESPONSE:
                decodeResponse(in, out);
                break;
            default:
                // 未知消息类型，跳过这个字节
                logger.warn("未知的消息类型: {}", messageType);
                break;
        }
    }
    
    private void decodeHeartbeat(ByteBuf in, List<Object> out) throws Exception {
        // 检查是否有足够的字节 (4 bytes length)
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return;
        }
        
        // 不要再mark了，直接读取
        int length = in.readInt();
        
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        HeartbeatMessage msg = (HeartbeatMessage) ois.readObject();
        
        out.add(msg);
    }
    
    private void decodeTransfer(ByteBuf in, List<Object> out) throws Exception {
        // 需要: 1字节算法ID + 5*8字节(sizes+timestamps) + 4字节文件名长度 = 49字节
        if (in.readableBytes() < 49) {
            in.resetReaderIndex();
            return;
        }
        
        // 不要再mark了，直接读取（已经在主decode中mark过了）
        byte algorithmId = in.readByte();
        long originalSize = in.readLong();
        long compressedSize = in.readLong();
        long compressStartTime = in.readLong();
        long compressEndTime = in.readLong();
        long sendStartTime = in.readLong();
        
        // 读取文件名长度
        int fileNameLength = in.readInt();
        
        // 检查是否有足够的字节读取文件名
        if (in.readableBytes() < fileNameLength + 4) { // +4 for data length field
            in.resetReaderIndex();
            return;
        }
        
        // 读取文件名
        String fileName = "";
        if (fileNameLength > 0) {
            byte[] fileNameBytes = new byte[fileNameLength];
            in.readBytes(fileNameBytes);
            fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
        }
        
        // 读取压缩数据长度
        int dataLength = in.readInt();
        
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        
        byte[] compressedData = new byte[dataLength];
        in.readBytes(compressedData);
        
        TransferMessage message = new TransferMessage(
                algorithmId, originalSize, compressedSize,
                compressStartTime, compressEndTime, sendStartTime,
                fileName, compressedData
        );
        
        out.add(message);
    }
    
    private void decodeResponse(ByteBuf in, List<Object> out) throws Exception {
        // 需要: 4*8字节(时间戳) + 1字节(boolean) + 4字节(消息长度) = 37字节
        if (in.readableBytes() < 37) {
            in.resetReaderIndex();
            return;
        }
        
        // 不要再mark了，直接读取
        long receiveStartTime = in.readLong();
        long receiveEndTime = in.readLong();
        long decompressStartTime = in.readLong();
        long decompressEndTime = in.readLong();
        boolean success = in.readBoolean();
        int messageLength = in.readInt();
        
        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }
        
        byte[] messageBytes = new byte[messageLength];
        in.readBytes(messageBytes);
        String message = new String(messageBytes, "UTF-8");
        
        ResponseMessage response = new ResponseMessage(
            receiveStartTime, receiveEndTime,
            decompressStartTime, decompressEndTime,
            success, message
        );
        
        out.add(response);
    }
}
