FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file (adjust the path and name)
COPY build/libs/backend.jar backend.jar

# 환경변수 선언
ENV SPRING_PROFILE=prod

# Set command to run the JAR
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILE}", "backend.jar"]