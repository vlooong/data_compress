package com.datacompress.algorithm.impl;

import com.datacompress.algorithm.CompressionAlgorithm;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * LZ4压缩算法实现
 * 使用lz4-java库
 * 注意：LZ4的级别选择是fast/high，这里用level=0表示fast，level>0表示high
 */
public class Lz4Compression implements CompressionAlgorithm {
    
    private static final LZ4Factory factory = LZ4Factory.fastestInstance();
    private static final int DEFAULT_LEVEL = 0; // 0=fast, >0=high
    
    @Override
    public String getName() {
        return "LZ4";
    }
    
    @Override
    public byte getAlgorithmId() {
        return 6;
    }
    
    @Override
    public byte[] compress(byte[] data) throws IOException {
        return compress(data, DEFAULT_LEVEL);
    }
    
    @Override
    public byte[] compress(byte[] data, int level) throws IOException {
        LZ4Compressor compressor;
        if (level == 0) {
            compressor = factory.fastCompressor();
        } else {
            // highCompressor级别范围1-17，这里映射0-9到1-9
            compressor = factory.highCompressor(Math.min(level, 9));
        }
        
        int maxCompressedLength = compressor.maxCompressedLength(data.length);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, maxCompressedLength);
        
        // 存储原始长度和压缩数据
        ByteBuffer buffer = ByteBuffer.allocate(4 + compressedLength);
        buffer.putInt(data.length);
        buffer.put(compressed, 0, compressedLength);
        
        return buffer.array();
    }
    
    @Override
    public byte[] decompress(byte[] compressedData) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(compressedData);
        int originalLength = buffer.getInt();
        
        byte[] compressed = new byte[compressedData.length - 4];
        buffer.get(compressed);
        
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[originalLength];
        decompressor.decompress(compressed, 0, restored, 0, originalLength);
        
        return restored;
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
        return 0; // fast compressor
    }
    
    @Override
    public int getMaxLevel() {
        return 9; // high compressor level
    }
}
