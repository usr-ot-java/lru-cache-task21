FROM openjdk:17
VOLUME /tmp

ENV JAR=build/libs/lru-cache-1.0-SNAPSHOT.jar

COPY ${JAR} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]