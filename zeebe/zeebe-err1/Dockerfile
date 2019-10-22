FROM openjdk:8-jre-alpine

# Install bash
RUN apk add --no-cache bash

ARG APP_NAME=zeebe-err1

ENV APP_NAME $APP_NAME

ENV APPLICATION_HOME /usr/apps/$APP_NAME

COPY target/universal/stage $APPLICATION_HOME/
COPY repository $APPLICATION_HOME/repository/

WORKDIR $APPLICATION_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec bin/$APP_NAME"]