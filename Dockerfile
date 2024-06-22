FROM maven:3-openjdk-17 AS build
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app
RUN mvn clean package-DskipTests

FROM openjdk:20-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=classpath:/application-prod.yml"]