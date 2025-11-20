package com.datacompress.protocol;

import java.io.Serializable;

/**
 * 心跳消息
 * 用于客户端与服务端之间的连接状态检测和网络延迟测量
 */
public class HeartbeatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long timestamp;  // 发送时间戳（毫秒）
    
    public HeartbeatMessage() {
    }
    
    public HeartbeatMessage(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
