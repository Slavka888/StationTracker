FROM maven:3.9.16-eclipse-temurin-21-alpine as build

WORKDIR /build

COPY pom.xml .

RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S stationtracker && adduser -S stationtracker -G stationtracker

WORKDIR /app

COPY --from=build --chown=stationtracker:stationtracker /build/target/StationTracker-0.0.1-SNAPSHOT.jar /app/app.jar

RUN mkdir -p /app/logs && chown -R stationtracker:stationtracker /app

USER stationtracker

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]