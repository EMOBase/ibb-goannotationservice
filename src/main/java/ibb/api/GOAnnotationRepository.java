package ibb.api;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GOAnnotationRepository {
    
    @Inject
    EntityManager em;

    private static final int BATCH_SIZE = 50; // Must be same as JDBC batch size in application.propperties

    public List<GOAnnotation> findByGeneIds(List<String> geneIds) {
        return em.createQuery(
                "select a from GOAnnotation a " +
                "left join fetch a.goTerm " +
                "where a.geneId in :geneIds",
            GOAnnotation.class)
            .setParameter("geneIds", geneIds)
            .getResultList();
    }

    public List<GOAnnotation> findByStatus(List<GOAnnotation.Status> statuses) {
        return em.createQuery(
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
        return em.createQuery(
                "delete GOAnnotation " +
                "where id in :ids"
            )
            .setParameter("ids", ids)
            .executeUpdate();
    }

    public int updateStatus(List<Long> ids, GOAnnotation.Status status) {
        return em.createQuery(
                "update GOAnnotation a " +
                "set a.status = :status " +
                "where id in :ids"
            )
            .setParameter("status", status)
            .setParameter("ids", ids)
            .executeUpdate();
    }

    public GOAnnotation create(GOAnnotation goAnnotation) {
        em.persist(goAnnotation);
        return goAnnotation;
    }

    public void create(List<GOAnnotation> goAnnotations) {
        for (int i = 0; i < goAnnotations.size(); i++) {
            em.persist(goAnnotations.get(i));
            if (i % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}
