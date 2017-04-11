package com.nortal.race.test.provided;

import com.nortal.race.assignment.model.*;
import com.nortal.race.test.RaceSimulatingTest;
import org.junit.Test;

import java.awt.*;

/**
 * Created by Priit Liivak
 */
public class ManyTargetsTest extends RaceSimulatingTest {


    @Test
    public void manyTargets() {
        Vessel vessel = new Vessel();
        RaceArea area = new RaceArea(new Point(500, 500));
        area.addTarget(100, 200);
        area.addTarget(150, 120);
        area.addTarget(350, 300);
        area.addTarget(370, 240);
        area.addTarget(100, 370);
        area.addTarget(400, 100);

        //Initial state
        VesselCommand vesselCommand = new VesselCommand(Thruster.BACK, ThrusterLevel.T0);
        runSimulation(area, vessel, vesselCommand);
    }

}