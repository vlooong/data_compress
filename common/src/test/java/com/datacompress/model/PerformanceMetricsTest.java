package com.datacompress.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 性能指标测试
 */
class PerformanceMetricsTest {
    
    @Test
    void testCompressionRatioCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setOriginalSize(1000);
        metrics.setCompressedSize(500);
        
        assertEquals(0.5, metrics.getCompressionRatio(), 0.001);
    }
    
    @Test
    void testCompressionTimeCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setCompressStartTime(1000);
        metrics.setCompressEndTime(1500);
        
        assertEquals(500, metrics.getCompressionTime());
    }
    
    @Test
    void testSendTimeCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setSendStartTime(2000);
        metrics.setSendEndTime(2300);
        
        assertEquals(300, metrics.getSendTime());
    }
    
    @Test
    void testDecompressionTimeCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setDecompressStartTime(3000);
        metrics.setDecompressEndTime(3400);
        
        assertEquals(400, metrics.getDecompressionTime());
    }
    
    @Test
    void testPropagationDelayCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setSendEndTime(2000);
        metrics.setReceiveStartTime(2100);
        
        assertEquals(100, metrics.getPropagationDelay());
    }
    
    @Test
    void testTotalRoundTripTimeCalculation() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setCompressStartTime(1000);
        metrics.setResponseReceivedTime(5000);
        
        assertEquals(4000, metrics.getTotalRoundTripTime());
    }
    
    @Test
    void testToString() {
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.setAlgorithmName("GZIP");
        metrics.setOriginalSize(1000);
        metrics.setCompressedSize(500);
        metrics.setCompressStartTime(1000);
        metrics.setCompressEndTime(1100);
        metrics.setSendStartTime(1100);
        metrics.setSendEndTime(1200);
        metrics.setDecompressStartTime(1300);
        metrics.setDecompressEndTime(1350);
        metrics.setResponseReceivedTime(1400);
        
        String result = metrics.toString();
        assertTrue(result.contains("GZIP"));
        assertTrue(result.contains("1000"));
        assertTrue(result.contains("500"));
    }
}
