package ibb.api;

import jakarta.persistence.Embeddable;

@Embeddable
public class Submitter {
    private String name;
    private String email;
    private Boolean shown;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean getShown() {
        return shown;
    }
    public void setShown(Boolean shown) {
        this.shown = shown;
    }
}
