![CI](https://github.com/ankokovin/Fullstack-Test/workflows/CI/badge.svg) [![codecov](https://codecov.io/gh/ankokovin/Fullstack-Test/branch/master/graph/badge.svg)](https://codecov.io/gh/ankokovin/Fullstack-Test)

# Приложение "Организации и работники"

## Запуск

### Docker-compose

1. Склонировать репозиторий
2. В терминале в папке с репозиторием:
```bash
docker-compose up 
```
3. Открыть в браузере _localhost:3000_

### Сборка

1. Склонировать репозиторий.
2. Postgres
    - Должен быть доступен через localhost:5432
      - Можно поменять параметры в [application-dev.properties](https://github.com/ankokovin/Fullstack-Test/blob/master/WebServer/src/main/resources/application-dev.properties)
    - Создать пользователя admin c паролем 12345
      - Можно поменять параметры в [application-dev.properties](https://github.com/ankokovin/Fullstack-Test/blob/master/WebServer/src/main/resources/application-dev.properties)
    - Создать базу данных orgs_and_workers
      - Можно поменять параметры в [application-dev.properties](https://github.com/ankokovin/Fullstack-Test/blob/master/WebServer/src/main/resources/application-dev.properties)
    - Запустить [setup.sql](https://github.com/ankokovin/Fullstack-Test/blob/master/postgres/setup/setup.sql) для задания схемы данных
    - Запустить [z_insert_default_values.sql](https://github.com/ankokovin/Fullstack-Test/blob/master/postgres/setup/z_insert_default_values.sql) для задания случайных начальных данных
3. Фронтенд
```bash
cd FrontEndApp
npm install
npm run-script build
```
4. WebServer
- Idea:
    - Сгенерировать код (jOOQ)
    - Собрать и запустить
- Командная строка:
```bash    
cd WebServer
mvn generate-sources spring-boot:run
```
