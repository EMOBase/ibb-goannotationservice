package ibb.api.goannotationservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import ibb.api.goannotationservice.parser.GAFParser;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class GOAnnotationIndexFactory {
    
    @Inject
    ElasticsearchClient esClient;

    @Inject
    Config config;

    void init(@Observes StartupEvent ev) {
        Set<String> species = config.species().keySet().stream().collect(Collectors.toSet());
        species.add(config.target().species());
        Log.debug("Start");
        species.forEach(name -> {
            GOAnnotationIndex index = get(name);
            if (config.elasticsearch().deleteIndexOnStart()) {
                index.deleteIndexIfExists();
            }
            index.createIndexIfNotExists();
            Log.debugf("Using index %s", name);
        });

        species.forEach(name -> {
            if (config.species().containsKey(name)) {
                String gafFile = config.species().get(name).localGaf();
                GOAnnotationIndex index = get(name);
                if (index.count() == 0) {
                    insertGOAnnotationsFromGAFFile(gafFile, index);
                }
            }
        });
    }

    public GOAnnotationIndex get(String species) {
        String indexPrefix = config.elasticsearch().indexPrefix();
        return new GOAnnotationIndex(esClient, indexPrefix + "-" + species);
    }

    private void insertGOAnnotationsFromGAFFile(String gafFile, GOAnnotationIndex index) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(gafFile);
        try (GAFParser parser = new GAFParser(new PossiblyCompressedInputStream(is))) {
            index.create(parser.stream().map(GOAnnotation::fromOfficialGoAnnotation));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
