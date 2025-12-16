FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY enrichment/pom.xml .
COPY mvnw .
COPY .mvn ./.mvn
RUN ./mvnw dependency:go-offline

COPY enrichment/src ./src
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/highloadstreamprocessorenrichment-0.0.1-SNAPSHOT.jar"]
