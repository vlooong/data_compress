package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZInputStream;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * LZMA压缩算法实现
 * 使用XZ for Java库
 */
public class LzmaCompression implements CompressionAlgorithm {
    
    private static final int DEFAULT_PRESET = 6;
    
    @Override
    public String getName() {
        return "LZMA";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 5;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_PRESET);
    }
    
    @Override
    public byte[] compress(byte[] data, int preset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZOutputStream xz = new XZOutputStream(baos, new LZMA2Options(preset))) {
            xz.write(data);
        }
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (XZInputStream xz = new XZInputStream(bais)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = xz.read(buffer)) > 0) {
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
        return DEFAULT_PRESET;
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
