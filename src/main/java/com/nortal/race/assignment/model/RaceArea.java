package com.nortal.race.assignment.model;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priit Liivak
 */
public class RaceArea  implements Serializable {

    //Area size
    private Point maximum;
    private List<Point> targets = new ArrayList<>();

    public RaceArea() {}

    public RaceArea(Point maximum) {
        this.maximum = maximum;
    }

    public List<Point> getTargets() {
        return targets;
    }

    public void addTarget(int x, int y) {
        targets.add(new Point(x, y));
    }

    public int getHeight() {
        return maximum.y;
    }

    public int getWidth() {
        return maximum.x;
    }

    public boolean isWithinBounds(Point position) {
        return position.x > 0 && position.x < maximum.x && position.y > 0 && position.y < maximum.y;
    }



    @Override
    public String toString() {
        return "RaceArea{" +
                "maximum=" + maximum +
                ", targets=" + targets +
                '}';
    }
}
