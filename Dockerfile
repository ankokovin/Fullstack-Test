FROM node:10.21.0-alpine3.11 as FrontEnd
COPY FrontEndApp /app/FrontEndApp
WORKDIR /app/FrontEndApp
RUN npm install
RUN npm run-script build

FROM ubuntu as BackEnd
ENV DEBIAN_FRONTEND=noninteractive
COPY WebServer /app/WebServer
WORKDIR /app/WebServer
RUN apt-get update && apt-get install -y postgresql-12 maven sudo
RUN useradd -m docker && echo "docker:docker" | chpasswd && adduser docker sudo
COPY postgres/setup/setup.sql app/postgres/setup.sql 
ENV PGPASSWORD 12345
RUN echo "\nlocalhost postgres" >> /etc/hosts    
RUN pg_ctlcluster 12 main start &&\
    sudo -u postgres psql -c "CREATE USER admin WITH PASSWORD '12345' SUPERUSER;" &&\
    sudo -u postgres psql -c "CREATE DATABASE orgs_and_workers;" &&\
    sudo -u postgres psql --dbname orgs_and_workers -f app/postgres/setup.sql &&\
    mvn generate-sources -P prod &&\
    mvn package -Dmaven.test.skip=true -P prod
USER docker
COPY --from=FrontEnd /app/WebServer/static /app/WebServer/target/static

FROM openjdk:slim
COPY --from=BackEnd /app/WebServer/target /app
WORKDIR /app
CMD java -jar WebServer-0.0.1-SNAPSHOT.jar
