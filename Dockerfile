FROM amazoncorretto:21-alpine

WORKDIR /app

COPY build/libs/artauction-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
