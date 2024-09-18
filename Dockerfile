FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/artauction-*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
