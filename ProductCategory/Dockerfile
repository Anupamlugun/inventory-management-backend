FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8081
COPY target/ProductCategory-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]