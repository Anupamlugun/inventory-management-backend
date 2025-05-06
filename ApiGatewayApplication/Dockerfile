FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8888
COPY target/ApiGatewayApplication-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
