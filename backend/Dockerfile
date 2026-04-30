FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

RUN chmod +x mvnw \
    && ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
