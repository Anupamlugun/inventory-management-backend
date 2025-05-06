FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8083
COPY target/OrderSale-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]