# 构建阶段 - 使用 Maven 镜像
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /dailyWeb
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre
LABEL maintainer="jacka7732"

# 安装 wget 用于健康检查
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 创建数据和日志目录
RUN mkdir -p /app/data/beifen  && chmod -R 755 /app

# 复制 jar 文件
COPY --from=builder /dailyWeb/target/dailyWeb-2.0-beta.jar app.jar

# 暴露端口
EXPOSE 8081

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS app.jar"]
