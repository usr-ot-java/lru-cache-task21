## How to run?

### Run in Docker

Firstly, it is required to build the project using Docker and the following command below:
```bash
docker build -t lru-cache .
```

Finally, run the Docker container:
```bash
docker run -p 8080:8080 -e SERVER_PORT=8080 -e CACHE_CAPACITY="10" -e CACHE_PERSIST_TIME="10" -e CACHE_PERSIST_PATH="cache-data-persist" -e CACHE_INIT_PATH="data/cache-data-init" lru-cache
```