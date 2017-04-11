package com.nortal.race.assignment.model;

import java.io.Serializable;

/**
 * Created by Priit Liivak
 */
public class VesselCommand implements Serializable {

    private Thruster thruster = Thruster.BACK;
    private ThrusterLevel thrusterLevel = ThrusterLevel.T0;

    public VesselCommand(Thruster thruster, ThrusterLevel thrusterLevel) {
        this.thruster = thruster;
        this.thrusterLevel = thrusterLevel;
    }

    public Thruster getThruster() {
        return thruster;
    }

    public void setThruster(Thruster thruster) {
        this.thruster = thruster;
    }

    public ThrusterLevel getThrusterLevel() {
        return thrusterLevel;
    }

    public void setThrusterLevel(ThrusterLevel thrusterLevel) {
        this.thrusterLevel = thrusterLevel;
    }

    @Override
    public String toString() {
        return "VesselCommand{" +
                thruster +
                ", " + thrusterLevel +
                '}';
    }
}
