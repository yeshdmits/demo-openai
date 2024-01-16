FROM openjdk:17.0
WORKDIR /opt/src
COPY build/libs/demo-0.0.1-SNAPSHOT.jar /opt/src/demo-openai.jar
EXPOSE 8080
CMD ["java", "-jar", "/opt/src/demo-openai.jar"]