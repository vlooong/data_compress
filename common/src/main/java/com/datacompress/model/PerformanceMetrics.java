package com.datacompress.model;

/**
 * 性能指标数据模型
 * 记录压缩传输过程中的所有性能指标
 */
public class PerformanceMetrics {
    
    // 文件和数据大小
    private long originalSize;           // 原始文件大小（字节）
    private long compressedSize;         // 压缩后大小（字节）
    
    // 时间戳
    private long compressStartTime;      // 压缩开始时间
    private long compressEndTime;        // 压缩结束时间
    private long sendStartTime;          // 发送开始时间
    private long sendEndTime;            // 发送结束时间
    private long receiveStartTime;       // 接收开始时间（服务端）
    private long receiveEndTime;         // 接收结束时间（服务端）
    private long decompressStartTime;    // 解压开始时间（服务端）
    private long decompressEndTime;      // 解压结束时间（服务端）
    private long responseReceivedTime;   // 收到响应时间（客户端）
    
    // 算法信息
    private String algorithmName;
    private byte algorithmId;
    
    // Getters and Setters
    
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
    
    public long getSendEndTime() {
        return sendEndTime;
    }
    
    public void setSendEndTime(long sendEndTime) {
        this.sendEndTime = sendEndTime;
    }
    
    public long getReceiveStartTime() {
        return receiveStartTime;
    }
    
    public void setReceiveStartTime(long receiveStartTime) {
        this.receiveStartTime = receiveStartTime;
    }
    
    public long getReceiveEndTime() {
        return receiveEndTime;
    }
    
    public void setReceiveEndTime(long receiveEndTime) {
        this.receiveEndTime = receiveEndTime;
    }
    
    public long getDecompressStartTime() {
        return decompressStartTime;
    }
    
    public void setDecompressStartTime(long decompressStartTime) {
        this.decompressStartTime = decompressStartTime;
    }
    
    public long getDecompressEndTime() {
        return decompressEndTime;
    }
    
    public void setDecompressEndTime(long decompressEndTime) {
        this.decompressEndTime = decompressEndTime;
    }
    
    public long getResponseReceivedTime() {
        return responseReceivedTime;
    }
    
    public void setResponseReceivedTime(long responseReceivedTime) {
        this.responseReceivedTime = responseReceivedTime;
    }
    
    public String getAlgorithmName() {
        return algorithmName;
    }
    
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }
    
    public byte getAlgorithmId() {
        return algorithmId;
    }
    
    public void setAlgorithmId(byte algorithmId) {
        this.algorithmId = algorithmId;
    }
    
    // 计算性能指标的方法
    
    /**
     * 计算压缩比
     * @return 压缩比（0-1之间的小数）
     */
    public double getCompressionRatio() {
        if (originalSize == 0) return 0;
        return (double) compressedSize / originalSize;
    }
    
    /**
     * 计算压缩耗时（毫秒）
     * @return 压缩耗时
     */
    public long getCompressionTime() {
        return compressEndTime - compressStartTime;
    }
    
    /**
     * 计算发送耗时（毫秒）
     * @return 发送耗时
     */
    public long getSendTime() {
        return sendEndTime - sendStartTime;
    }
    
    /**
     * 计算接收耗时（毫秒）
     * @return 接收耗时
     */
    public long getReceiveTime() {
        return receiveEndTime - receiveStartTime;
    }
    
    /**
     * 计算解压耗时（毫秒）
     * @return 解压耗时
     */
    public long getDecompressionTime() {
        return decompressEndTime - decompressStartTime;
    }
    
    /**
     * 计算传播时延（毫秒）
     * 近似值：发送结束到接收开始的时间差
     * @return 传播时延
     */
    public long getPropagationDelay() {
        if (receiveStartTime == 0 || sendEndTime == 0) return 0;
        return Math.max(0, receiveStartTime - sendEndTime);
    }
    
    /**
     * 计算排队时延（毫秒）
     * 接收完成到解压开始的时间差
     * @return 排队时延
     */
    public long getQueuingDelay() {
        if (decompressStartTime == 0 || receiveEndTime == 0) return 0;
        return Math.max(0, decompressStartTime - receiveEndTime);
    }
    
    /**
     * 计算总往返时间（毫秒）
     * @return 总往返时间
     */
    public long getTotalRoundTripTime() {
        return responseReceivedTime - compressStartTime;
    }
    
    @Override
    public String toString() {
        return String.format(
            "PerformanceMetrics[algorithm=%s, originalSize=%d, compressedSize=%d, " +
            "ratio=%.2f%%, compressTime=%dms, sendTime=%dms, decompressTime=%dms, totalTime=%dms]",
            algorithmName, originalSize, compressedSize, 
            getCompressionRatio() * 100, getCompressionTime(), 
            getSendTime(), getDecompressionTime(), getTotalRoundTripTime()
        );
    }
}
