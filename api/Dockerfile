FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY ["aplicacao", "./aplicacao"]

COPY ["video", "./video"]

COPY ["pom.xml", "./pom.xml"]

COPY ["application-local.yaml", "application.yaml", "./"]

RUN mvn -DskipTests package

EXPOSE 3000

CMD ["java", "-jar", "aplicacao/target/street-eye-backend-0.0.1-SNAPSHOT.jar"]
