FROM node:10.21.0-alpine3.11 as FrontEnd
WORKDIR /app
COPY FrontEndApp ./FrontEndApp
WORKDIR /app/FrontEndApp
RUN npm install
RUN npm run-script build

FROM ubuntu as BackEnd
ENV DEBIAN_FRONTEND=noninteractive
WORKDIR /app
COPY WebServer ./WebServer
WORKDIR /app/WebServer
RUN apt-get update && apt-get install -y postgresql-12 maven sudo
RUN useradd -m docker && echo "docker:docker" | chpasswd && adduser docker sudo

COPY postgres/setup.sql app/postgres/setup.sql 
ENV PGPASSWORD 12345    
RUN pg_ctlcluster 12 main start &&\
    sudo -u postgres psql -c "CREATE USER admin WITH PASSWORD '12345' SUPERUSER;" &&\
    sudo -u postgres psql -c "CREATE DATABASE orgs_and_workers;" &&\
    sudo -u postgres psql --dbname orgs_and_workers -f app/postgres/setup.sql &&\
    mvn generate-sources  &&\
    mvn package -Dmaven.test.skip=true -Dspring.datasource.url=dbc:postgresql://postgres:5432/orgs_and_workers 
USER docker
COPY --from=FrontEnd /app/WebServer/static /app/WebServer/target/static

FROM openjdk:slim
COPY --from=BackEnd /app/WebServer/target /app
CMD java -jar /app/WebServer-0.0.1-SNAPSHOT.jar
