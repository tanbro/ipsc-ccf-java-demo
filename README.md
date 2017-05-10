# IPSC通用CTI流程的Java程序例子

## 如何运行

### 准备
1. JDK
2. Maven
3. 将 `ipsc-bus-client-java`, `ipsc-ccf-java` 和本项目的代码仓库复制到同一个工作目录下。
4. 按照 `ipsc-bus-client-java` 的 README 编译这个 JNI 库并生成jar包
5. 按照 `ipsc-ccf-java` 的 README 生成jar包

### 执行
首先进入项目目录
```shell
$ cd path/to/ipsc-bus-client-java
```
#### 1. 安装依赖包
```shell
$ mvn install
```

#### 2. 编译
```shell
$ mvn compile
```

#### 3. 设置 JNI 的库搜索路径
```shell
$ export MAVEN_OPTS="-Djava.library.path=/usr/local/lib"
```

#### 4. 执行

* 会议Demo的执行命令行是：
```$shell
$ mvn exec:java -pl ipsc-ccf-demo-conference
```
