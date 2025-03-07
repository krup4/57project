FROM openjdk:17-alpine
LABEL maintainer=krupn0ff
WORKDIR /app
COPY Project/build/docker/libs libs/
COPY Project/build/docker/resources resources/
COPY Project/build/docker/classes classes/
ENTRYPOINT ["java", "-cp", "/app/resources:/app/classes:/app/libs/*", "application.PrinterApplicationKt"]
EXPOSE 8080
