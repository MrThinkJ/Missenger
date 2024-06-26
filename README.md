# Missenger

Missenger is a chat application built with Java, Spring Boot, and JavaScript. It allows users to send and receive messages, video call in real-time. The application also supports sending images in chat messages.

## Building the Project

### Without Docker

Ensure you have Java ( version 20 or higher ) and Maven installed on your machine, also MongoDB service running.

1. Clone the repository: 
```
git clone https://github.com/MrThinkJ/missenger.git
```
2. Navigate into the project directory:
```
cd missenger
```
3. Build the project using Maven:
```
mvn clean install
```
4. Run the application:
```
java -jar target/missenger-0.0.1-SNAPSHOT.jar
```

### With Docker

Ensure you have Docker installed on your machine.

1. Clone the repository:
```
git clone https://github.com/MrThinkJ/missenger.git
```
2. Navigate into the project directory:
```
cd missenger
```
3. Run Docker Compose:
```
docker-compose up
```

Please note that you should change the variables in the docker-compose and Dockerfile files according to your personal purposes.

## Configuration
### Without Docker
1. Java version in pom.xml
```
<properties>
    <java.version>20</java.version>
</properties>
```
2. MongoDB configuration in application.yml
```
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: missenger
```
3. Application configuration in application.yml
```
server:
  port: 8088
```
### With Docker
1. Java version in Dockerfile
```
FROM openjdk:20
```
2. MongoDB configuration in docker-compose.yml
* At service `mongo`
```
ports:
  - 27017:27017
environment:
  - MONGO_INITDB_ROOT_USERNAME=root
  - MONGO_INITDB_ROOT_PASSWORD=root
```
3. MongoExpress configuration in docker-compose.yml
* At service `mongo-express`
```
ports:
  - 8081:8081
environment:
  - ME_CONFIG_MONGODB_ADMINUSERNAME=root
  - ME_CONFIG_MONGODB_ADMINPASSWORD=root
  - ME_CONFIG_MONGODB_SERVER=mongo
```
4. Database configuration in docker-compose.yml
* At service `missenger`
```
ports:
  - 8088:8088
environment:
  - DB_HOST=mongo
  - DB_PORT=27017
  - DB_NAME=missenger
  - DB_USERNAME=root
  - DB_PASSWORD=root
  - SERVER_PORT=8088
```