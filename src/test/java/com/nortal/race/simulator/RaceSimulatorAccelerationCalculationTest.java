package com.nortal.race.simulator;

import com.nortal.race.assignment.model.Thruster;
import com.nortal.race.assignment.model.ThrusterLevel;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Priit Liivak
 */
public class RaceSimulatorAccelerationCalculationTest {

    private RaceSimulator simulator = new RaceSimulator(false);

    @Test
    public void testDragAccelerationDirection() {
        double dragAcceleration = simulator.getDragAcceleration(-1);
        Assert.assertTrue(dragAcceleration > 0);

        dragAcceleration = simulator.getDragAcceleration(1);
        Assert.assertTrue(dragAcceleration < 0);

        dragAcceleration = simulator.getDragAcceleration(0);
        Assert.assertTrue(dragAcceleration == 0);
    }

    @Test
    public void testCalculateAccelerationWith0SpeedAndLowThrust() {
        double acceleration = simulator.calculateAcceleration(0, -1, ThrusterLevel.T1);
        Assert.assertEquals(0, acceleration, 0);

        acceleration = simulator.calculateAcceleration(0, 1, ThrusterLevel.T1);
        Assert.assertEquals(0, acceleration, 0);
    }

    @Test
    public void testCalculateAccelerationWith0Speed() {
        double acceleration = simulator.calculateAcceleration(0, 1, ThrusterLevel.T2);
        double t2LaunchAcceleration = ThrusterLevel.T2.getAcceleration() - 1.6;
        Assert.assertEquals(t2LaunchAcceleration, acceleration, 0);

        acceleration = simulator.calculateAcceleration(0, -1, ThrusterLevel.T2);
        Assert.assertEquals(-t2LaunchAcceleration, acceleration, 0);
    }

    @Test
    public void testCalculateAccelerationX() {
        double acceleration = simulator.calculateAcceleration(1, Thruster.LEFT.getAccelerationModifier(), ThrusterLevel.T2);
        Assert.assertEquals(0.395, acceleration, 0);

        acceleration = simulator.calculateAcceleration(1, Thruster.RIGHT.getAccelerationModifier(), ThrusterLevel.T2);
        Assert.assertEquals(-3.605, acceleration, 0);

        acceleration = simulator.calculateAcceleration(10, Thruster.RIGHT.getAccelerationModifier(), ThrusterLevel.T3);
        Assert.assertEquals(-5.1, acceleration, 0);

        acceleration = simulator.calculateAcceleration(21.9, Thruster.LEFT.getAccelerationModifier(), ThrusterLevel.T4);
        //Round to 2 decimal places
        double roundedAcceleration = Math.round(acceleration * 100) / 100;
        Assert.assertEquals(0, roundedAcceleration, 0);

    }

    @Test
    public void testCalculateAccelerationY() {
        double acceleration = simulator.calculateAcceleration(1, Thruster.BACK.getAccelerationModifier(), ThrusterLevel.T2);
        Assert.assertEquals(0.395, acceleration, 0);

        acceleration = simulator.calculateAcceleration(1, Thruster.FRONT.getAccelerationModifier(), ThrusterLevel.T2);
        Assert.assertEquals(-3.605, acceleration, 0);

        acceleration = simulator.calculateAcceleration(10, Thruster.FRONT.getAccelerationModifier(), ThrusterLevel.T3);
        Assert.assertEquals(-5.1, acceleration, 0);

        acceleration = simulator.calculateAcceleration(21.9, Thruster.BACK.getAccelerationModifier(), ThrusterLevel.T4);
        //Round to 2 decimal places
        double roundedAcceleration = Math.round(acceleration * 100) / 100;
        Assert.assertEquals(0, roundedAcceleration, 0);
    }


}
