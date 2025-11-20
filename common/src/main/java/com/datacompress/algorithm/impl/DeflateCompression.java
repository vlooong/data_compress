package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * DEFLATE压缩算法实现
 * 使用Java内置的Deflater/Inflater
 */
public class DeflateCompression implements CompressionAlgorithm {
    
    private static final int DEFAULT_LEVEL = 6;
    
    @Override
    public String getName() {
        return "DEFLATE";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 2;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_LEVEL);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        Deflater deflater = new Deflater(level);
        deflater.setInput(data);
        deflater.finish();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            baos.write(buffer, 0, count);
        }
        
        deflater.end();
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                baos.write(buffer, 0, count);
            }
        } catch (Exception e) {
            throw new IOException("Failed to decompress data", e);
        } finally {
            inflater.end();
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
