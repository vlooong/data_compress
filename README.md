# 数据压缩测试系统

一个用于测试和评估不同数据压缩算法性能的客户端-服务端应用程序。

## 项目概述

本系统实现了客户端与服务端之间的文件传输通信，支持多种压缩算法，并能够测量和展示详细的性能指标，包括：
- 压缩耗时
- 发送时延
- 传播时延
- 解压耗时
- 压缩比
- 总往返时间

## 技术栈

- **Java**: 11+
- **构建工具**: Maven
- **网络框架**: Netty (服务端)
- **UI框架**: JavaFX 17
- **压缩算法**: GZIP, DEFLATE, ZIP, BZIP2, LZMA, LZ4, Zstd, SNAPPY, Brotli
- **日志框架**: SLF4J + Logback

## 项目结构

```
data_compress/
├── pom.xml                 # Maven父项目配置
├── common/                 # 公共模块
│   ├── algorithm/          # 9种压缩算法实现
│   ├── protocol/           # 通信协议（编解码器）
│   └── model/              # 数据模型
├── server/                 # 服务端模块（Netty）
│   └── src/main/java/
│       └── com/datacompress/server/
│           ├── CompressionServer.java
│           ├── ServerInitializer.java
│           └── CompressionServerHandler.java
└── client/                 # 客户端模块（JavaFX）
    └── src/main/java/
        └── com/datacompress/client/
            ├── CompressionClient.java
            ├── FileManager.java
            └── ui/
                ├── MainApplication.java
                └── MainController.java
```

## 构建项目

### 前置要求

- JDK 11 或更高版本
- Maven 3.6+

### 编译

在项目根目录下执行：

```bash
mvn clean compile
```

### 打包

```bash
mvn clean package
```

打包后的JAR文件位于：
- 服务端: `server/target/server-1.0.0.jar`
- 客户端: `client/target/client-1.0.0.jar`

## 运行项目

### 1. 启动服务端

```bash
cd server
java -jar target/server-1.0.0.jar
```

默认监听端口为 `8888`。可以通过命令行参数指定端口：

```bash
java -jar target/server-1.0.0.jar 9999
```

### 2. 启动客户端

使用JavaFX插件运行：

```bash
cd client
mvn javafx:run
```

或者直接运行打包的JAR：

```bash
cd client
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/client-1.0.0.jar
```

**注意**: 运行JavaFX应用需要配置JavaFX SDK路径。

### 3. 使用客户端

1. **连接服务器**
   - 输入服务器地址（默认：localhost）
   - 输入端口号（默认：8888）
   - 点击"连接"按钮
   - 连接成功后状态指示灯变为绿色

2. **传输文件**
   - 点击"浏览..."按钮选择要传输的文件
   - 从下拉菜单中选择压缩算法
   - 点击"发送"按钮
   - 观察进度条和性能指标

3. **查看结果**
   - 性能指标区域显示当前传输的详细数据
   - 测试历史表格记录所有传输记录，方便对比不同算法

## 支持的压缩算法

| 算法 | 特点 | 适用场景 |
|------|------|----------|
| **GZIP** | 平衡的压缩比和速度 | 通用场景 |
| **DEFLATE** | GZIP的底层算法 | 通用场景 |
| **ZIP** | 广泛支持的格式 | 文件归档 |
| **BZIP2** | 较高压缩比，速度较慢 | 归档存储 |
| **LZMA** | 极高压缩比，速度慢 | 长期存储 |
| **LZ4** | 极快速度，中等压缩比 | 实时传输 |
| **Zstd** | 优秀的压缩比和速度平衡 | 现代通用场景 |
| **SNAPPY** | 快速压缩解压 | 实时数据流 |
| **Brotli** | 高压缩比，适合文本 | Web传输 |

## 性能指标说明

- **原始大小**: 原始文件的字节数
- **压缩后大小**: 压缩后的字节数
- **压缩比**: 压缩后大小 / 原始大小 × 100%（越小越好）
- **压缩耗时**: 客户端压缩数据所需时间
- **发送耗时**: 从开始发送到发送完成的时间
- **传播时延**: 数据在网络中传输的时间
- **解压耗时**: 服务端解压数据所需时间
- **总往返时间**: 从开始压缩到收到服务端响应的总时间

## 日志文件

- 服务端日志: `server/logs/server.log`
- 客户端日志: `client/logs/client.log`

## 常见问题

### Q1: JavaFX运行时错误
**A**: 确保已正确安装JavaFX SDK，并在运行时添加JavaFX模块路径。

### Q2: 连接服务器失败
**A**: 
- 检查服务端是否已启动
- 检查主机地址和端口是否正确
- 检查防火墙设置

### Q3: Brotli压缩失败
**A**: Brotli依赖本地库，确保系统支持。如果失败，可以使用其他8种算法。

## 开发和测试

### 运行测试

```bash
mvn test
```

### 开发建议

1. 在本地测试时使用 `localhost` 作为服务器地址
2. 建议先测试小文件（&lt;10MB），再测试大文件
3. 使用测试历史功能对比不同算法的性能

## 许可证

本项目仅用于学习和测试目的。

## 作者

数据压缩测试系统开发团队
