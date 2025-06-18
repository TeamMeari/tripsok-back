FROM gradle:8.5-jdk21 AS build

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew clean build -x test

FROM gradle:8.5-jdk21 AS test

WORKDIR /app

COPY --from=build /app .

RUN ./gradlew test

FROM amazoncorretto:21-alpine AS run

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# 앱 실행
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx512m", "app.jar"]