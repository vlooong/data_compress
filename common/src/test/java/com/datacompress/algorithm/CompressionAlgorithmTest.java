package com.datacompress.algorithm;

import com.datacompress.algorithm.impl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 压缩算法单元测试
 */
class CompressionAlgorithmTest {
    
    /**
     * 提供所有压缩算法实例
     */
    static Stream<CompressionAlgorithm> algorithmProvider() {
        return Stream.of(
            new NoCompression(),
            new GzipCompression(),
            new DeflateCompression(),
            new ZipCompression(),
            new Bzip2Compression(),
            new LzmaCompression(),
            new Lz4Compression(),
            new ZstdCompression(),
            new SnappyCompression(),
            new BrotliCompression()
        );
    }
    
    /**
     * 测试简单文本的压缩和解压
     */
    @ParameterizedTest
    @MethodSource("algorithmProvider")
    void testSimpleTextCompression(CompressionAlgorithm algorithm) throws IOException {
        String originalText = "Hello, World! This is a test for compression algorithms.";
        byte[] originalData = originalText.getBytes(StandardCharsets.UTF_8);
        
        // 压缩
        byte[] compressed = algorithm.compress(originalData);
        assertNotNull(compressed, algorithm.getName() + ": 压缩结果不应为null");
        
        // 解压
        byte[] decompressed = algorithm.decompress(compressed);
        assertNotNull(decompressed, algorithm.getName() + ": 解压结果不应为null");
        
        // 验证解压后数据与原始数据一致
        assertArrayEquals(originalData, decompressed, 
            algorithm.getName() + ": 解压后的数据应与原始数据一致");
        
        String decompressedText = new String(decompressed, StandardCharsets.UTF_8);
        assertEquals(originalText, decompressedText, 
            algorithm.getName() + ": 解压后的文本应与原始文本一致");
    }
    
    /**
     * 测试重复数据的压缩效果
     */
    @ParameterizedTest
    @MethodSource("algorithmProvider")
    void testRepetitiveDataCompression(CompressionAlgorithm algorithm) throws IOException {
        // 创建大量重复数据
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        byte[] originalData = sb.toString().getBytes(StandardCharsets.UTF_8);
        
        // 压缩
        byte[] compressed = algorithm.compress(originalData);
        
        // 验证压缩确实减小了数据大小
        assertTrue(compressed.length < originalData.length,
            algorithm.getName() + ": 压缩后大小应小于原始大小");
        
        // 解压并验证
        byte[] decompressed = algorithm.decompress(compressed);
        assertArrayEquals(originalData, decompressed,
            algorithm.getName() + ": 解压后的数据应与原始数据一致");
        
        // 输出压缩比
        double ratio = (double) compressed.length / originalData.length;
        System.out.printf("%s - 原始: %d bytes, 压缩: %d bytes, 压缩比: %.2f%%%n",
            algorithm.getName(), originalData.length, compressed.length, ratio * 100);
    }
    
    /**
     * 测试随机数据的压缩
     */
    @ParameterizedTest
    @MethodSource("algorithmProvider")
    void testRandomDataCompression(CompressionAlgorithm algorithm) throws IOException {
        // 创建随机数据（不可压缩）
        byte[] originalData = new byte[1000];
        new Random(42).nextBytes(originalData);
        
        // 压缩
        byte[] compressed = algorithm.compress(originalData);
        
        // 解压并验证
        byte[] decompressed = algorithm.decompress(compressed);
        assertArrayEquals(originalData, decompressed,
            algorithm.getName() + ": 解压后的数据应与原始数据一致");
        
        // 随机数据通常无法有效压缩，可能会增大
        System.out.printf("%s - 随机数据 原始: %d bytes, 压缩: %d bytes%n",
            algorithm.getName(), originalData.length, compressed.length);
    }
    
    /**
     * 测试空数据的压缩
     */
    @ParameterizedTest
    @MethodSource("algorithmProvider")
    void testEmptyDataCompression(CompressionAlgorithm algorithm) throws IOException {
        byte[] originalData = new byte[0];
        
        // 压缩空数据
        byte[] compressed = algorithm.compress(originalData);
        assertNotNull(compressed, algorithm.getName() + ": 压缩空数据不应返回null");
        
        // 解压
        byte[] decompressed = algorithm.decompress(compressed);
        assertNotNull(decompressed, algorithm.getName() + ": 解压空数据不应返回null");
        assertEquals(0, decompressed.length, 
            algorithm.getName() + ": 解压后应为空数组");
    }
    
    /**
     * 测试大数据的压缩
     */
    @ParameterizedTest
    @MethodSource("algorithmProvider")
    void testLargeDataCompression(CompressionAlgorithm algorithm) throws IOException {
        // 创建1MB的测试数据
        byte[] originalData = new byte[1024 * 1024];
        Arrays.fill(originalData, (byte) 'A');
        
        // 压缩
        long startTime = System.currentTimeMillis();
        byte[] compressed = algorithm.compress(originalData);
        long compressTime = System.currentTimeMillis() - startTime;
        
        // 解压
        startTime = System.currentTimeMillis();
        byte[] decompressed = algorithm.decompress(compressed);
        long decompressTime = System.currentTimeMillis() - startTime;
        
        // 验证
        assertArrayEquals(originalData, decompressed,
            algorithm.getName() + ": 解压后的数据应与原始数据一致");
        
        // 输出性能信息
        double ratio = (double) compressed.length / originalData.length;
        System.out.printf("%s - 大数据测试 | 原始: %d bytes, 压缩: %d bytes, 压缩比: %.2f%%, " +
                        "压缩耗时: %d ms, 解压耗时: %d ms%n",
            algorithm.getName(), originalData.length, compressed.length, 
            ratio * 100, compressTime, decompressTime);
    }
    
    /**
     * 测试算法ID的唯一性
     */
    @Test
    void testAlgorithmIdsAreUnique() {
        CompressionAlgorithm[] algorithms = {
            new NoCompression(),
            new GzipCompression(),
            new DeflateCompression(),
            new ZipCompression(),
            new Bzip2Compression(),
            new LzmaCompression(),
            new Lz4Compression(),
            new ZstdCompression(),
            new SnappyCompression(),
            new BrotliCompression()
        };
        
        // 检查所有算法ID是否唯一
        long uniqueIdCount = Arrays.stream(algorithms)
            .map(CompressionAlgorithm::getAlgorithmId)
            .distinct()
            .count();
        
        assertEquals(algorithms.length, uniqueIdCount, "所有算法ID应该是唯一的");
        
        // 验证ID在0-9范围内
        Arrays.stream(algorithms).forEach(alg -> {
            assertTrue(alg.getAlgorithmId() >= 0 && alg.getAlgorithmId() <= 9,
                alg.getName() + " 的ID应在0-9范围内");
        });
    }
    
    /**
     * 测试CompressionFactory
     */
    @Test
    void testCompressionFactory() {
        // 测试通过ID获取算法
        for (byte id = 0; id <= 9; id++) {
            CompressionAlgorithm algorithm = CompressionFactory.getAlgorithm(id);
            assertNotNull(algorithm, "应该能通过ID " + id + " 获取算法");
            assertEquals(id, algorithm.getAlgorithmId(), "算法ID应该匹配");
        }
        
        // 测试通过名称获取算法
        String[] names = CompressionFactory.getAllAlgorithmNames();
        assertEquals(10, names.length, "应该有10种算法");
        
        for (String name : names) {
            CompressionAlgorithm algorithm = CompressionFactory.getAlgorithm(name);
            assertNotNull(algorithm, "应该能通过名称 " + name + " 获取算法");
            assertEquals(name, algorithm.getName(), "算法名称应该匹配");
        }
    }
}
