FROM openjdk:11
COPY /target/*.jar app.jar
ENTRYPOINT ["java","-jar","./app.jar","--spring.datasource.url=jdbc:postgresql://db/devices"]