# demo-service

## Description

demo service for testing Redis and OpenTelemetry  

## OpenTelemetry

The project uses OpenTelemetry for tracing. Metric, traces and logs are sent to OpenTelemetry Collector.

For testing purposes, docker compose file is provided to bring up an instance of Grafana (using [grafana/otlp-lgtm](https://github.com/grafana/docker-otel-lgtm?tab=readme-ov-file#docker-otel-lgtm) image)
integrated with Prometheus, Tempo and Loki as well as Redis instance

Grafana is available at http://localhost:3000 (credentials: admin admin)

### Running OpenTelemetry & Redis 

```
docker-compose up -d
```

RediSearch index is required to be created in Redis, like so: 
```
docker exec -it redis_db redis-cli
FT.CREATE myIdx ON HASH PREFIX 1 my.students. SCHEMA User-Name TAG SORTABLE
```

### How to run application

1. In the terminal, position in directory where this **README.md** is located. Then run:
```
mvn clean package
```
this will copy the opentelemetry-javaagent.jar under target/lib folder & will create an application jar 

2. From same directory run:
```
java -javaagent:.\target\lib\opentelemetry-javaagent.jar -jar .\target\demo-0.0.1-SNAPSHOT.jar -Dotel.traces.exporter=otlp -Dotel.metrics.exporter=otlp -Dotel.logs.exporter=otlp -Dotel.instrumentation.lettuce.experimental-span-attributes=true -Dotel.instrumentation.lettuce.connection-telemetry.enabled=true -Dotel.instrumentation.common.default-enabled=true -Dotel.instrumentation.lettuce.enabled=true -Dotel.service.name=demo-otel-redis -Dotel.exporter.endpoint=http://localhost:4317
```
3. Invoke an endpoint
```
GET http://localhost:8080/test/search
```

### Tech stack

![](https://img.shields.io/badge/Spring%20Boot%20-3.2.3-brightgreen?logo=springboot&logoColor=green&labelColor=compatible-green)
![](https://img.shields.io/badge/java-jdk17-white?logo=openjdk&logoColor=black&labelColor=blue)
![](https://img.shields.io/badge/redis-7.0.15-black?logo=redis&logoColor=black&labelColor=red)
![](https://img.shields.io/badge/apache%20maven-3.8.6-yellow?logo=apachemaven&logoColor=red&labelColor=white)
![](https://img.shields.io/badge/Docker-26.0.0-brightgreen?logo=docker&logoColor=blue)