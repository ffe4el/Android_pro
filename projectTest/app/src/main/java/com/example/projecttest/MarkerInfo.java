package com.example.projecttest;
import java.io.Serializable;

public class MarkerInfo implements Serializable {
    private String address;
    private double area;
    private int user;
    private int fan;
    private int air;

    public MarkerInfo(String address, double area, int user, int fan, int air) {
        this.address = address;
        this.area = area;
        this.user = user;
        this.fan = fan;
        this.air = air;
    }

    public String getAddress() {
        return address;
    }

    public double getArea() {
        return area;
    }

    public int getUser() {
        return user;
    }

    public int getFan() {
        return fan;
    }

    public int getAir() {
        return air;
    }
}
