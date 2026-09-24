FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /build

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY ./ ./

RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    ./mvnw -B -nsu dependency:go-offline && \
    ./mvnw clean package


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/backend/target/*.jar ./

ENTRYPOINT ["exec", "java", "-jar", "backend-0.0.1.jar "]

EXPOSE 8080 8080