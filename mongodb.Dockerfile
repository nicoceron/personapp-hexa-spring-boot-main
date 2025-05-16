FROM mongo:latest

COPY scripts/persona_ddl_mongo.js /docker-entrypoint-initdb.d/01-schema.js
COPY scripts/persona_dml_mongo.js /docker-entrypoint-initdb.d/02-data.js

# Set MongoDB to use authentication
ENV MONGO_INITDB_ROOT_USERNAME=root
ENV MONGO_INITDB_ROOT_PASSWORD=root
ENV MONGO_INITDB_DATABASE=persona_db 