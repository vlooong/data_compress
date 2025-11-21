package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;

import java.io.IOException;

/**
 * 无压缩算法实现
 * 直接传输原始数据，不进行任何压缩处理
 * 用于建立性能基准，以便与其他压缩算法进行对比
 */
public class NoCompression implements CompressionAlgorithm {
    
    @Override
    public String getName() {
        return "NONE";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 0;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        // 不进行任何压缩，直接返回原始数据
        return data;
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        // 无压缩模式不支持压缩级别，直接返回原始数据
        return data;
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        // 不进行任何解压，直接返回原始数据
        return compressedData;
    }
    
    @Override
    public boolean supportsCustomLevel() {
        // 不支持自定义压缩级别
        return false;
    }
    
    @Override
    public int getDefaultLevel() {
        return 0;
    }
    
    @Override
    public int getMinLevel() {
        return 0;
    }
    
    @Override
    public int getMaxLevel() {
        return 0;
    }
}
