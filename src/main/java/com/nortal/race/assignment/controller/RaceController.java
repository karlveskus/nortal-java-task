package com.nortal.race.assignment.controller;

import com.nortal.race.assignment.model.*;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

public class RaceController {

    private static final double WATER_DRAG_THRESHOLD = 1.6;

    private static final int TARGET_PROXIMITY_THRESHOLD = 2;

    public RaceController() {
    }

    private List<Point> targetsToCapture;
    private int targetId;
    private Point previousVesselPos = new Point();
    private ThrusterLevel thrusterLevel = ThrusterLevel.T4;

    // Distance between vessel and current target on Y-axis
    private int offsetY;

    // Distance draged on Y-axis after turning thruster off on current speed.
    private double dragY;

    public VesselCommand calculateNextCommand(Vessel vessel, RaceArea raceArea) {
        targetsToCapture = raceArea.getTargets();
        Point target = findNextTarget(vessel);

        Thruster thruster = calcThruster(vessel, target);
        VesselCommand vesselCommand = new VesselCommand(thruster, thrusterLevel);

        previousVesselPos = vessel.getPosition();
        return vesselCommand;
    }

    private Thruster calcThruster(Vessel vessel, Point target) {
        offsetY = Math.abs(target.y - vessel.getPosition().y);
        dragY = vessel.getSpeedY() * vessel.getSpeedY() / WATER_DRAG_THRESHOLD;

        if (targetOnTopLeftFromVessel(target, vessel.getPosition())) {
            return getThrusterWhenTargetOnTopLeft();
        }

        else if (targetOnTopRightFromVessel(target, vessel.getPosition())) {
            return getThrusterWhentargetOnTopRight();
        }

        else if (targetOnBottomRightFromVessel(target, vessel.getPosition())) {
            return getThrusterWhentargetOnBottomRight();
        }

        else {
            return getThrusterWhentargetOnBottomLeft();
        }
    }

    private boolean targetOnTopLeftFromVessel(Point target, Point vesselPos) {
        return target.x <= vesselPos.x && vesselPos.y < target.y;
    }

    private boolean targetOnTopRightFromVessel(Point target, Point vesselPos) {
        return vesselPos.x < target.x && vesselPos.y <= target.y;
    }

    private boolean targetOnBottomRightFromVessel(Point target, Point vesselPos) {
        return vesselPos.x <= target.x && target.y < vesselPos.y;
    }

    private Thruster getThruster(Thruster thrusterXAxis, Thruster thrusterYAxis) {
        if(ifOnLevelWithYAxis()) {
            return thrusterYAxis;
        } else {
            return thrusterXAxis;
        }
    }

    private Thruster getThrusterWhentargetOnBottomRight() {
        return getThruster(Thruster.LEFT, Thruster.FRONT);
    }

    private Thruster getThrusterWhentargetOnTopRight() {
        return getThruster(Thruster.LEFT, Thruster.BACK);
    }

    private Thruster getThrusterWhentargetOnBottomLeft() {
        return getThruster(Thruster.RIGHT, Thruster.FRONT);
    }

    private Thruster getThrusterWhenTargetOnTopLeft() {
        return getThruster(Thruster.RIGHT, Thruster.BACK);
    }

    private boolean ifOnLevelWithYAxis() {
        return offsetY > dragY + TARGET_PROXIMITY_THRESHOLD;
    }

    private boolean vesselCloseToTarget(Vessel vessel) {
        Point target = targetsToCapture.get(targetId);

        double passingDistance = Line2D.ptSegDist(
                previousVesselPos.x, previousVesselPos.y,
                vessel.getPosition().x, vessel.getPosition().y,
                target.x, target.y);

        return passingDistance < TARGET_PROXIMITY_THRESHOLD;
    }

    private Point findNextTarget(Vessel vessel) {
        if (vesselCloseToTarget(vessel)) {
            targetId++;
        }

        if (targetsToCapture.size() == targetId){
            return new Point();
        }
        return targetsToCapture.get(targetId);
    }
}

