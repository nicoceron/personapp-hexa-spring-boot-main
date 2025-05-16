FROM mariadb:latest

# Install mariadb-client to ensure mysqladmin and other tools are available
USER root
RUN apt-get update && apt-get install -y mariadb-client procps && rm -rf /var/lib/apt/lists/*
USER mysql

COPY scripts/persona_ddl_maria.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY scripts/persona_dml_maria.sql /docker-entrypoint-initdb.d/02-data.sql
 
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=persona_db
ENV MYSQL_USER=persona_db
ENV MYSQL_PASSWORD=persona_db 