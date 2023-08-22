package ibb.api;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GOAnnotationRepository {
    
    @Inject
    EntityManager entityManager;

    @ConfigProperty(name = "quarkus.hibernate-orm.jdbc.statement-batch-size")
    int batchSize;

    @Inject
    Logger logger;

    public List<GOAnnotation> findByGeneIds(List<String> geneIds) {
        return entityManager.createQuery(
                "select a from GOAnnotation a " +
                "left join fetch a.goTerm " +
                "where a.geneId in :geneIds",
            GOAnnotation.class)
            .setParameter("geneIds", geneIds)
            .getResultList();
    }

    public List<GOAnnotation> findByStatus(List<GOAnnotation.Status> statuses) {
        return entityManager.createQuery(
                "select a from GOAnnotation a " +
                "left join fetch a.goTerm " +
                "where a.status in :statuses",
                GOAnnotation.class 
            )
            .setParameter("statuses", statuses)
            .getResultList();
    }

    public List<GOAnnotation> findByStatus(GOAnnotation.Status status) {
        return findByStatus(List.of(status));
    }

    public int delete(List<Long> ids) {
        return entityManager.createQuery(
                "delete GOAnnotation " +
                "where id in :ids"
            )
            .setParameter("ids", ids)
            .executeUpdate();
    }

    public int updateStatus(List<Long> ids, GOAnnotation.Status status) {
        return entityManager.createQuery(
                "update GOAnnotation a " +
                "set a.status = :status " +
                "where id in :ids"
            )
            .setParameter("status", status)
            .setParameter("ids", ids)
            .executeUpdate();
    }

    public GOAnnotation create(GOAnnotation goAnnotation) {
        entityManager.persist(goAnnotation);
        return goAnnotation;
    }

    public void create(List<GOAnnotation> goAnnotations) {
        logger.debugv("Start bulk insert of {0} entities", goAnnotations.size());
        for (int i = 0; i < goAnnotations.size(); i++) {
            entityManager.persist(goAnnotations.get(i));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
                logger.debugv("\tInserted: {0}", i);
            }
        }
    }
}
