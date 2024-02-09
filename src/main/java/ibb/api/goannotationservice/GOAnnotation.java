package ibb.api.goannotationservice;

import com.fasterxml.jackson.annotation.JsonInclude;

import ibb.api.goannotationservice.parser.OfficialGOAnnotation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GOAnnotation {
    public static interface ValidationGroups {
        public static interface Create {}
        public static interface Update {}
    }

    public static enum Status {
        UNREVIEWED, INTERNAL, OFFICIAL, TO_BE_DELETED
    }

    public static class Term {
        @NotBlank
        public String id;

        @NotBlank
        public String name;

        @NotBlank
        public String aspect;
    }

    @Null(groups = ValidationGroups.Create.class)
    @NotNull(groups = ValidationGroups.Update.class)
    public String id;

    @NotBlank(groups = ValidationGroups.Create.class)
    public String gene;
    
    @Null(groups = ValidationGroups.Create.class)
    public Status status;

    @NotNull(groups = ValidationGroups.Create.class)
    @Valid
    public Term term;

    @NotBlank(groups = ValidationGroups.Create.class)
    public String geneProduct;

    @NotBlank(groups = ValidationGroups.Create.class)
    public String evidence;
    
    @NotBlank(groups = ValidationGroups.Create.class)
    public String reference;

    public String quotation;

    public String taxon;

    public String date;

    @NotBlank(groups = ValidationGroups.Create.class)
    public String lab;

    public static GOAnnotation fromOfficialGoAnnotation(OfficialGOAnnotation official) {
        GOAnnotation ann = new GOAnnotation();
        ann.gene = official.dbObjectId;
        ann.term = new Term() {{
            id = official.goId;
            aspect = official.aspect;
        }};
        ann.geneProduct = official.dbObjectType;
        ann.evidence = official.evidence;
        ann.reference = official.dbReference;
        ann.date = official.date;
        ann.taxon = official.taxon;
        ann.status = Status.OFFICIAL;
        return ann;
    }
    public OfficialGOAnnotation toOfficialGOAnnotation() {
        OfficialGOAnnotation official = new OfficialGOAnnotation();
        official.dbObjectId = gene;
        official.goId = term.id;
        official.aspect = term.aspect;
        official.dbObjectType = geneProduct;
        official.evidence = evidence;
        official.dbReference = reference;
        official.date = date;
        official.taxon = taxon;
        return official;
    }
}
