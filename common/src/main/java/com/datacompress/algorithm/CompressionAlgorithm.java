package com.datacompress.algorithm;

import java.io.IOException;

/**
 * 压缩算法统一接口
 * 所有压缩算法实现都需要遵循此接口
 */
public interface CompressionAlgorithm {
    
    /**
     * 获取算法名称
     * @return 算法名称
     */
    String getName();
    
    /**
     * 获取算法唯一标识符
     * @return 算法ID (1-9)
     */
    byte getAlgorithmId();
    
    /**
     * 压缩数据（使用默认压缩级别）
     * @param data 原始数据
     * @return 压缩后的数据
     * @throws IOException 压缩过程中发生的异常
     */
    byte[] compress(byte[] data) throws IOException;
    
    /**
     * 压缩数据（使用指定压缩级别）
     * @param data 原始数据
     * @param level 压缩级别
     * @return 压缩后的数据
     * @throws IOException 压缩过程中发生的异常
     */
    byte[] compress(byte[] data, int level) throws IOException;
    
    /**
     * 解压数据
     * @param compressedData 压缩后的数据
     * @return 原始数据
     * @throws IOException 解压过程中发生的异常
     */
    byte[] decompress(byte[] compressedData) throws IOException;
    
    /**
     * 是否支持自定义压缩级别
     * @return true表示支持，false表示不支持
     */
    boolean supportsCustomLevel();
    
    /**
     * 获取默认压缩级别
     * @return 默认压缩级别
     */
    int getDefaultLevel();
    
    /**
     * 获取最小压缩级别
     * @return 最小压缩级别
     */
    int getMinLevel();
    
    /**
     * 获取最大压缩级别
     * @return 最大压缩级别
     */
    int getMaxLevel();
}
