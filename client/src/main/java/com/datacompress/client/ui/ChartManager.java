package com.datacompress.client.ui;

import com.datacompress.model.PerformanceMetrics;
import javafx.scene.chart.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 图表管理器
 * 负责管理和更新性能图表
 */
public class ChartManager {
    
    private final BarChart<String, Number> compressionRatioChart;
    private final BarChart<String, Number> timePerformanceChart;
    private final ScatterChart<Number, Number> tradeoffChart;
    
    private final Map<String, XYChart.Series<Number, Number>> tradeoffSeriesMap = new HashMap<>();
    
    public ChartManager(BarChart<String, Number> compressionRatioChart,
                       BarChart<String, Number> timePerformanceChart,
                       ScatterChart<Number, Number> tradeoffChart) {
        this.compressionRatioChart = compressionRatioChart;
        this.timePerformanceChart = timePerformanceChart;
        this.tradeoffChart = tradeoffChart;
        
        initializeCharts();
    }
    
    private void initializeCharts() {
        // 初始化压缩比图表
        compressionRatioChart.setLegendVisible(false);
        compressionRatioChart.setAnimated(true);
        
        // 初始化时间性能图表
        timePerformanceChart.setLegendVisible(true);
        timePerformanceChart.setAnimated(true);
        
        // 初始化权衡图表
        tradeoffChart.setLegendVisible(true);
        tradeoffChart.setAnimated(true);
    }
    
    /**
     * 添加性能数据点到图表
     */
    public void addPerformanceData(PerformanceMetrics metrics) {
        String algorithm = metrics.getAlgorithmName();
        double ratio = metrics.getCompressionRatio() * 100;
        
        // 更新压缩比图表
        updateCompressionRatioChart(algorithm, ratio);
        
        // 更新时间性能图表
        updateTimePerformanceChart(metrics);
        
        // 更新权衡图表
        updateTradeoffChart(algorithm, ratio, metrics.getTotalRoundTripTime());
    }
    
    private void updateCompressionRatioChart(String algorithm, double ratio) {
        // 压缩比图表应该只有一个系列，所有算法都在这个系列中
        XYChart.Series<String, Number> series;
        
        if (compressionRatioChart.getData().isEmpty()) {
            // 创建唯一的系列
            series = new XYChart.Series<>();
            series.setName("压缩比");
            compressionRatioChart.getData().add(series);
        } else {
            series = compressionRatioChart.getData().get(0);
        }
        
        // 查找是否已存在该算法的数据点
        XYChart.Data<String, Number> existingData = null;
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getXValue().equals(algorithm)) {
                existingData = data;
                break;
            }
        }
        
        if (existingData != null) {
            // 更新现有数据
            existingData.setYValue(ratio);
        } else {
            // 添加新数据点
            series.getData().add(new XYChart.Data<>(algorithm, ratio));
        }
    }
    
    private void updateTimePerformanceChart(PerformanceMetrics metrics) {
        String algorithm = metrics.getAlgorithmName();
        
        // 查找或创建各个时间指标的系列
        XYChart.Series<String, Number> compressSeries = findOrCreateSeries(timePerformanceChart, "压缩");
        XYChart.Series<String, Number> sendSeries = findOrCreateSeries(timePerformanceChart, "发送");
        XYChart.Series<String, Number> decompressSeries = findOrCreateSeries(timePerformanceChart, "解压");
        
        // 更新数据
        updateSeriesData(compressSeries, algorithm, metrics.getCompressionTime());
        updateSeriesData(sendSeries, algorithm, metrics.getSendTime());
        updateSeriesData(decompressSeries, algorithm, metrics.getDecompressionTime());
    }
    
    private XYChart.Series<String, Number> findOrCreateSeries(BarChart<String, Number> chart, String name) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            if (series.getName().equals(name)) {
                return series;
            }
        }
        
        // 创建新系列
        XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
        newSeries.setName(name);
        chart.getData().add(newSeries);
        return newSeries;
    }
    
    private void updateSeriesData(XYChart.Series<String, Number> series, String algorithm, long value) {
        // 查找是否已存在该算法的数据点
        XYChart.Data<String, Number> existingData = null;
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getXValue().equals(algorithm)) {
                existingData = data;
                break;
            }
        }
        
        if (existingData != null) {
            // 更新现有数据
            existingData.setYValue(value);
        } else {
            // 添加新数据
            series.getData().add(new XYChart.Data<>(algorithm, value));
        }
    }
    
    private void updateTradeoffChart(String algorithm, double ratio, long totalTime) {
        // 查找或创建该算法的系列
        XYChart.Series<Number, Number> series = tradeoffSeriesMap.get(algorithm);
        
        if (series == null) {
            // 创建新系列
            series = new XYChart.Series<>();
            series.setName(algorithm);
            tradeoffSeriesMap.put(algorithm, series);
            tradeoffChart.getData().add(series);
        } else {
            // 清除旧数据（每个算法只显示一个点）
            series.getData().clear();
        }
        
        // 添加数据点
        series.getData().add(new XYChart.Data<>(ratio, totalTime));
    }
    
    /**
     * 清空所有图表数据
     */
    public void clearAllCharts() {
        compressionRatioChart.getData().clear();
        timePerformanceChart.getData().clear();
        tradeoffChart.getData().clear();
        tradeoffSeriesMap.clear();
    }
}
