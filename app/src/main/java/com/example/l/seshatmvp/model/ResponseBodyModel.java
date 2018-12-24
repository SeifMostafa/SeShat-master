package com.example.l.seshatmvp.model;

import java.util.ArrayList;

public class ResponseBodyModel
{
    private ArrayList<responses>responses;

    public ResponseBodyModel(ArrayList<responses> responses) {
        this.responses = responses;
    }

    public ArrayList<responses> getResponses() {
        return responses;
    }

    public void setResponses(ArrayList<responses> responses) {
        this.responses = responses;
    }

    @Override
    public String toString() {
        return "ResponseBodyModel{" +
                "responses=" + responses.toString() +
                '}';
    }
}
