# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# pom.xml ve kaynak kodu kopyala
COPY pom.xml .
COPY src ./src

# Projeyi build et (testleri atlayarak)
RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Build aşamasında oluşan jar'ı al
COPY --from=build /app/target/*.jar app.jar

# (Opsiyonel) JVM bellek ayarı
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"

EXPOSE 8080

# Uygulamayı başlat
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
