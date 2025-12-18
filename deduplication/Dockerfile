FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY deduplication/pom.xml .
COPY mvnw .
COPY .mvn ./.mvn
RUN ./mvnw dependency:go-offline

COPY deduplication/src ./src
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/highloadstreamprocessor-deduplication-0.0.1-SNAPSHOT.jar"]
