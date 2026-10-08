FROM node:lts-alpine AS build-front
WORKDIR /app

COPY ./frontend .
RUN npm install && npm run build:prod

FROM nginx:1.29.4-alpine-slim

COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build-front /app/dist/ /var/www/html/main/

EXPOSE 5450
