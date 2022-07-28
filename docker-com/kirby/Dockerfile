FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=./*.jar
COPY ${JAR_FILE} kirby-app.jar
ENTRYPOINT ["java","-jar","/kirby-app.jar"]
