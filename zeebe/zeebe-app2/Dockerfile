FROM openjdk:8-jre-alpine

ARG APP_FILE=zeebe-app2-0.0.1-exec.jar
ENV APP_FILE $APP_FILE
ENV APPLICATION_HOME /usr/apps/zeebe-app2

ENV JAVA_OPTS ""

COPY target/$APP_FILE $APPLICATION_HOME/

WORKDIR $APPLICATION_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java $JAVA_OPTS -jar $APP_FILE"]