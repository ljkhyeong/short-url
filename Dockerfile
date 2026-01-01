FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=local", "-jar", "app.jar"]