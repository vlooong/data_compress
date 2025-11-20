package com.datacompress.client.ui;

import com.datacompress.algorithm.CompressionAlgorithm;
import com.datacompress.algorithm.CompressionFactory;
import com.datacompress.client.CompressionClient;
import com.datacompress.client.FileManager;
import com.datacompress.model.PerformanceMetrics;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 主界面控制器
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private Button connectButton;
    @FXML private Circle statusIndicator;
    @FXML private Label statusLabel;

    @FXML private TextField filePathField;
    @FXML private Button browseButton;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Spinner<Integer> compressionLevelSpinner;
    @FXML private Label levelLabel;
    @FXML private Button sendButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    @FXML private Label originalSizeLabel;
    @FXML private Label compressedSizeLabel;
    @FXML private Label compressionRatioLabel;
    @FXML private Label compressTimeLabel;
    @FXML private Label sendTimeLabel;
    @FXML private Label propagationDelayLabel;
    @FXML private Label decompressTimeLabel;
    @FXML private Label totalTimeLabel;

    @FXML private TableView<MetricsRow> historyTable;
    @FXML private TableColumn<MetricsRow, String> algorithmColumn;
    @FXML private TableColumn<MetricsRow, String> originalSizeColumn;
    @FXML private TableColumn<MetricsRow, String> compressedSizeColumn;
    @FXML private TableColumn<MetricsRow, String> ratioColumn;
    @FXML private TableColumn<MetricsRow, String> compressTimeColumn;
    @FXML private TableColumn<MetricsRow, String> totalTimeColumn;

    private CompressionClient client;
    private File selectedFile;
    private ObservableList<MetricsRow> historyData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 初始化主机和端口
        hostField.setText("localhost");
        portField.setText("8888");

        // 初始化压缩算法下拉框
        String[] algorithms = CompressionFactory.getAllAlgorithmNames();
        algorithmComboBox.setItems(FXCollections.observableArrayList(algorithms));
        if (algorithms.length > 0) {
            algorithmComboBox.setValue(algorithms[0]);
        }

        // 初始化压缩级别Spinner
        compressionLevelSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9, 6)
        );
        compressionLevelSpinner.setEditable(true);

        // 当算法改变时，更新压缩级别范围
        algorithmComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateCompressionLevelRange(newVal);
            }
        });

        // 初始化时更新一次压缩级别范围
        if (algorithmComboBox.getValue() != null) {
            updateCompressionLevelRange(algorithmComboBox.getValue());
        }

        // 初始化表格列
        algorithmColumn.setCellValueFactory(new PropertyValueFactory<>("algorithm"));
        originalSizeColumn.setCellValueFactory(new PropertyValueFactory<>("originalSize"));
        compressedSizeColumn.setCellValueFactory(new PropertyValueFactory<>("compressedSize"));
        ratioColumn.setCellValueFactory(new PropertyValueFactory<>("ratio"));
        compressTimeColumn.setCellValueFactory(new PropertyValueFactory<>("compressTime"));
        totalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        historyTable.setItems(historyData);

        // 设置初始状态
        updateConnectionStatus(false);
        sendButton.setDisable(true);
    }

    /**
     * 更新压缩级别范围
     */
    private void updateCompressionLevelRange(String algorithmName) {
        CompressionAlgorithm algorithm = CompressionFactory.getAlgorithm(algorithmName);
        if (algorithm != null) {
            if (algorithm.supportsCustomLevel()) {
                compressionLevelSpinner.setDisable(false);
                levelLabel.setDisable(false);

                SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        algorithm.getMinLevel(),
                        algorithm.getMaxLevel(),
                        algorithm.getDefaultLevel()
                    );
                compressionLevelSpinner.setValueFactory(valueFactory);

                // 更新级别提示
                levelLabel.setText(String.format("压缩级别 (%d-%d):",
                    algorithm.getMinLevel(), algorithm.getMaxLevel()));
            } else {
                // 不支持自定义级别
                compressionLevelSpinner.setDisable(true);
                levelLabel.setDisable(true);
                levelLabel.setText("压缩级别:");
            }
        }
    }

    @FXML
    private void handleConnect() {
        if (client != null && client.isConnected()) {
            // 断开连接
            client.disconnect();
            client = null;
            updateConnectionStatus(false);
            connectButton.setText("连接");
        } else {
            // 连接服务器
            String host = hostField.getText().trim();
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("错误", "无效的端口号");
                return;
            }

            client = new CompressionClient(host, port);
            connectButton.setDisable(true);

            client.connect().thenAccept(success -> {
                Platform.runLater(() -> {
                    connectButton.setDisable(false);
                    if (success) {
                        updateConnectionStatus(true);
                        connectButton.setText("断开");
                    } else {
                        showAlert("连接失败", "无法连接到服务器 " + host + ":" + port);
                        updateConnectionStatus(false);
                    }
                });
            });
        }
    }

    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择要传输的文件");
        selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());

        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
            sendButton.setDisable(client == null || !client.isConnected());
        }
    }

    @FXML
    private void handleSend() {
        if (selectedFile == null) {
            showAlert("错误", "请先选择要传输的文件");
            return;
        }

        if (client == null || !client.isConnected()) {
            showAlert("错误", "未连接到服务器");
            return;
        }

        String algorithm = algorithmComboBox.getValue();
        if (algorithm == null) {
            showAlert("错误", "请选择压缩算法");
            return;
        }

        // 获取压缩级别
        int compressionLevel = compressionLevelSpinner.getValue();

        // 禁用发送按钮
        sendButton.setDisable(true);
        progressBar.setProgress(0);
        progressLabel.setText("准备中...");
        clearMetrics();

        // 读取文件
        byte[] fileData;
        try {
            fileData = FileManager.readFile(selectedFile);
        } catch (Exception e) {
            logger.error("读取文件失败", e);
            showAlert("错误", "读取文件失败: " + e.getMessage());
            sendButton.setDisable(false);
            return;
        }

        // 发送文件（传递压缩级别）
        client.sendFile(fileData, algorithm, compressionLevel, (progress, message) -> {
            Platform.runLater(() -> {
                progressBar.setProgress(progress);
                progressLabel.setText(message);
            });
        }).thenAccept(metrics -> {
            Platform.runLater(() -> {
                displayMetrics(metrics);
                addToHistory(metrics);
                sendButton.setDisable(false);
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                logger.error("发送文件失败", e);
                showAlert("错误", "发送文件失败: " + e.getMessage());
                sendButton.setDisable(false);
                progressBar.setProgress(0);
                progressLabel.setText("");
            });
            return null;
        });
    }

    private void updateConnectionStatus(boolean connected) {
        if (connected) {
            statusIndicator.setFill(Color.GREEN);
            statusLabel.setText("已连接");
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusIndicator.setFill(Color.RED);
            statusLabel.setText("未连接");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void displayMetrics(PerformanceMetrics metrics) {
        originalSizeLabel.setText(FileManager.formatFileSize(metrics.getOriginalSize()));
        compressedSizeLabel.setText(FileManager.formatFileSize(metrics.getCompressedSize()));
        compressionRatioLabel.setText(String.format("%.2f%%", metrics.getCompressionRatio() * 100));
        compressTimeLabel.setText(metrics.getCompressionTime() + " ms");
        sendTimeLabel.setText(metrics.getSendTime() + " ms");
        propagationDelayLabel.setText(metrics.getPropagationDelay() + " ms");
        decompressTimeLabel.setText(metrics.getDecompressionTime() + " ms");
        totalTimeLabel.setText(metrics.getTotalRoundTripTime() + " ms");
    }

    private void clearMetrics() {
        originalSizeLabel.setText("-");
        compressedSizeLabel.setText("-");
        compressionRatioLabel.setText("-");
        compressTimeLabel.setText("-");
        sendTimeLabel.setText("-");
        propagationDelayLabel.setText("-");
        decompressTimeLabel.setText("-");
        totalTimeLabel.setText("-");
    }

    private void addToHistory(PerformanceMetrics metrics) {
        MetricsRow row = new MetricsRow(
                metrics.getAlgorithmName(),
                FileManager.formatFileSize(metrics.getOriginalSize()),
                FileManager.formatFileSize(metrics.getCompressedSize()),
                String.format("%.2f%%", metrics.getCompressionRatio() * 100),
                metrics.getCompressionTime() + " ms",
                metrics.getTotalRoundTripTime() + " ms"
        );
        historyData.add(0, row); // 添加到列表开头
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void shutdown() {
        if (client != null) {
            client.disconnect();
        }
    }

    /**
     * 表格行数据类
     */
    public static class MetricsRow {
        private final String algorithm;
        private final String originalSize;
        private final String compressedSize;
        private final String ratio;
        private final String compressTime;
        private final String totalTime;

        public MetricsRow(String algorithm, String originalSize, String compressedSize,
                         String ratio, String compressTime, String totalTime) {
            this.algorithm = algorithm;
            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
            this.ratio = ratio;
            this.compressTime = compressTime;
            this.totalTime = totalTime;
        }

        public String getAlgorithm() { return algorithm; }
        public String getOriginalSize() { return originalSize; }
        public String getCompressedSize() { return compressedSize; }
        public String getRatio() { return ratio; }
        public String getCompressTime() { return compressTime; }
        public String getTotalTime() { return totalTime; }
    }
}
