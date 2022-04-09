FROM amazoncorretto:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=build/libs/boot-app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT java -jar /app.jar
