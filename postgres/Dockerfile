FROM postgres:12.3-alpine
COPY setup.sql /docker-entrypoint-initdb.d/
COPY z_insert_default_values.sql /docker-entrypoint-initdb.d/