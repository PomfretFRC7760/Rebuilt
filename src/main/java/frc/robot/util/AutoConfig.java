package frc.robot.util;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotContainer;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;

public class AutoConfig {
    public Pose2d startPose;

    public Pose2d fuel1Pose;
    public Pose2d fuel2Pose;
    public Pose2d fuel3Pose;
    public Pose2d fuel4Pose;

    public Pose2d station2Pose;
    public Pose2d station3Pose;
    public Pose2d station4Pose;

    public int lift1;
    public int lift2;
    public int lift3;
    public int lift4;

    public boolean enable1;
    public boolean enable2;
    public boolean enable3;
    public boolean enable4;

    public int pickup2;
    public int pickup3;
    public int pickup4;

    private final SendableChooser<HubPositions> letterChooser = new SendableChooser<>();
    private final SendableChooser<Station> stationChooser = new SendableChooser<>();
    private final SendableChooser<Integer> liftChooser = new SendableChooser<>();
    private final SendableChooser<Integer> pickupChooser = new SendableChooser<>();
    private final SendableChooser<Boolean> useCommand = new SendableChooser<>();
    private final SendableChooser<Start> startChooser = new SendableChooser<>();

    private final RobotContainer r;

    public AutoConfig(RobotContainer r) {
        this.r = r;

        // Letter selection
        for (HubPositions hubPos : HubPositions.values()) {
            letterChooser.addOption(hubPos.name(), hubPos);
        }

        letterChooser.setDefaultOption("A", HubPositions.A);

        for (Station station : Station.values()) {
            stationChooser.addOption(station.name(), station);
        }

        stationChooser.setDefaultOption("CLOSEST", Station.CLOSEST);

        liftChooser.setDefaultOption(FuelConstants.LABEL_FUEL_L1, 2);
        liftChooser.addOption(FuelConstants.LABEL_FUEL_L2, 3);
        liftChooser.addOption(FuelConstants.LABEL_FUEL_L3, 4);
        liftChooser.addOption("OUTPOST", 9);

        pickupChooser.setDefaultOption(FuelConstants.LABEL_FUEL, 6);
        pickupChooser.addOption(FuelConstants.LABEL_FUEL_L2, 7);
        pickupChooser.addOption(FuelConstants.LABEL_FUEL_L3, 8);

        useCommand.setDefaultOption("YES", true);
        useCommand.addOption("NO", false);

        for (Start start : Start.values()) {
            startChooser.addOption(start.name(), start);
        }

        startChooser.setDefaultOption("CENTER", Start.CENTER);


        // Add choosers to SmartDashboard
        SmartDashboard.putData("Auto Letter", letterChooser);
        SmartDashboard.putData("Station", stationChooser);
        SmartDashboard.putData("Pickup", pickupChooser);
        SmartDashboard.putData("Lift", liftChooser);
        SmartDashboard.putData("Use Command", useCommand);
        SmartDashboard.putData("Start", startChooser);

        SmartDashboard.putData("Save Fuel 1 (Station is ignored, preload fuel)", new InstantCommand(() -> saveSelection1()).ignoringDisable(true));
        SmartDashboard.putData("Save Fuel 2", new InstantCommand(() -> saveSelection2()).ignoringDisable(true));
        SmartDashboard.putData("Save Fuel 3", new InstantCommand(() -> saveSelection3()).ignoringDisable(true));
        SmartDashboard.putData("Save Fuel 4", new InstantCommand(() -> saveSelection4()).ignoringDisable(true));
        SmartDashboard.putData("Save Starting Pose", new InstantCommand(() -> saveStartingPose()).ignoringDisable(true));

    }

    public static enum HubPositions {
        A, B, C, D, E, F, G, H, I, J, K, L, BARGE
    }

    /** 2026 REBUILT: LEFT/RIGHT=OUTPOST (24), DEPOT (24), NEUTRAL_ZONE (360–408), AB–KL=HUB. */
    public static enum Station {
        LEFT, RIGHT, CLOSEST, DEPOT, NEUTRAL_ZONE, AB, CD, EF, GH, IJ, KL
    }

    public static enum Start {
        LEFT, CENTER, RIGHT
    }

    public Pose2d stationSelection() {
        switch (stationChooser.getSelected()) {
            case LEFT:
                return AutoLocation.getLeftGatherStationFar();
            case RIGHT:
                return AutoLocation.getRightGatherStationFar();
            case CLOSEST:
                return selectClosestFuelLocation();
            case DEPOT:
                return AutoLocation.getDepotLoc();
            case NEUTRAL_ZONE:
                return AutoLocation.getNeutralZoneCenter();
            case AB, CD, EF, GH, IJ, KL:
                return AutoLocation.getFuelLocation(stationChooser.getSelected());
            default:
                return selectClosestFuelLocation();
        }
    }

    public Pose2d scoreSelecton() {
        switch (letterChooser.getSelected()) {
            case BARGE:
                return AutoLocation.getBargeLoc();
            default:
                return AutoLocation.getHubLocation(letterChooser.getSelected());
        }
    }

    /** Optimal order by capacity: NEUTRAL_ZONE (360–408) > DEPOT (24) = OUTPOST (24). */
    public Pose2d selectClosestFuelLocation() {
        return r.driveSubsystem.getPose().nearest(
            List.of(
                AutoLocation.getNeutralZoneCenter(),
                AutoLocation.getDepotLoc(),
                AutoLocation.getLeftGatherStationFar(),
                AutoLocation.getRightGatherStationFar()
            )
        );
    }

    public void saveSelection1() {
        fuel1Pose = scoreSelecton();
        lift1 = liftChooser.getSelected();
        enable1 = useCommand.getSelected();
    }

    public void saveSelection2() {
        fuel2Pose = scoreSelecton();
        lift2 = liftChooser.getSelected();
        enable2 = useCommand.getSelected();
        station2Pose = stationSelection();
        pickup2 = pickupChooser.getSelected();
    }

    public void saveSelection3() {
        fuel3Pose = scoreSelecton();
        lift3 = liftChooser.getSelected();
        enable3 = useCommand.getSelected();
        station3Pose = stationSelection();
        pickup3 = pickupChooser.getSelected();
    }

    public void saveSelection4() {
        fuel4Pose = scoreSelecton();
        lift4 = liftChooser.getSelected();
        enable4 = useCommand.getSelected();
        station4Pose = stationSelection();
        pickup4 = pickupChooser.getSelected();
    }

    public void saveStartingPose() {
        startPose = scoreSelecton();
        r.driveSubsystem.resetPose(startPose);
    }
}
