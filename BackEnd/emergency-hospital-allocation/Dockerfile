# Étape 1 : build avec Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar

# Étape 2 : image finale
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# Active le profil docker
ENTRYPOINT ["sh", "-c", "sleep 15 && java -Dspring.profiles.active=docker -jar app.jar"]