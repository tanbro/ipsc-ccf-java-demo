# IPSC 通用CTI低代码流程的 Java 程序调用 API 例子

## 准备

1. JDK
1. Maven
1. 将 `ipsc-bus-client-java`, `ipsc-ccf-java` 和本项目的代码仓库复制到同一个工作目录下。
1. 按照 `ipsc-bus-client-java` 的 README 编译这个 JNI 库并生成jar包
1. 按照 `ipsc-ccf-java` 的 README 生成jar包

## 执行

1. 首先进入项目目录

   ```sh
   cd path/to/ipsc-bus-client-java-demo
   ```

1. 安装依赖包

   ```sh
   mvn install
   ```

1. 编译

   ```sh
   mvn compile
   ```

1. 设置 JNI 的库搜索路径，例如:

   ```sh
   export MAVEN_OPTS="-Djava.library.path=/usr/local/lib"
   ```

1. 执行

   会议Demo的执行命令行是：

   ```sh
   mvn exec:java
   ```
