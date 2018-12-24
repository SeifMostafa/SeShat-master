package com.example.l.seshatmvp.model;

import java.util.ArrayList;

public class BoundingPoly {
    private ArrayList<VerticesModel> vertices;

    public BoundingPoly(ArrayList<VerticesModel> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<VerticesModel> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<VerticesModel> vertices) {
        this.vertices = vertices;
    }
}
