# saf-planner-backend
Backend repo for Short Animation Film Planner

## Development

1. Create a .env file and define the necessary variables (see application-dev.yml to see which variables)
2. Create a Docker compose file somewhere:
```yaml
services:
  postgres:
    container_name: <choose a container name>
    image: postgres
    environment:
      - POSTGRES_USER=<user matching the variable in .env>
      - POSTGRES_PASSWORD=<password matching the variable in .env>
    volumes:
      - ./postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
```