package com.nortal.race.simulator;

import com.nortal.race.assignment.model.RaceArea;
import com.nortal.race.assignment.model.RaceResult;
import com.nortal.race.assignment.model.Vessel;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

/**
 * Created by Priit Liivak
 */
public class RaceSimulatorRaceResultTest {

    private RaceSimulator simulator = new RaceSimulator();

    private Point startingPoint = new Point(1, 1);

    @Test
    public void testVesselCrashed() {
        Vessel vessel = new Vessel();
        vessel.setPosition(new Point(11, 9));
        RaceArea area = new RaceArea(new Point(10, 10));
        area.addTarget(1, 1);

        RaceResult raceResult = simulator.updateRaceResult(startingPoint, vessel, area);
        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.CRASHED, raceResult.getStatus());

        vessel.setPosition(new Point(10, 9));
        raceResult = simulator.updateRaceResult(startingPoint, vessel, area);
        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.CRASHED, raceResult.getStatus());

        vessel.setPosition(new Point(9, 10));
        raceResult = simulator.updateRaceResult(startingPoint, vessel, area);
        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.CRASHED, raceResult.getStatus());
    }

    @Test
    public void testRaceTimedOut() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(10, 10));
        area.addTarget(9, 2);
        simulator.getRaceResult().startRace();
        simulator.getRaceResult().addRaceTime(11 * 60 * 1000);
        RaceResult raceResult = simulator.updateRaceResult(startingPoint, vessel, area);
        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.TIMEOUT, raceResult.getStatus());
    }

    @Test
    public void testAllPointsCaptured() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(10, 10));
        area.addTarget(1, 2);
        area.addTarget(1, 6);

        //Moving from 1,1 to 1,2 - exactly on target
        vessel.setPosition(new Point(1, 2));
        RaceResult raceResult = simulator.updateRaceResult(new Point(1, 1), vessel, area);
        Assert.assertFalse(raceResult.isRaceOver());
        Assert.assertNotNull(raceResult.getCapturedTargets());
        Assert.assertEquals(1, raceResult.getCapturedTargets().size());

        //Moving from 1,2 to 1,8 - over the target within threshold
        vessel.setPosition(new Point(1, 8));
        raceResult = simulator.updateRaceResult(new Point(1, 2), vessel, area);
        Assert.assertEquals(2, raceResult.getCapturedTargets().size());
        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.COMPLETED, raceResult.getStatus());
    }


}
