FROM openjdk:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/app.jar"]