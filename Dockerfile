FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-alpine

RUN addgroup -S -g 1000 app && adduser -S -u 1000 -G app app
COPY --from=build /app/target/translate-api-*-jar-with-dependencies.jar /opt/app.jar
RUN chown app:app /opt/app.jar
WORKDIR /app
USER app
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
