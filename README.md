# saf-planner-backend
Backend repo for Short Animation Film Planner

## Development

Create a Docker compose file for the database somewhere:
```yaml
services:
  postgres:
    container_name: <choose a container name>
    image: postgres:16
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - ./postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
```

Run the database container:
```bash
docker compose up
```

Run the backend application:
```bash
./gradlew bootRun
```
