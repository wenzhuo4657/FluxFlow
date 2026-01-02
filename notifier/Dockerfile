# 构建阶段 - 使用 Maven 镜像
FROM infotechsoft/maven:3.9.6-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段 - 使用轻量级 JRE
# todo 这里或许应该考虑将mq和redis一起部署了？否则太过简单了
FROM eclipse-temurin:17-jre
LABEL maintainer="jacka7732"
COPY --from=builder /app/target/app-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS app.jar"]


