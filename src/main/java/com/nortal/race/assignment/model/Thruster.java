package com.nortal.race.assignment.model;

import java.io.Serializable;

/**
 * Created by Priit Liivak
 */
public enum Thruster implements Serializable {

    LEFT(1),
    FRONT(-1),
    RIGHT(-1),
    BACK(1);

    /**
     * Acceleration modifier indicates how specific thruster affects vessel acceleration.
     * <ul>
     * <li>If LEFT thruster is burning, vessel moves to the right. Positive acceleration indicates increase of coordinate value</li>
     * <li>If FRONT thruster is burning, vessel moves backward. Negative acceleration indicates decrease of coordinate value</li>
     * <li>If RIGHT thruster is burning, vessel moves to the left. Negative acceleration indicates decrease of coordinate value</li>
     * <li>If BACK thruster is burning, vessel moves forward. Positive acceleration indicates increase of coordinate value</li>
     * </ul>
     *
     */
    private int accelerationModifier;

    Thruster(int accelerationModifier) {
        this.accelerationModifier = accelerationModifier;
    }

    public int getAccelerationModifier() {
        return accelerationModifier;
    }
}
