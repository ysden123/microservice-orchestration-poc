FROM openjdk:8-jdk-alpine

ARG APP_FILE=zeebe-app2-0.0.1.jar
ENV APP_FILE $APP_FILE
ENV APPLICATION_HOME /usr/apps/zeebe-app2

ENV JAVA_OPTS ""

COPY target/scala-2.13/$APP_FILE $APPLICATION_HOME/

WORKDIR $APPLICATION_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java $JAVA_OPTS -jar $APP_FILE"]