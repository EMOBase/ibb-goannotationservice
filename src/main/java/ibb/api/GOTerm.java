package ibb.api;

import ibb.api.parser.OBOStanza;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GOTerm")
public class GOTerm {

    @Id
    private String id;

    private String name;
    private String aspect;

    public static GOTerm fromOBOStanza(OBOStanza oboStanza) {
        GOTerm goTerm = new GOTerm();
        goTerm.setId(oboStanza.getId());
        goTerm.setName(oboStanza.getAttribute("name"));
        goTerm.setAspect(oboStanza.getAttribute("namespace"));
        return goTerm;

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAspect() {
        return aspect;
    }
    public void setAspect(String aspect) {
        this.aspect = aspect;
    }
}
