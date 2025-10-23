FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . /workspace
RUN mvn -B -DskipTests -pl audit-service -am package
RUN mkdir -p /workspace && cp audit-service/target/*.jar /workspace/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /
COPY --from=build /workspace/app.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "/app.jar"]