# ibb-go

Service to handle Gene Ontology terms and annotations

This project uses [Quarkus](https://quarkus.io/).

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
quarkus dev
```

## Create initial data

If this is the first time you run the the service and there are no data submitted from users,
you can add initial GO terms and GO annotations from Gene Ontology Consortium.
Otherwise, you should import data from existing database.

Downloads:

 - http://current.geneontology.org/annotations/fb.gaf.gz (FlyBase GO Annotations)
 - http://purl.obolibrary.org/obo/go.obo (GO term definitions)

Run service in dev mode:

```shell script
quarkus dev
```

Upload data:

```shell script
curl -X 'POST' \
  'http://localhost:8080/go-annotations/official' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@fb.gaf.gz;type=application/x-gzip'

curl -X 'POST' \
  'http://localhost:8080/go-terms' \
  -H 'accept: */*' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@go.obo'
```

Save the database:

```shell script
docker exec -it [container_id] /bin/bash

mysqldump -u quarkus -h0.0.0.0 -p quarkus --no-tablespaces | gzip > geneontologyservice.sql.gz
```

The default password in dev mode is `quarkus`
