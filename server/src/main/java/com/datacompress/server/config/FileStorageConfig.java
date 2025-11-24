package com.datacompress.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 文件存储配置类
 * 负责加载和管理文件存储相关的配置
 */
public class FileStorageConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageConfig.class);
    
    // 默认配置值
    private static final String DEFAULT_DIRECTORY = "decompressed_files";
    private static final boolean DEFAULT_SAVE_ENABLED = true;
    
    // 配置文件路径
    private static final String CONFIG_FILE = "server.properties";
    
    // 配置键
    private static final String KEY_DIRECTORY = "decompressed.files.directory";
    private static final String KEY_SAVE_ENABLED = "decompressed.files.save.enabled";
    
    private Path storageDirectory;
    private boolean saveEnabled;
    
    /**
     * 构造函数，加载配置
     */
    public FileStorageConfig() {
        loadConfiguration();
        if (saveEnabled) {
            ensureDirectoryExists();
        }
    }
    
    /**
     * 从配置文件加载配置
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("成功加载配置文件: {}", CONFIG_FILE);
            } else {
                logger.warn("配置文件 {} 不存在，使用默认配置", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("读取配置文件失败，使用默认配置", e);
        }
        
        // 读取保存目录配置
        String directoryPath = properties.getProperty(KEY_DIRECTORY, DEFAULT_DIRECTORY);
        storageDirectory = Paths.get(directoryPath);
        
        // 读取是否启用保存功能
        saveEnabled = Boolean.parseBoolean(
            properties.getProperty(KEY_SAVE_ENABLED, String.valueOf(DEFAULT_SAVE_ENABLED))
        );
        
        logger.info("文件存储配置 - 保存目录: {}, 启用状态: {}", 
                    storageDirectory.toAbsolutePath(), saveEnabled);
    }
    
    /**
     * 确保存储目录存在
     */
    private void ensureDirectoryExists() {
        try {
            if (!Files.exists(storageDirectory)) {
                Files.createDirectories(storageDirectory);
                logger.info("创建存储目录: {}", storageDirectory.toAbsolutePath());
            } else {
                logger.info("存储目录已存在: {}", storageDirectory.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("创建存储目录失败: {}", storageDirectory.toAbsolutePath(), e);
            saveEnabled = false; // 如果无法创建目录，禁用保存功能
        }
    }
    
    /**
     * 获取存储目录
     */
    public Path getStorageDirectory() {
        return storageDirectory;
    }
    
    /**
     * 检查是否启用文件保存
     */
    public boolean isSaveEnabled() {
        return saveEnabled;
    }
}
