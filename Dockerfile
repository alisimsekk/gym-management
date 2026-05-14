FROM eclipse-temurin:25-jdk-noble AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src ./src

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:25-jre-noble

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/gym-management-1.0.0.jar app.jar

EXPOSE 8089

HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=5 \
  CMD wget -q --spider http://localhost:8089/api/v1/v3/api-docs || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
