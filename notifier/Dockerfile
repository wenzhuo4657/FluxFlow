# 构建阶段 - 使用 Maven 镜像
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段 - 使用轻量级 JRE
FROM eclipse-temurin:17-jre
LABEL maintainer="jacka7732"

# 安装 wget 用于健康检查
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /notifier

# 复制 jar 文件
COPY --from=builder /app/target/app-1.0-SNAPSHOT.jar  notifier.jar

# 暴露端口
EXPOSE 8089

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS notifier.jar"]


