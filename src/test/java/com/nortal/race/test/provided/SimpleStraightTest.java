package com.nortal.race.test.provided;

import com.nortal.race.assignment.model.*;
import com.nortal.race.test.RaceSimulatingTest;
import org.junit.Test;

import java.awt.*;

/**
 * Created by Priit Liivak
 */
public class SimpleStraightTest extends RaceSimulatingTest {


    @Test
    public void testStraightLineSingleTarget() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(500, 500));
        area.addTarget(200, 200);

        //Initial state
        VesselCommand vesselCommand = new VesselCommand(Thruster.BACK, ThrusterLevel.T0);
        runSimulation(area, vessel, vesselCommand);
    }

    @Test
    public void twoTargetsYAxis() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(500, 500));
        area.addTarget(100, 200);
        area.addTarget(100, 20);

        //Initial state
        VesselCommand vesselCommand = new VesselCommand(Thruster.BACK, ThrusterLevel.T0);
        runSimulation(area, vessel, vesselCommand);
    }

    @Test
    public void twoTargetsXAxis() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(500, 500));
        area.addTarget(100, 200);
        area.addTarget(150, 200);

        //Initial state
        VesselCommand vesselCommand = new VesselCommand(Thruster.BACK, ThrusterLevel.T0);
        runSimulation(area, vessel, vesselCommand);
    }

    @Test
    public void targetsDiagonal() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(500, 500));
        area.addTarget(100, 100);
        area.addTarget(150, 150);
        area.addTarget(250, 250);

        //Initial state
        VesselCommand vesselCommand = new VesselCommand(Thruster.BACK, ThrusterLevel.T0);
        runSimulation(area, vessel, vesselCommand);
    }


}
