FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN echo "SPRING_REDIS_HOST=${SPRING_REDIS_HOST}"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]