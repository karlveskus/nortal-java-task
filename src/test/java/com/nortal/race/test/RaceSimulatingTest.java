package com.nortal.race.test;

import com.esotericsoftware.kryo.Kryo;
import com.nortal.race.assignment.controller.RaceController;
import com.nortal.race.assignment.model.RaceArea;
import com.nortal.race.assignment.model.Vessel;
import com.nortal.race.assignment.model.VesselCommand;
import com.nortal.race.assignment.model.RaceResult;
import com.nortal.race.simulator.RaceSimulator;
import org.junit.Assert;

/**
 * Created by Priit Liivak
 */
public abstract class RaceSimulatingTest {

    protected RaceResult runSimulation(RaceArea area, Vessel vessel, VesselCommand vesselCommand) {
        RaceController controller = new RaceController();
        RaceSimulator simulator = new RaceSimulator(true);

        Kryo kryo = new Kryo();

        RaceResult raceResult = simulator.getRaceResult();
        while (!raceResult.isRaceOver()) {
            simulator.simulateVesselReaction(vessel, vesselCommand, area);

            Vessel vesselCopy = kryo.copy(vessel);
            RaceArea areaCopy = kryo.copy(area);
            //Pass copies to controller to avoid accidental modification of "real life"
            vesselCommand = controller.calculateNextCommand(vesselCopy, areaCopy);
        }

        System.out.println("Race is over in " + raceResult.getRaceTime()/1000+"s");
        System.out.println("Race result " + raceResult.getStatus());

        Assert.assertTrue(raceResult.isRaceOver());
        Assert.assertEquals(RaceResult.Status.COMPLETED, raceResult.getStatus());

        return raceResult;
    }
}
