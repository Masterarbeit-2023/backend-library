def generate_docker_compose_database():
    db_name = 'mydatabase'
    db_user = 'myuser'
    db_password = 'mypassword'

    # Create the Docker Compose file
    with open('docker-compose.yml', 'w') as f:
        f.write('version: "3"\n')
        f.write('services:\n')
        f.write('  db:\n')
        f.write('    image: postgres:latest\n')
        f.write('    restart: always\n')
        f.write(f'    environment:\n')
        f.write(f'      POSTGRES_DB: {db_name}\n')
        f.write(f'      POSTGRES_USER: {db_user}\n')
        f.write(f'      POSTGRES_PASSWORD: {db_password}\n')
        f.write('    ports:\n')
        f.write('      - 5432:5432\n')
        f.write('    volumes:\n')
        f.write('      - ./data:/var/lib/postgresql/data\n')

    print("Docker Compose file generated successfully.")

# Generate the Docker Compose file
generate_docker_compose()
