FROM openjdk:11-jre-slim

ARG APP_NAME=conductor-worker

ENV APP_NAME $APP_NAME

ENV APPLICATION_HOME /usr/apps/$APP_NAME

COPY target/universal/stage $APPLICATION_HOME/

WORKDIR $APPLICATION_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec bin/$APP_NAME"]