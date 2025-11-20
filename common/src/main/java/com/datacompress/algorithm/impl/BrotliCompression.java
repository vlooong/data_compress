package com.datacompress.algorithm.impl;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.decoder.BrotliInputStream;
import com.aayushatharva.brotli4j.encoder.BrotliOutputStream;
import com.aayushatharva.brotli4j.encoder.Encoder;
import com.datacompress.algorithm.CompressionAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Brotli压缩算法实现
 * 使用brotli4j库
 */
public class BrotliCompression implements CompressionAlgorithm {
    
    private static final int DEFAULT_QUALITY = 6;
    
    static {
        // 加载Brotli本地库
        try {
            Brotli4jLoader.ensureAvailability();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Brotli native library", e);
        }
    }
    
    @Override
    public String getName() {
        return "Brotli";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 9;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_QUALITY);
    }
    
    @Override
    public byte[] compress(byte[] data, int quality) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder.Parameters params = new Encoder.Parameters().setQuality(quality);
        try (BrotliOutputStream brotli = new BrotliOutputStream(baos, params)) {
            brotli.write(data);
        }
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (BrotliInputStream brotli = new BrotliInputStream(bais)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = brotli.read(buffer)) > 0) {
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
        return DEFAULT_QUALITY;
    }
    
    @Override
    public int getMinLevel() {
        return 0;
    }
    
    @Override
    public int getMaxLevel() {
        return 11;
    }
}
