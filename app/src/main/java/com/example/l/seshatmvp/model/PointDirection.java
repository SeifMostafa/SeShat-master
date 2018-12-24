package com.example.l.seshatmvp.model;

import java.util.ArrayList;

public class PointDirection {
    private ArrayList<Direction> directions;

    public PointDirection(ArrayList<Direction> directions) {
        this.directions = directions;
    }

    public PointDirection() {
        this.directions = new ArrayList<>();
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public void setDirections(ArrayList<Direction> directions) {
        this.directions = directions;
    }
}
