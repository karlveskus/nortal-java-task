package com.nortal.race.assignment.model;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Priit Liivak
 */
public class Vessel implements Serializable {

    /**
     * Positive speed indicates moving forward on given axis (increasing location)
     * Negative speed indicates moving backward on given axis (decreasing location)
     */
    private double speedX = 0;
    /**
     * Positive speed indicates moving forward on given axis (increasing location)
     * Negative speed indicates moving backward on given axis (decreasing location)
     */
    private double speedY = 0;

    private Point position = new Point(1, 1);

    public Vessel() {
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Vessel{" +
                "speedX=" + speedX +
                ", speedY=" + speedY +
                ", position=" + (position == null ? "null" : (position.x + ";" + position.y)) +
                '}';
    }
}

