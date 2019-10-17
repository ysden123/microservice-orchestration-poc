FROM openjdk:8-jre-alpine

# Install bash
RUN apk add --no-cache bash

ARG APP_NAME=zeebe-dmn1

ENV APP_NAME $APP_NAME

ENV APPLICATION_HOME /usr/apps/$APP_NAME

COPY target/universal/stage $APPLICATION_HOME/

WORKDIR $APPLICATION_HOME/bin
ENTRYPOINT ["sh", "-c"]
CMD ["exec ./$APP_NAME"]