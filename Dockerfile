FROM gradle:jdk17 AS cache

WORKDIR /app

COPY gradle gradle
COPY ./Project/build.gradle.kts settings.gradle.kts ./

RUN gradle dependencies --no-daemon --stacktrace

FROM gradle:jdk17 AS builder

WORKDIR /app

COPY --from=cache /app/.gradle ./.gradle
COPY --from=cache /app .

COPY Project/src src

RUN gradle bootJar -x test --no-daemon --stacktrace

FROM openjdk:17-jdk

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar ./app.jar

CMD ["java", "-jar", "app.jar"]
