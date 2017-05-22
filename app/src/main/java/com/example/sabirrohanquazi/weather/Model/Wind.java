package com.example.sabirrohanquazi.weather.Model;

/**
 * Created by Sabir Rohan Quazi on 2017-05-07.
 */

public class Wind {

    private double speed;
    private double deg;


    public Wind(double speed, double deg) {
        this.speed = speed;
        this.deg = deg;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
