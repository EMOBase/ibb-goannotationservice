package ibb.api;

import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GOTermRepository {
    
    @Inject
    EntityManager entityManager;

    @ConfigProperty(name = "quarkus.hibernate-orm.jdbc.statement-batch-size")
    int batchSize;

    @Inject
    Logger logger;

    public void create(List<GOTerm> goTerms) {
        logger.debugv("Start bulk insert of {0} entities", goTerms.size());
        for (int i = 0; i < goTerms.size(); i++) {
            entityManager.persist(goTerms.get(i));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                logger.debugv("\tInserted: {0}", i);
            }
        }
    }

    public Optional<GOTerm> findByIdOptional(String id) {
        return Optional.ofNullable(entityManager.find(GOTerm.class, id));
    }
}
