package ibb.api.goannotationservice.parser;

/**
 * Represent a line in GO Annotation File (GAF) format 2.2
 * http://geneontology.org/docs/go-annotation-file-gaf-format-2.2/
 */
public class OfficialGOAnnotation {
    public String db;
    public String dbObjectId;
    public String dbObjectSymbol;
    public String qualifier;
    public String goId;
    public String dbReference;
    public String evidence;
    public String withOrFrom;
    public String aspect;
    public String dbObjectName;
    public String dbObjectSynonym;
    public String dbObjectType;
    public String taxon;
    public String date;
    public String assignedBy;
    public String annotationExtension;
    public String geneProductFormId;

    public String toGAFLine() {
        return String.join("\t",
            db != null ? db : "",
            dbObjectId != null ? dbObjectId : "",
            dbObjectSymbol != null ? dbObjectSymbol : "",
            qualifier != null ? qualifier : "",
            goId != null ? goId : "",
            dbReference != null ? dbReference : "",
            evidence != null ? evidence : "",
            withOrFrom != null ? withOrFrom : "",
            aspect != null ? aspect : "",
            dbObjectName != null ? dbObjectName : "",
            dbObjectSynonym != null ? dbObjectSynonym : "",
            dbObjectType != null ? dbObjectType : "",
            taxon != null ? taxon : "",
            date != null ? date : "",
            assignedBy != null ? assignedBy : "",
            annotationExtension != null ? annotationExtension : "",
            geneProductFormId != null ? geneProductFormId : ""
        );
    }
}
