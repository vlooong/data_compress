package com.datacompress.protocol;

import java.io.Serializable;

/**
 * 客户端发送给服务端的传输消息
 * 包含压缩算法信息、时间戳和压缩后的数据
 */
public class TransferMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private byte algorithmId;            // 压缩算法ID (1-9)
    private long originalSize;           // 原始文件大小
    private long compressedSize;         // 压缩后数据大小
    private long compressStartTime;      // 压缩开始时间戳
    private long compressEndTime;        // 压缩结束时间戳
    private long sendStartTime;          // 发送开始时间戳
    private byte[] compressedData;       // 压缩后的数据
    
    public TransferMessage() {
    }
    
    public TransferMessage(byte algorithmId, long originalSize, long compressedSize,
                          long compressStartTime, long compressEndTime, 
                          long sendStartTime, byte[] compressedData) {
        this.algorithmId = algorithmId;
        this.originalSize = originalSize;
        this.compressedSize = compressedSize;
        this.compressStartTime = compressStartTime;
        this.compressEndTime = compressEndTime;
        this.sendStartTime = sendStartTime;
        this.compressedData = compressedData;
    }
    
    // Getters and Setters
    
    public byte getAlgorithmId() {
        return algorithmId;
    }
    
    public void setAlgorithmId(byte algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    public long getOriginalSize() {
        return originalSize;
    }
    
    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }
    
    public long getCompressedSize() {
        return compressedSize;
    }
    
    public void setCompressedSize(long compressedSize) {
        this.compressedSize = compressedSize;
    }
    
    public long getCompressStartTime() {
        return compressStartTime;
    }
    
    public void setCompressStartTime(long compressStartTime) {
        this.compressStartTime = compressStartTime;
    }
    
    public long getCompressEndTime() {
        return compressEndTime;
    }
    
    public void setCompressEndTime(long compressEndTime) {
        this.compressEndTime = compressEndTime;
    }
    
    public long getSendStartTime() {
        return sendStartTime;
    }
    
    public void setSendStartTime(long sendStartTime) {
        this.sendStartTime = sendStartTime;
    }
    
    public byte[] getCompressedData() {
        return compressedData;
    }
    
    public void setCompressedData(byte[] compressedData) {
        this.compressedData = compressedData;
    }
}
