FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /work
COPY pom.xml .
COPY audit-service/pom.xml audit-service/pom.xml
COPY . .
RUN mvn -B -pl audit-service -am -DskipTests package

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /work/audit-service/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java","-jar","/app/app.jar"]