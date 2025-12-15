FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Копируем Maven wrapper и даем права
COPY mvnw .
COPY .mvn ./.mvn
RUN chmod +x mvnw

# Копируем pom.xml
COPY pom.xml .

# Скачиваем только зависимости для компиляции (быстрее и меньше)
RUN ./mvnw dependency:resolve

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Запускаем приложение
CMD ["java", "-jar", "target/highloadstreamprocessor-1.0-SNAPSHOT.jar"]
