package me.flyray.bsin.domain.entity;

public class BsinFormSaveRepresentation {
    protected boolean reusable;
    protected boolean newVersion;
    protected String comment;
    protected String formImageBase64;
    protected BsinFormRepresentation formRepresentation;

    public BsinFormSaveRepresentation() {
    }

    public boolean isReusable() {
        return this.reusable;
    }

    public void setReusable(boolean reusable) {
        this.reusable = reusable;
    }

    public boolean isNewVersion() {
        return this.newVersion;
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFormImageBase64() {
        return this.formImageBase64;
    }

    public void setFormImageBase64(String formImageBase64) {
        this.formImageBase64 = formImageBase64;
    }

    public BsinFormRepresentation getFormRepresentation() {
        return this.formRepresentation;
    }

    public void setFormRepresentation(BsinFormRepresentation formRepresentation) {
        this.formRepresentation = formRepresentation;
    }
}
