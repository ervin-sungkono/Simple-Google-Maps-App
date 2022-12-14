package com.example.gslclocationbasedservice.model;

import com.google.android.gms.maps.model.LatLng;

public class Position {
    int id;
    LatLng point;

    public Position(int id, LatLng point) {
        this.id = id;
        this.point = point;
    }

    public int getId() {
        return id;
    }

    public LatLng getPoint() {
        return point;
    }
}
