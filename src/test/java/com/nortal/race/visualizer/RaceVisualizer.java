package com.nortal.race.visualizer;

import com.nortal.race.assignment.model.*;
import com.nortal.race.simulator.RaceSimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class RaceVisualizer {

    public static final int VISUALIZATION_PAUSE_MILLIS = 100;

    private static final int PADDING = 12;
    private static final int VESSEL_RADIUS = 10;
    private static final int TARGET_RADIUS = 10;

    private JFrame frame;
    private JPanel pane;
    private VesselCommand vesselCommand;

    private Vessel vessel;
    private RaceArea raceArea;

    //true if GUI needs to be initialized. Set to false once the GUI has been created
    private boolean createGUI = false;

    public RaceVisualizer(boolean createGUI) {
        this.createGUI = createGUI;

        String disableGuiProperty = System.getProperty("simulator.disable.gui");
        Boolean disableGui = Boolean.valueOf(disableGuiProperty);
        if (Boolean.TRUE.equals(disableGui)) {
            //For automation
            System.out.println("GUI is disabled");
            this.createGUI = false;
        }

    }

    public void renderVisualizationGUI(final Vessel vessel, final VesselCommand vesselCommand, final RaceArea raceArea) {

        if (createGUI) {

            //Always update values with latest data
            this.raceArea = raceArea;
            this.vessel = vessel;
            this.vesselCommand = vesselCommand;

            try {
                //Slow down the calculations to be able to see what's happening
                Thread.sleep(VISUALIZATION_PAUSE_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (frame == null) {
                createGui();
            }
            frame.repaint();
        }


    }


    private void createGui() {
        frame = new JFrame(getClass().getSimpleName());

        pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); //ALWAYS call this method first!


                Graphics2D g2d = (Graphics2D) g;
                g2d.setBackground(Color.WHITE);

                if (raceArea != null) {
                    paintRaceArea(g2d);
                }

                if (vessel != null) {
                    paintVessel(g2d);
                }

                if (vesselCommand != null) {
                    paintVesselThuster(g2d);
                }


                g2d.dispose();
                g.dispose();
            }

            private void paintRaceArea(Graphics2D g2d) {
                double vizAcceleration = RaceSimulator.FRAME_SIZE_MILLIS / VISUALIZATION_PAUSE_MILLIS;
                g2d.drawString(vizAcceleration + " x speed", 11, 11);
                g2d.draw(new Rectangle2D.Double(PADDING, PADDING, raceArea.getWidth(), raceArea.getHeight()));

                g2d.setColor(Color.GREEN);
                for (Point target : raceArea.getTargets()) {
                    g2d.fill(new Ellipse2D.Double(getX(target), getY(target), TARGET_RADIUS, TARGET_RADIUS));
                }
                g2d.setColor(Color.BLACK);
            }

            private void paintVesselThuster(Graphics2D g2d) {
                Point thustStartPos = new Point(vessel.getPosition());
                thustStartPos.translate(5, -5);
                Thruster thruster = vesselCommand.getThruster();
                ThrusterLevel thrusterLevel = vesselCommand.getThrusterLevel();
                int thrustLength = (int) Math.round(thrusterLevel.getAcceleration() * 2);

                Point thrustEndPosA = new Point(thustStartPos);
                Point thrustEndPosB = new Point(thustStartPos);

                int thrusterEndPosDelta = -thruster.getAccelerationModifier() * (5 + thrustLength);

                if (Thruster.BACK.equals(thruster) || Thruster.FRONT.equals(thruster)) {
                    thrustEndPosA.translate(-2, thrusterEndPosDelta);
                    thrustEndPosB.translate(2, thrusterEndPosDelta);
                } else if (Thruster.LEFT.equals(thruster) || Thruster.RIGHT.equals(thruster)) {
                    thrustEndPosA.translate(thrusterEndPosDelta, -2);
                    thrustEndPosB.translate(thrusterEndPosDelta, 2);
                }

                if (!ThrusterLevel.T0.equals(thrusterLevel)) {
                    Stroke prevStroke = g2d.getStroke();
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(Color.RED);
                    g2d.draw(new Line2D.Double(getPoint(thustStartPos), getPoint(thrustEndPosA)));
                    g2d.draw(new Line2D.Double(getPoint(thrustEndPosA), getPoint(thrustEndPosB)));
                    g2d.draw(new Line2D.Double(getPoint(thustStartPos), getPoint(thrustEndPosB)));
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(prevStroke);
                }
            }

            private void paintVessel(Graphics2D g2d) {
                Point position = vessel.getPosition();
                g2d.draw(new Ellipse2D.Double(getX(position), getY(position), VESSEL_RADIUS, VESSEL_RADIUS));
            }

            private Point getPoint(Point position) {
                //X axis is shifted in simulator
                //Y axis is flipped in simulator
                return new Point(getX(position), getY(position));
            }

            private int getX(Point position) {
                //X axis is shifted in simulator
                return position.x + PADDING;
            }

            private int getY(Point position) {
                //Y axis is flipped in simulator
                return raceArea.getHeight() - position.y;
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500+2*PADDING, 500+2*PADDING);
            }
        };

        frame.add(pane);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public void setRaceArea(RaceArea raceArea) {
        this.raceArea = raceArea;
    }
}
