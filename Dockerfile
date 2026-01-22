FROM openjdk:17.0.2-jdk

WORKDIR /app

COPY build/libs/student-service-0.0.1-SNAPSHOT.jar /app/student-service-0.0.1-SNAPSHOT.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "student-service-0.0.1-SNAPSHOT.jar"]