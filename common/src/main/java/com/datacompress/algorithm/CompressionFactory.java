package com.datacompress.algorithm;

import com.datacompress.algorithm.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 压缩算法工厂类
 * 用于根据算法ID或名称获取对应的压缩算法实现
 */
public class CompressionFactory {
    
    private static final Map<Byte, CompressionAlgorithm> algorithmById = new HashMap<>();
    private static final Map<String, CompressionAlgorithm> algorithmByName = new HashMap<>();
    
    static {
        registerAlgorithm(new GzipCompression());
        registerAlgorithm(new DeflateCompression());
        registerAlgorithm(new ZipCompression());
        registerAlgorithm(new Bzip2Compression());
        registerAlgorithm(new LzmaCompression());
        registerAlgorithm(new Lz4Compression());
        registerAlgorithm(new ZstdCompression());
        registerAlgorithm(new SnappyCompression());
        registerAlgorithm(new BrotliCompression());
    }
    
    private static void registerAlgorithm(CompressionAlgorithm algorithm) {
        algorithmById.put(algorithm.getAlgorithmId(), algorithm);
        algorithmByName.put(algorithm.getName(), algorithm);
    }
    
    /**
     * 根据算法ID获取压缩算法实例
     * @param algorithmId 算法ID (1-9)
     * @return 压缩算法实例，如果不存在则返回null
     */
    public static CompressionAlgorithm getAlgorithm(byte algorithmId) {
        return algorithmById.get(algorithmId);
    }
    
    /**
     * 根据算法名称获取压缩算法实例
     * @param name 算法名称
     * @return 压缩算法实例，如果不存在则返回null
     */
    public static CompressionAlgorithm getAlgorithm(String name) {
        return algorithmByName.get(name);
    }
    
    /**
     * 获取所有支持的算法名称
     * @return 算法名称数组
     */
    public static String[] getAllAlgorithmNames() {
        return algorithmByName.keySet().toArray(new String[0]);
    }
}
