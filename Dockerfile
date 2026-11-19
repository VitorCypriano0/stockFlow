FROM maven:3.9.8-eclipse-temurin-21-jammy
LABEL authors="vitor.cypriano"

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

WORKDIR /app

COPY --from=builder /app/target/stockFlow-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar","stockFlow-0.0.1-SNAPSHOT.jar"]