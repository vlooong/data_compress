package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * BZIP2压缩算法实现
 * 使用Apache Commons Compress库
 */
public class Bzip2Compression implements CompressionAlgorithm {
    
    private static final int DEFAULT_BLOCK_SIZE = 9;
    
    @Override
    public String getName() {
        return "BZIP2";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 4;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_BLOCK_SIZE);
    }
    
    @Override
    public byte[] compress(byte[] data, int blockSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BZip2CompressorOutputStream bzip2 = new BZip2CompressorOutputStream(baos, blockSize)) {
            bzip2.write(data);
        }
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(bais)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = bzip2.read(buffer)) > 0) {
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
        return DEFAULT_BLOCK_SIZE;
    }
    
    @Override
    public int getMinLevel() {
        return 1;
    }
    
    @Override
    public int getMaxLevel() {
        return 9;
    }
}
