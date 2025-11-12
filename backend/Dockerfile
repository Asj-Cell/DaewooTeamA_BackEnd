# Base image with JDK 21
FROM eclipse-temurin:21-jdk

# Set timezone to Asia/Seoul
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built JAR file
COPY build/libs/backend.jar backend.jar

# Set default Spring profile (can be overridden by docker-compose)
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", \
  "-jar", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=prod", \
  "backend.jar"]
