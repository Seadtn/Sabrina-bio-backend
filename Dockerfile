FROM eclipse-temurin:17-jdk-focal as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw  
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-focal
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]