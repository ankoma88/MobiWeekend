package com.ankoma88.mobiweekend;


import java.io.Serializable;

public class Place implements Serializable {
    private String placeId;
    private String placeFullName;

    public Place() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceFullName() {
        return placeFullName;
    }

    public void setPlaceFullName(String placeFullName) {
        this.placeFullName = placeFullName;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeId='" + placeId + '\'' +
                ", placeFullName='" + placeFullName + '\'' +
                '}';
    }
}
