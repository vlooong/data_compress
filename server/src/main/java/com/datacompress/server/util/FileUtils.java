package com.datacompress.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 文件工具类
 * 提供文件操作相关的工具方法
 */
public class FileUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    
    /**
     * 生成解压文件名
     * 如果提供了原始文件名，使用 {timestamp}_{originalFileName} 格式
     * 否则使用 decompressed_{timestamp}_{algorithmName}.dat 格式
     * 
     * @param originalFileName 原始文件名（可能为null或空）
     * @param algorithmName 压缩算法名称
     * @param timestamp 时间戳
     * @return 文件名
     */
    public static String generateFileName(String originalFileName, String algorithmName, long timestamp) {
        if (originalFileName != null && !originalFileName.trim().isEmpty()) {
            // 使用原始文件名，添加时间戳前缀避免冲突
            return String.format("%d_%s", timestamp, originalFileName);
        } else {
            // 降级方案：使用算法名称生成文件名
            return String.format("decompressed_%d_%s.dat", timestamp, algorithmName);
        }
    }
    
    /**
     * 保存文件到指定目录
     * 
     * @param directory 目标目录
     * @param fileName 文件名
     * @param data 文件数据
     * @return 保存的文件路径，如果保存失败返回null
     */
    public static Path saveFile(Path directory, String fileName, byte[] data) {
        try {
            Path filePath = directory.resolve(fileName);
            Files.write(filePath, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("文件保存成功: {} ({} bytes)", filePath.toAbsolutePath(), data.length);
            return filePath;
        } catch (IOException e) {
            logger.error("保存文件失败: {}/{}", directory, fileName, e);
            return null;
        }
    }
    
    /**
     * 确保目录存在，如果不存在则创建
     * 
     * @param directory 目录路径
     * @return 目录是否存在或成功创建
     */
    public static boolean ensureDirectoryExists(Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                logger.info("目录创建成功: {}", directory.toAbsolutePath());
            }
            return true;
        } catch (IOException e) {
            logger.error("创建目录失败: {}", directory, e);
            return false;
        }
    }
}
