FROM openjdk:17-jdk-alpine
MAINTAINER creammjnk
COPY target/chatapp-0.0.1-SNAPSHOT.jar chatapp-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/chatapp-0.0.1-SNAPSHOT.jar"]