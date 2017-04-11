package com.nortal.race.assignment.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Priit Liivak
 */
public class RaceResult {

    private List<Point> capturedTargets = new ArrayList<>();
    // Time in millis representing race length
    // In simulation mode this does not necessarily represent the time how long test was running
    private long raceTime;
    private Status status;

    public List<Point> getCapturedTargets() {
        return capturedTargets;
    }

    public void addCapturedTarget(Point capturedTarget) {
        if (!capturedTargets.contains(capturedTarget)) {
            this.capturedTargets.add(capturedTarget);
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isRaceOver() {
        return EnumSet.of(Status.CRASHED, Status.COMPLETED, Status.TIMEOUT).contains(status);
    }

    public long getRaceTime() {
        return raceTime;
    }

    public void addRaceTime(long timeInMillisToAdd) {
        this.raceTime += timeInMillisToAdd;
    }

    public void startRace() {
        this.raceTime = 0;
        this.status = Status.ONGOING;
    }

    @Override
    public String toString() {
        return "RaceResult{" +
                "capturedTargets=" + capturedTargets +
                ", raceTime=" + raceTime +
                ", status=" + status +
                '}';
    }

    public enum Status {
        ONGOING,
        CRASHED,
        COMPLETED,
        TIMEOUT
    }
}
