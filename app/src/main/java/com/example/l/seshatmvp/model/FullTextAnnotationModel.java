package com.example.l.seshatmvp.model;

import java.util.ArrayList;

public class FullTextAnnotationModel {
    private ArrayList<PageModel>pages;
    private String text;

    public FullTextAnnotationModel(ArrayList<PageModel> pages, String text) {
        this.pages = pages;
        this.text = text;
    }

    public ArrayList<PageModel> getPages() {
        return pages;
    }

    public void setPages(ArrayList<PageModel> pages) {
        this.pages = pages;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "FullTextAnnotationModel{" +
                "pages=" + pages +
                ", text='" + text + '\'' +
                '}';
    }
}
