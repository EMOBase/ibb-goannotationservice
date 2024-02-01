package ibb.api.goannotationservice;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import io.quarkus.logging.Log;

public class GOAnnotationIndex {

    private static final int MAX_RESULTS = 10000; // Get all results (10000 is the maximum value supported by Elasticsearch)

    private ElasticsearchClient esClient;
    private String index;

    public GOAnnotationIndex(ElasticsearchClient esClient, String index) {
        this.esClient = esClient;
        this.index = index;
    }

    public List<GOAnnotation> findByGenes(List<String> genes) {
        try {
            return esClient.search(s -> s
                .index(index)
                .size(MAX_RESULTS) 
                .query(q -> q
                    .terms(t -> t
                        .field("gene.keyword")
                        .terms(tt -> tt.value(genes.stream().map(FieldValue::of).toList()))
                    )
                )
            , GOAnnotation.class).hits().hits().stream().map(this::mapToGOAnnotation).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GOAnnotation> findByStatus(List<GOAnnotation.Status> statuses) {
        try {
            return esClient.search(s -> s
                .index(index)
                .size(MAX_RESULTS)
                .query(q -> q
                    .terms(t -> t
                        .field("status.keyword")
                        .terms(tt -> tt.value(statuses.stream().map(status -> status.name()).map(FieldValue::of).toList()))
                    )
                )
            , GOAnnotation.class).hits().hits().stream().map(this::mapToGOAnnotation).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GOAnnotation> findByStatus(GOAnnotation.Status status) {
        return findByStatus(List.of(status));
    }

    public long count() {
        try {
            return esClient.count(c -> c.index(index)).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(List<GOAnnotation> anns) {
        try (BulkIngester<Void> ingester = BulkIngester.of(b -> b.client(esClient))) {
            anns.forEach(ann -> {
                if (GOAnnotation.Status.TO_BE_DELETED.equals(ann.status)) {
                    ingester.add(op -> op
                        .delete(d -> d
                            .id(ann.id)
                            .index(index)));
                } else {
                    ingester.add(op -> op
                        .update(u -> u
                            .id(ann.id)
                            .index(index)
                            .action(a -> a.doc(ann))));
                }
            });
        }
    }

    public GOAnnotation create(GOAnnotation goAnnotation) {
        try {
            IndexResponse response = esClient.index(i -> i.index(index).document(goAnnotation));
            goAnnotation.id = response.id();
            return goAnnotation;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void create(Stream<GOAnnotation> anns) {
        try (BulkIngester<Void> ingester = BulkIngester.of(b -> b.client(esClient))) {
            AtomicLong counter = new AtomicLong(0);
            anns.forEach((ann) -> {
                counter.getAndIncrement();
                ingester.add(op -> op
                    .index(idx -> idx
                        .index(index)
                        .document(ann)
                    )
                );
            });
            Log.debugf("Sent %d GO annotations to Elasticsearch", counter.get());
        }
    }

    public void create(List<GOAnnotation> anns) {
        create(anns.stream());
    }

    public void createIndexIfNotExists() {
        try {
            if (!esClient.indices().exists(idx -> idx.index(index)).value()) {
                esClient.indices().create(idx -> idx.index(index));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIndexIfExists() {
        try {
            esClient.indices().delete(idx -> idx.index(index).ignoreUnavailable(true));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshIndex() {
        try {
            esClient.indices().refresh(idx -> idx.index(index));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private GOAnnotation mapToGOAnnotation(Hit<GOAnnotation> hit) {
        GOAnnotation ann = Optional.ofNullable(hit.source()).orElseGet(GOAnnotation::new);
        ann.id = hit.id();
        return ann;
    }
}
