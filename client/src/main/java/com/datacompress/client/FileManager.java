package com.datacompress.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 文件管理类
 * 处理文件读取操作
 */
public class FileManager {
    
    /**
     * 读取文件内容
     * @param file 文件对象
     * @return 文件字节数组
     * @throws IOException 读取文件时发生的异常
     */
    public static byte[] readFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("文件不存在");
        }
        
        if (!file.isFile()) {
            throw new IOException("不是一个有效的文件");
        }
        
        return Files.readAllBytes(file.toPath());
    }
    
    /**
     * 格式化文件大小
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
