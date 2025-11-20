package com.datacompress.protocol;

import java.io.Serializable;

/**
 * 服务端返回给客户端的响应消息
 * 包含服务端的处理时间戳和状态信息
 */
public class ResponseMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long receiveStartTime;       // 接收开始时间戳
    private long receiveEndTime;         // 接收完成时间戳
    private long decompressStartTime;    // 解压开始时间戳
    private long decompressEndTime;      // 解压完成时间戳
    private boolean success;             // 处理是否成功
    private String message;              // 消息（成功或错误信息）
    
    public ResponseMessage() {
    }
    
    public ResponseMessage(long receiveStartTime, long receiveEndTime,
                          long decompressStartTime, long decompressEndTime,
                          boolean success, String message) {
        this.receiveStartTime = receiveStartTime;
        this.receiveEndTime = receiveEndTime;
        this.decompressStartTime = decompressStartTime;
        this.decompressEndTime = decompressEndTime;
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    
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
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
