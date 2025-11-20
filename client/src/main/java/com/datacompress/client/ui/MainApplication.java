package com.datacompress.client.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX应用程序主入口
 */
public class MainApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 700);
            
            // 加载样式表
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("数据压缩测试系统");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // 关闭时断开连接
            MainController controller = loader.getController();
            primaryStage.setOnCloseRequest(event -> {
                controller.shutdown();
            });
            
            primaryStage.show();
            
            logger.info("应用程序已启动");
            
        } catch (Exception e) {
            logger.error("启动应用程序失败", e);
            throw e;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
