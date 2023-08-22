package ibb.api;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.Length;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import ibb.api.parser.OfficialGOAnnotation;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "GOAnnotation")
public class GOAnnotation {
    public static enum Status {
        UNREVIEWED, REVIEWED, OFFICIAL, TO_BE_DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @Column(name = "gene_id")
    private String geneId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "go_term_id", insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private GOTerm goTerm;

    @NotBlank
    @Column(name = "go_term_id")
    private String goTermId;

    @NotBlank
    private String evidence;
    
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private String reference;

    @Column(length = Length.LONG)
    private String quotation;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "submitter_name")),
        @AttributeOverride(name = "email", column = @Column(name = "submitter_email")),
        @AttributeOverride(name = "shown", column = @Column(name = "submitter_shown")),
    })
    @NotNull
    private Submitter submitter;

    @CreationTimestamp
    @Schema(implementation = Long.class)
    private Instant createdAt;

    @UpdateTimestamp
    @Schema(implementation = Long.class)
    private Instant updatedAt;

    public static GOAnnotation fromOfficialGoAnnotation(OfficialGOAnnotation official) {
        GOAnnotation ann = new GOAnnotation();
        ann.setGeneId(official.getDbObjectId());
        ann.setGoTermId(official.getGoId());
        ann.setEvidence(official.getEvidence());
        ann.setReference(official.getDbReference());

        Submitter submitter = new Submitter();
        submitter.setName(official.getAssignedBy());
        submitter.setShown(true);
        ann.setSubmitter(submitter);
        return ann;
    }
    public OfficialGOAnnotation toOfficialGOAnnotation() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public GOTerm getGoTerm() {
        return goTerm;
    }

    public void setGoTerm(GOTerm goTerm) {
        this.goTerm = goTerm;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public Submitter getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Submitter submitter) {
        this.submitter = submitter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGoTermId() {
        return goTermId;
    }

    public void setGoTermId(String goTermId) {
        this.goTermId = goTermId;
    }
}
