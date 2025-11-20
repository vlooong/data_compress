package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZIP压缩算法实现
 * 使用Java内置的ZIP压缩
 */
public class ZipCompression implements CompressionAlgorithm {
    
    private static final int DEFAULT_LEVEL = 6;
    
    @Override
    public String getName() {
        return "ZIP";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 3;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_LEVEL);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(baos)) {
            zip.setLevel(level);
            ZipEntry entry = new ZipEntry("data");
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
        }
        return baos.toByteArray();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (ZipInputStream zip = new ZipInputStream(bais)) {
            ZipEntry entry = zip.getNextEntry();
            if (entry != null) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = zip.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                zip.closeEntry();
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
