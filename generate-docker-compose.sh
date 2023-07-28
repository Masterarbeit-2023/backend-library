#!/bin/bash

if [ "$1" = "postgresql" ]
then
  # Database credentials
  DB_NAME_CONSTANT="POSTGRES_DB"
  DB_USER_CONSTANT="POSTGRES_USER"
  DB_PASSWORD_CONSTANT="POSTGRES_PASSWORD"

  DB_NAME="mydatabase"
  DB_USER="myuser"
  DB_PASSWORD="mypassword"
  DB_VOLUME="/var/lib/postgresql/data"
  DB_PORT=5432
  DB_IMAGE="postgres"
elif [ "$1" = "mysql" ]
then
  # Database credentials
    DB_NAME_CONSTANT="MYSQL_DATABASE"
    DB_USER_CONSTANT="MYSQL_USER"
    DB_PASSWORD_CONSTANT="MYSQL_PASSWORD"

    DB_NAME="mydatabase"
    DB_USER="myuser"
    DB_PASSWORD="mypassword"
    DB_VOLUME="/var/lib/mysql"
    DB_PORT=3306
    DB_IMAGE="mysql:latest"
fi


# Generate the Docker Compose file
cat <<EOF > docker-compose.yml
version: '3'
services:
  db:
    image: $DB_IMAGE
    restart: always
    environment:
      $DB_NAME_CONSTANT: $DB_NAME
      $DB_USER_CONSTANT: $DB_USER
      $DB_PASSWORD_CONSTANT: $DB_PASSWORD
    volumes:
      - ./data:$DB_VOLUME
    ports:
      - $DB_PORT:$DB_PORT

EOF

echo "Docker Compose file generated successfully."
