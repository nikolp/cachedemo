FROM openjdk:17-jdk

WORKDIR /app

COPY target/cachedemo-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
