#!/bin/sh

exec java -jar backend-0.0.1.jar  \
    --spring.datasource.username="$(cat "$POSTGRES_USER_FILE")" \
    --spring.datasource.password="$(cat "$POSTGRES_PASSWORD_FILE")"