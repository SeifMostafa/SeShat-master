package com.example.l.seshatmvp.model;

public class TextAnnotationsModel
{
    private String locale;
    private String description;
    private BoundingPoly boundingPoly;

    public TextAnnotationsModel(String locale, String description, BoundingPoly boundingPolyObject) {
        this.locale = locale;
        this.description = description;
        this.boundingPoly = boundingPolyObject;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BoundingPoly getBoundingPoly() {
        return boundingPoly;
    }

    public void setBoundingPoly(BoundingPoly boundingPoly) {
        this.boundingPoly = boundingPoly;
    }
}
