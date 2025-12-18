FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

COPY filter/pom.xml .
COPY mvnw .
COPY .mvn ./.mvn
RUN ./mvnw dependency:go-offline

COPY filter/src ./src
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/highloadstreamprocessorfilter-0.0.1-SNAPSHOT.jar"]
