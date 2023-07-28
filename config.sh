#! /usr/bin/bash
echo "Hello World"

#pip3 install yq
pip3 install shyaml

python config.py

# Path to the YAML file
yaml_file="config.yaml"

# Read YAML file using yq
#yq -r . config.yaml
database_provider=$(cat config.yaml | shyaml get-value 'config.database.provider')
database_type=$(cat config.yaml | shyaml get-value 'config.database.type')


services_provider=$(cat config.yaml | shyaml get-value 'config.services.provider')
services_infrastructure=$(cat config.yaml | shyaml get-value 'config.services.infrastructure')

#yaml=$(shyaml get-value 'config')
echo "${database_provider}"
echo "${database_type}"

echo "${services_provider}"
echo "${services_infrastructure}"

if [ "${database_provider}" = "on-premise" ]
then
  echo Create database docker-compose

fi
