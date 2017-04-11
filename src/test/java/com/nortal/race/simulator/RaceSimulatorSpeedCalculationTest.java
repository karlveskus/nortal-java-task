package com.nortal.race.simulator;

import com.nortal.race.assignment.model.Thruster;
import com.nortal.race.assignment.model.ThrusterLevel;
import com.nortal.race.assignment.model.Vessel;
import com.nortal.race.assignment.model.VesselCommand;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Priit Liivak
 */
public class RaceSimulatorSpeedCalculationTest {

    private RaceSimulator simulator = new RaceSimulator();

    @Test
    public void testCalculateNewSpeedFormula() {
        int currentSpeed = 1;
        int acceleration = -1;
        int timeSpentSeconds = 1;

        double speed = simulator.calculateNewSpeed(currentSpeed, acceleration, timeSpentSeconds);
        Assert.assertEquals(0, speed, 0);

        timeSpentSeconds = 2;
        speed = simulator.calculateNewSpeed(currentSpeed, acceleration, timeSpentSeconds);
        Assert.assertEquals(-1, speed, 0);
    }

    @Test
    public void testCalculateNewSpeedWithDragWhileStopping() {
        int currentSpeed = 0;
        int timeSpentSeconds = 9999;

        double speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        Assert.assertEquals(0, speed, 0);
    }


    @Test
    public void testCalculateNewSpeedWithDrag() {
        int currentSpeed = 1;
        int timeSpentSeconds = 1;

        double speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        Assert.assertEquals(0, speed, 0);

        currentSpeed = 1;
        timeSpentSeconds = 9999;
        speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        Assert.assertEquals(0, speed, 0);

        currentSpeed = 10;
        timeSpentSeconds = 2;
        speed = simulator.calculateNewSpeedWithDrag(currentSpeed, timeSpentSeconds);
        Assert.assertEquals(5.8, speed, 0);
    }

    @Test
    public void testCalculateAchievedSpeedsWithNoThrust() {
        Vessel vessel = new Vessel();
        vessel.setSpeedX(10);
        vessel.setSpeedY(10);

        //With no thruster both axes affected by drag
        simulator.calculateAchievedSpeeds(vessel, new VesselCommand(Thruster.BACK, ThrusterLevel.T0), 2);
        Assert.assertEquals(5.8, vessel.getSpeedX(), 0);
        Assert.assertEquals(5.8, vessel.getSpeedY(), 0);

        //No negative impact on speed over long period of time - avoiding overflow
        vessel.setSpeedX(1);
        vessel.setSpeedY(1);
        simulator.calculateAchievedSpeeds(vessel, new VesselCommand(Thruster.BACK, ThrusterLevel.T0), 99999);
        Assert.assertEquals(0, vessel.getSpeedX(), 0);
        Assert.assertEquals(0, vessel.getSpeedY(), 0);

    }

    @Test
    public void testCalculateAchievedSpeedsThrustDirectionY() {
        Vessel vessel = new Vessel();
        vessel.setSpeedX(10);
        vessel.setSpeedY(10);
        simulator.calculateAchievedSpeeds(vessel, new VesselCommand(Thruster.BACK, ThrusterLevel.T3), 2);
        Assert.assertEquals(11.8, vessel.getSpeedY(), 0);
        Assert.assertEquals(5.8, vessel.getSpeedX(), 0);
    }


}
