package com.example.l.seshatmvp.model;

import java.util.ArrayList;

public class responses {
    private ArrayList<TextAnnotationsModel> TextAnnotations;
    private FullTextAnnotationModel fullTextAnnotation;

    public responses(ArrayList<TextAnnotationsModel> textAnnotations, FullTextAnnotationModel fullTextAnnotation) {
        TextAnnotations = textAnnotations;
        this.fullTextAnnotation = fullTextAnnotation;
    }

    public ArrayList<TextAnnotationsModel> getTextAnnotations() {
        return TextAnnotations;
    }

    public void setTextAnnotations(ArrayList<TextAnnotationsModel> textAnnotations) {
        TextAnnotations = textAnnotations;
    }

    public FullTextAnnotationModel getFullTextAnnotation() {
        return fullTextAnnotation;
    }

    public void setFullTextAnnotation(FullTextAnnotationModel fullTextAnnotation) {
        this.fullTextAnnotation = fullTextAnnotation;
    }

    @Override
    public String toString() {
        return "responses{" +
                "TextAnnotations=" + TextAnnotations.toString() +
                ", fullTextAnnotation=" + fullTextAnnotation.toString() +
                '}';
    }
}
