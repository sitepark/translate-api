FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/translate-api-*-jar-with-dependencies.jar app.jar
RUN chown app:app app.jar
USER app
ENTRYPOINT ["java", "-jar", "app.jar"]
