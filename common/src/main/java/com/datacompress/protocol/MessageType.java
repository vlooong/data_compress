package com.datacompress.protocol;

/**
 * 消息类型常量
 */
public class MessageType {
    public static final byte HEARTBEAT = 0x01;
    public static final byte TRANSFER = 0x02;
    public static final byte RESPONSE = 0x03;
}
