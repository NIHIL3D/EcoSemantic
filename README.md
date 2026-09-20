# EcoSemantic

A small environmental dataset catalogue built with Spring Boot and Blazegraph. It combines metadata from CSV and JSON sources into a shared RDF graph and exposes a REST API to browse datasets and search by variable.

The datasets describe fictional sites A and B. They contain metadata, not actual environmental measurements.

## How it works

1. Read the CSV and JSON files from `src/main/resources/data`.
2. Map their fields to a common model and normalize variable names, such as `soil_temp` to `soil_temperature`.
3. Reject invalid entries, such as a dataset with an empty title.
4. Convert the metadata to RDF with Apache Jena and upload it to Blazegraph through an explicit API call.
5. Query Blazegraph through the REST API to retrieve complete dataset descriptions.

Spring Boot runs on Java 21, while Blazegraph runs in a separate Java 8 container. A Docker volume preserves the graph when containers are recreated.

## Run locally

Requirements: JDK 21 or compatible, Docker Desktop with Linux containers, and Docker Compose. The Blazegraph 2.1.5 JAR must be available at `docker/blazegraph/blazegraph.jar`.

Run these commands from the directory containing `pom.xml` (PowerShell):

```powershell
.\mvnw.cmd package -DskipTests
docker compose up --build -d
docker compose logs --tail=50 app blazegraph
```

Wait for both services to start. Rebuild the JAR and Docker image after changing the application or its bundled data.

- API: http://localhost:8082/api/datasets
- Blazegraph Workbench: http://localhost:9998/blazegraph/

Import the sample metadata:

```powershell
Invoke-WebRequest -UseBasicParsing -Method Post -Uri "http://localhost:8082/api/datasets/rdf"
```

Expected response: **204 No Content**. Before the first import, the catalogue is empty.

## Test

### API checks

```powershell
# All four valid datasets
Invoke-RestMethod "http://localhost:8082/api/datasets"

# Two datasets: a:1 and b:1
Invoke-RestMethod "http://localhost:8082/api/datasets?variable=soil_temperature"

# Two datasets: a:2 and b:2
Invoke-RestMethod "http://localhost:8082/api/datasets?variable=soil_moisture"

# Expected: HTTP 400
Invoke-RestMethod "http://localhost:8082/api/datasets?variable=unknown"
```
