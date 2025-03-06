# Этап сборки
FROM openjdk:17-jdk-alpine as build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем только файлы, необходимые для сборки (оптимизация кэширования Docker)
COPY settings.gradle.kts .
COPY Project/build.gradle.kts Project/
COPY Project/src Project/src
COPY gradlew .
COPY gradle gradle

# Переходим в папку проекта
WORKDIR /app/Project

# Собираем проект с помощью Gradle
RUN chmod +x ../gradlew
RUN ../gradlew build

# Этап запуска
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR-файл из этапа сборки
COPY --from=build /app/Project/build/libs/*.jar app.jar

# Открываем порт, на котором будет работать приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]