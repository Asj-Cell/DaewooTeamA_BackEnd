FROM openjdk:21-jdk-slim

# Set timezone
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs

# Copy JAR file
COPY build/libs/backend.jar backend.jar

# Set default profile
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 8888

# Run application
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", \
  "backend.jar"]