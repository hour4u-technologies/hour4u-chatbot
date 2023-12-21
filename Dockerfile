### BUILD image
FROM maven:3.8.4-openjdk-17 as builder

MAINTAINER Codemiro Technologies <dv.singh@codemiro.com>

#Copy Custom Maven settings
#COPY settings.xml /root/.m2/
# create app folder for sources
RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
#RUN mvn dependency:resolve-plugins
#Copy source code
COPY src /build/src
# Build application
RUN mvn package


FROM openjdk:17-jdk-slim-buster as runtime

EXPOSE 8099
#Set app home folder
ENV APP_HOME /app
#Possibility to set JVM options (https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)
ENV JAVA_OPTS=""

#Create base app folder
RUN mkdir $APP_HOME
#Create folder to save configuration files
RUN mkdir $APP_HOME/config
#Create folder with application logs
RUN mkdir $APP_HOME/log

VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

WORKDIR $APP_HOME
#Copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]
