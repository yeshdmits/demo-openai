FROM gradle:8-jdk17-focal AS BUILD
WORKDIR /opt/src/
COPY . .
RUN gradle build

FROM openjdk:17.0
WORKDIR /opt/src
COPY  --from=BUILD /opt/src/build/libs/demo-0.0.1-SNAPSHOT.jar /app/src/demo-openai.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/src/demo-openai.jar"]