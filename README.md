# Gene Ontology Annotation Service for the iBeetle-Base project

## Development

Prerequisites:
- [quarkus-cli](https://quarkus.io/guides/cli-tooling) is installed.

To run the application in dev mode, run:
```bash
quarkus dev
```

These commands might be useful after a long time not working on this project:

- `quarkus update`: Update all quarkus dependencies to latest versions
- `./mvnw -N wrapper:wrapper -Dmaven=3.8.7`: Update maven wrapper to new version, in this case `3.8.7`

## Deployment

Images are published to GHCR by `.github/workflows/publish.yml` as native
executables for `linux/amd64` and `linux/arm64`:

```
ghcr.io/emobase/ibb-goannotationservice:<tag>
```

To build locally (needs Docker; the native build runs in a builder container
and produces a binary for the host's architecture):

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true -DskipTests
docker build -f src/main/docker/Dockerfile.native-micro -t emobase/ibb-goannotationservice:local .
```

The container needs an Elasticsearch instance at `es:9200`. Set
`GOANNOTATIONSERVICE_ELASTICSEARCH_INDEX_PREFIX` to namespace the indices it
creates; the default is `goannotationservice-annotations`.
