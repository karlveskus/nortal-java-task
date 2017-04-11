package com.nortal.race.simulator;

import com.nortal.race.assignment.model.*;
import com.nortal.race.visualizer.RaceVisualizer;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Simulating the world for the race
 * <p>
 * Created by Priit Liivak
 */
public class RaceSimulator {

    /**
     * It takes force equal to 1.6m/s² to start moving the vessel in any direction.
     * Once the vessel is moving, drag amount depends on the speed of the vessel.
     */
    public static final double WATER_DRAG_THRESHOLD = 1.6;

    /**
     * Each simulation step is executed within 1 second frame
     */
    public static final int FRAME_SIZE_MILLIS = 500;

    /**
     * How close the center of vessel has to be to the center of target to be considered a successful collection
     */
    public static final int TARGET_PROXIMITY_THRESHOLD = 2;

    /**
     * Race cannot continue indefinitely. There is a timeout of 10 min
     */
    private static final long RACE_TIMEOUT_MILLIS = 10 * 60 * 1000;

    private RaceResult raceResult = new RaceResult();

    private RaceVisualizer raceVisualizer;

    public RaceSimulator() {
        this(true);
    }

    public RaceSimulator(boolean renderGUI) {
        raceVisualizer = new RaceVisualizer(renderGUI);
    }

    public void simulateVesselReaction(Vessel vessel, VesselCommand command, RaceArea raceArea) {
        //calculateReachedPosition modifies Vessel position. clone for calculations
        Point positionBeforeCommand = new Point(vessel.getPosition());

        double timeSpentSeconds = FRAME_SIZE_MILLIS / 1000.0;
        calculateReachedPosition(vessel, command, timeSpentSeconds);
        calculateAchievedSpeeds(vessel, command, timeSpentSeconds);

        updateRaceResult(positionBeforeCommand, vessel, raceArea);
        raceVisualizer.renderVisualizationGUI(vessel, command, raceArea);
    }

    RaceResult updateRaceResult(Point positionBeforeCommand, Vessel vessel, RaceArea raceArea) {
        if (raceResult.getStatus() == null) {
            raceResult.startRace();
        }

        raceResult.addRaceTime(FRAME_SIZE_MILLIS);

        if (raceResult.getRaceTime() > RACE_TIMEOUT_MILLIS) {
            raceResult.setStatus(RaceResult.Status.TIMEOUT);
            return raceResult;
        }

        if (!raceArea.isWithinBounds(vessel.getPosition())) {
            raceResult.setStatus(RaceResult.Status.CRASHED);
            return raceResult;
        }

        // Collect any targets on the distance travelled
        captureTargetsOnDistanceTravelled(positionBeforeCommand, vessel, raceArea);

        if (isAllTargetsCaptured(raceArea)) {
            raceResult.setStatus(RaceResult.Status.COMPLETED);
        }

        return raceResult;
    }

    private boolean isAllTargetsCaptured(RaceArea raceArea) {
        //Check if all targets collected
        boolean allTargetsCaptured = true;
        for (Point target : raceArea.getTargets()) {
            if (!raceResult.getCapturedTargets().contains(target)) {
                allTargetsCaptured = false;
                break;
            }
        }
        return allTargetsCaptured;
    }

    private void captureTargetsOnDistanceTravelled(Point positionBeforeCommand, Vessel vessel, RaceArea raceArea) {
        for (Point target : raceArea.getTargets()) {
            double passingDistance = Line2D.ptSegDist(
                    positionBeforeCommand.x, positionBeforeCommand.y,
                    vessel.getPosition().x, vessel.getPosition().y,
                    target.x, target.y);

            if (passingDistance < TARGET_PROXIMITY_THRESHOLD) {
                System.err.println("Target captured " + target);
                raceResult.addCapturedTarget(target);
            }
        }
    }

    public void calculateReachedPosition(Vessel vessel, VesselCommand command, double timeSpentSeconds) {

        int displacementX;
        int displacementY;

        Thruster thruster = command.getThruster();
        ThrusterLevel thrusterLevel = command.getThrusterLevel();

        double speedX = vessel.getSpeedX();
        double speedY = vessel.getSpeedY();

        if (thrusterLevel.equals(ThrusterLevel.T0)) {
            //If thrusters are off then both directions affected by drag only
            displacementX = calculateDisplacementWithDrag(speedX, timeSpentSeconds);
            displacementY = calculateDisplacementWithDrag(speedY, timeSpentSeconds);

        } else if (Thruster.BACK.equals(thruster) || Thruster.FRONT.equals(thruster)) {
            //If Y directional thruster is applying force then X affected by drag, Y by thruster power
            displacementX = calculateDisplacementWithDrag(speedX, timeSpentSeconds);
            displacementY = calculateDisplacement(speedY, command, timeSpentSeconds);

        } else if (Thruster.LEFT.equals(thruster) || Thruster.RIGHT.equals(thruster)) {
            //If X directional thruster is applying force then Y affected by drag, X by thruster power
            displacementX = calculateDisplacement(speedX, command, timeSpentSeconds);
            displacementY = calculateDisplacementWithDrag(speedY, timeSpentSeconds);

        } else {
            throw new IllegalArgumentException("Vessel command " + command + " invalid");
        }

        Point position = vessel.getPosition();
        position.translate(displacementX, displacementY);

    }

    private int calculateDisplacement(double currentSpeed, VesselCommand command, double timeSpentSeconds) {
        double vesselAcceleration = calculateAcceleration(currentSpeed, command.getThruster().getAccelerationModifier(), command.getThrusterLevel());
        return calculateDisplacement(currentSpeed, vesselAcceleration, timeSpentSeconds);
    }

    private int calculateDisplacement(double currentSpeed, double vesselAcceleration, double timeSpentSeconds) {
        double exactDisplacement = currentSpeed * timeSpentSeconds + 1 / 2 * vesselAcceleration * Math.pow(timeSpentSeconds, 2);
        return (int) Math.round(exactDisplacement);
    }

    /*
    * Drag is a special kind of acceleration that has always an opposite direction to current movement
    * Depending on current movement direction we either increase or decrease the position.
    * We also compensate for overshoot since drag eventually pulls the vessel to a stop
    */
    private int calculateDisplacementWithDrag(double currentSpeed, double timeSpentSeconds) {

        double dragAcceleration = getDragAcceleration(currentSpeed);
        int displacement = calculateDisplacement(currentSpeed, dragAcceleration, timeSpentSeconds);

        //compensate overshooting. Drag does not make the vessel to change direction
        if (currentSpeed == 0 || (currentSpeed < 0 && displacement > 0) || (currentSpeed > 0 && displacement < 0)) {
            displacement = 0;
        }

        return displacement;
    }

    /**
     * Calculates the speed for the vessel with given thruster settings that the vessel achieves in given time (milliseconds)
     */
    void calculateAchievedSpeeds(Vessel vessel, VesselCommand command, double timeSpentSeconds) {

        double newSpeedX;
        double newSpeedY;

        double speedX = vessel.getSpeedX();
        double speedY = vessel.getSpeedY();

        if (command.getThrusterLevel().equals(ThrusterLevel.T0)) {
            //If thrusters are off then both directions affected by drag only
            newSpeedX = calculateNewSpeedWithDrag(speedX, timeSpentSeconds);
            newSpeedY = calculateNewSpeedWithDrag(speedY, timeSpentSeconds);

        } else if (Thruster.BACK.equals(command.getThruster()) || Thruster.FRONT.equals(command.getThruster())) {
            //If Y directional thruster is applying force then X affected by drag, Y by thruster power
            newSpeedX = calculateNewSpeedWithDrag(speedX, timeSpentSeconds);
            newSpeedY = calculateNewSpeed(speedY, command, timeSpentSeconds);

        } else if (Thruster.LEFT.equals(command.getThruster()) || Thruster.RIGHT.equals(command.getThruster())) {
            //If X directional thruster is applying force then Y affected by drag, X by thruster power
            newSpeedX = calculateNewSpeed(speedX, command, timeSpentSeconds);
            newSpeedY = calculateNewSpeedWithDrag(speedY, timeSpentSeconds);

        } else {
            throw new IllegalArgumentException("Vessel command " + command + " invalid");
        }

        //speed = speed + acceleration * timeSpentSeconds;
        vessel.setSpeedX(newSpeedX);
        vessel.setSpeedY(newSpeedY);
    }

    /*
    * Drag is a special kind of acceleration that has always an opposite direction to current movement
    * Depending on current movement direction we either increase or decrease the speed.
    * We also compensate for overshoot since the ultimate target speed for drag equals 0
    */
    double calculateNewSpeedWithDrag(double currentSpeed, double timeSpentSeconds) {

        double dragAcceleration = getDragAcceleration(currentSpeed);
        double newSpeed = calculateNewSpeed(currentSpeed, dragAcceleration, timeSpentSeconds);

        //compensate overshooting. Drag does not make the vessel to change direction
        if (currentSpeed == 0 || (currentSpeed < 0 && newSpeed > 0) || (currentSpeed > 0 && newSpeed < 0)) {
            newSpeed = 0;
        }

        return newSpeed;
    }


    private double calculateNewSpeed(double currentSpeed, VesselCommand command, double timeSpentSeconds) {
        double vesselAcceleration = calculateAcceleration(currentSpeed, command.getThruster().getAccelerationModifier(), command.getThrusterLevel());
        return calculateNewSpeed(currentSpeed, vesselAcceleration, timeSpentSeconds);
    }

    double calculateNewSpeed(double currentSpeed, double vesselAcceleration, double timeSpentSeconds) {
        return currentSpeed + vesselAcceleration * timeSpentSeconds;
    }


    /**
     * Calculated directional acceleration of thruster and level of thrust.
     * For example if BACK thruster is enabled with T2 then thruster acceleration is positive
     * if FORWARD thruster is enabled with T2 then thruster acceleration is negative
     *
     * @param thrusterAccelerationModifier indicates the direction of thrust applied at given level.
     *                                     The speed and thrust are always on the same axis
     */
    double calculateAcceleration(double currentSpeed, int thrusterAccelerationModifier, ThrusterLevel thrusterLevel) {
        if (thrusterAccelerationModifier != -1 && thrusterAccelerationModifier != 1) {
            throw new IllegalArgumentException("Thruster acceleration modifier must be either -1 or 1");
        }

        double thrustAcceleration = thrusterAccelerationModifier * thrusterLevel.getAcceleration();
        if (currentSpeed == 0) {
            //If current speed for given direction is 0 then soon to apply drag is opposite to thrust acceleration
            //If thruster acceleration does not exceed threshold the vessel does not move
            if (thrusterLevel.getAcceleration() <= WATER_DRAG_THRESHOLD) {
                return 0;
            }

            double dragAcceleration = -thrusterAccelerationModifier * WATER_DRAG_THRESHOLD;
            return thrustAcceleration + dragAcceleration;
        } else {
            //Otherwise drag is applied depending on the current movement direction
            double dragAcceleration = getDragAcceleration(currentSpeed);
            return thrustAcceleration + dragAcceleration;
        }

    }

    /**
     * Calculates directional acceleration of water depending on current speed indicating movement.
     * Drag affects vessel always in the opposite direction to its movement
     * <p>
     * Formula: x = 1.6 + v²/200
     * This gives us vmax = √480 = approximately 21.908902
     */
    double getDragAcceleration(double currentSpeed) {
        // 0 if currentSpeed = 0
        double dragDirectionalModifier = -Math.signum(currentSpeed);
        //
        // //The maximum speed of the vessel is x²
        double waterDrag = WATER_DRAG_THRESHOLD + (Math.pow(currentSpeed, 2) / 200);
        return dragDirectionalModifier * waterDrag;
    }

    public RaceResult getRaceResult() {
        return raceResult;
    }
}
