package com.datacompress.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * TransferMessage解码器
 * 将接收到的字节流解码为TransferMessage对象
 */
public class TransferMessageDecoder extends ByteToMessageDecoder {

    private static final int HEADER_SIZE = 1 + 8 + 8 + 8 + 8 + 8 + 8 + 4; // 53 bytes

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 需要至少: 1字节类型 + 1字节算法ID + 6*8字节(sizes+timestamps) + 4字节文件名长度 = 59字节
        if (in.readableBytes() < 59) {
            return;
        }

        // 标记读位置
        in.markReaderIndex();

        // 读取并检查消息类型
        byte messageType = in.readByte();
        if (messageType != MessageType.TRANSFER) {
            in.resetReaderIndex();
            return;
        }

        // 读取消息头信息
        byte algorithmId = in.readByte();
        long originalSize = in.readLong();
        long compressedSize = in.readLong();
        long compressStartTime = in.readLong();
        long compressEndTime = in.readLong();
        long sendStartTime = in.readLong();
        long sendEndTime = in.readLong();
        
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

        // 检查是否有足够的字节读取压缩数据
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 读取压缩数据
        byte[] compressedData = new byte[dataLength];
        in.readBytes(compressedData);

        // 创建TransferMessage对象
        TransferMessage message = new TransferMessage(
                algorithmId,
                originalSize,
                compressedSize,
                compressStartTime,
                compressEndTime,
                sendStartTime,
                sendEndTime,
                fileName,
                compressedData
        );

        out.add(message);
    }
}
