package ibb.api.parser;

/**
 * Represent a line in GO Annotation File (GAF) format 2.2
 * http://geneontology.org/docs/go-annotation-file-gaf-format-2.2/
 */
public class OfficialGOAnnotation {
    private String db;
    private String dbObjectId;
    private String dbObjectSymbol;
    private String qualifier;
    private String goId;
    private String dbReference;
    private String evidence;
    private String withOrFrom;
    private String aspect;
    private String dbObjectName;
    private String dbObjectSynonym;
    private String dbObjectType;
    private String taxon;
    private String date;
    private String assignedBy;
    private String annotationExtension;
    private String geneProductFormId;
    
    public String getDb() {
        return db;
    }
    public void setDb(String db) {
        this.db = db;
    }
    public String getDbObjectId() {
        return dbObjectId;
    }
    public void setDbObjectId(String dbObjectId) {
        this.dbObjectId = dbObjectId;
    }
    public String getDbObjectSymbol() {
        return dbObjectSymbol;
    }
    public void setDbObjectSymbol(String dbObjectSymbol) {
        this.dbObjectSymbol = dbObjectSymbol;
    }
    public String getQualifier() {
        return qualifier;
    }
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }
    public String getGoId() {
        return goId;
    }
    public void setGoId(String goId) {
        this.goId = goId;
    }
    public String getDbReference() {
        return dbReference;
    }
    public void setDbReference(String dbReference) {
        this.dbReference = dbReference;
    }
    public String getEvidence() {
        return evidence;
    }
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
    public String getWithOrFrom() {
        return withOrFrom;
    }
    public void setWithOrFrom(String withOrFrom) {
        this.withOrFrom = withOrFrom;
    }
    public String getAspect() {
        return aspect;
    }
    public void setAspect(String aspect) {
        this.aspect = aspect;
    }
    public String getDbObjectName() {
        return dbObjectName;
    }
    public void setDbObjectName(String dbObjectName) {
        this.dbObjectName = dbObjectName;
    }
    public String getDbObjectSynonym() {
        return dbObjectSynonym;
    }
    public void setDbObjectSynonym(String dbObjectSynonym) {
        this.dbObjectSynonym = dbObjectSynonym;
    }
    public String getDbObjectType() {
        return dbObjectType;
    }
    public void setDbObjectType(String dbObjectType) {
        this.dbObjectType = dbObjectType;
    }
    public String getTaxon() {
        return taxon;
    }
    public void setTaxon(String taxon) {
        this.taxon = taxon;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getAssignedBy() {
        return assignedBy;
    }
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    public String getAnnotationExtension() {
        return annotationExtension;
    }
    public void setAnnotationExtension(String annotationExtension) {
        this.annotationExtension = annotationExtension;
    }
    public String getGeneProductFormId() {
        return geneProductFormId;
    }
    public void setGeneProductFormId(String geneProductFormId) {
        this.geneProductFormId = geneProductFormId;
    }
}
