package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP压缩算法实现
 * 使用Java内置的GZIP压缩
 */
public class GzipCompression implements CompressionAlgorithm {
    
    private static final int DEFAULT_LEVEL = 6;
    
    @Override
    public String getName() {
        return "GZIP";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 1;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_LEVEL);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos) {
            {
                // 设置压缩级别
                this.def.setLevel(level);
            }
        }) {
            gzip.write(data);
        }
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPInputStream gzip = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
        }
        
        return baos.toByteArray();
    }
    
    @Override
    public boolean supportsCustomLevel() {
        return true;
    }
    
    @Override
    public int getDefaultLevel() {
        return DEFAULT_LEVEL;
    }
    
    @Override
    public int getMinLevel() {
        return 0;
    }
    
    @Override
    public int getMaxLevel() {
        return 9;
    }
}
