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
}
