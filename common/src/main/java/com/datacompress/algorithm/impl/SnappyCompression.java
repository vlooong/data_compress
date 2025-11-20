package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * SNAPPY压缩算法实现
 * 使用snappy-java库
 * 注意：SNAPPY不支持自定义压缩级别
 */
public class SnappyCompression implements CompressionAlgorithm {
    
    @Override
    public String getName() {
        return "SNAPPY";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 8;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return Snappy.compress(data);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        // SNAPPY不支持压缩级别，忽略level参数
        return compress(data);
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        return Snappy.uncompress(compressedData);
    }
    
    @Override
    public boolean supportsCustomLevel() {
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
