package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;
import com.github.luben.zstd.Zstd;

import java.io.IOException;

/**
 * Zstd压缩算法实现
 * 使用zstd-jni库
 */
public class ZstdCompression implements CompressionAlgorithm {
    
    private static final int COMPRESSION_LEVEL = 3; // 默认压缩级别
    
    @Override
    public String getName() {
        return "Zstd";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 7;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, COMPRESSION_LEVEL);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        try {
            return Zstd.compress(data, level);
        } catch (Exception e) {
            throw new IOException("Failed to compress with Zstd", e);
        }
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        try {
            long originalSize = Zstd.decompressedSize(compressedData);
            return Zstd.decompress(compressedData, (int) originalSize);
        } catch (Exception e) {
            throw new IOException("Failed to decompress with Zstd", e);
        }
    }
    
    @Override
    public boolean supportsCustomLevel() {
        return true;
    }
    
    @Override
    public int getDefaultLevel() {
        return COMPRESSION_LEVEL;
    }
    
    @Override
    public int getMinLevel() {
        return 1;
    }
    
    @Override
    public int getMaxLevel() {
        return 22;
    }
}
