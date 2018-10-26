FROM openjdk:8-jre-alpine
RUN apk update
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/America/Mexico_City /etc/localtime
RUN echo "America/Mexico_City" > /etc/timezone
RUN apk del tzdata
COPY target/demo-0.0.1-SNAPSHOT.jar /home/demo.jar
EXPOSE 9090
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/demo.jar"]
