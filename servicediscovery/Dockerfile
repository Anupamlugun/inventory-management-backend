FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8761
COPY target/servicediscovery-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]