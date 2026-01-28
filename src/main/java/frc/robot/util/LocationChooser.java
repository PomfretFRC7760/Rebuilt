package frc.robot.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RobotContainer;
import java.util.List;

/** 2026 REBUILT: HUB positions A–L, fuel stations AB/KL, PROCESSOR=TOWER, BARGE=OUTPOST, LEFT/RIGHT=OUTPOST. */
public class LocationChooser {

    private final SendableChooser<HubPositions> letterChooser = new SendableChooser<>();
//    private final SendableChooser<Boolean> climbModeChooser = new SendableChooser<>();
    
    private final RobotContainer r;

    public LocationChooser(RobotContainer r) {
        this.r = r;

        // Letter selection (HUB positions A–L and fuel station pairs)
        for (HubPositions hubPos : HubPositions.values()) {
            letterChooser.addOption(hubPos.name(), hubPos);
        }

        letterChooser.setDefaultOption("NONE", HubPositions.NONE);

        // Climb mode
        // climbModeChooser.setDefaultOption("Off", false);
        // climbModeChooser.addOption("On", true);


        // Add choosers to SmartDashboard
        SmartDashboard.putData("Letter", letterChooser);
        //SmartDashboard.putData("Climb Mode", climbModeChooser);
    }

    public static enum HubPositions {
        A, B, C, D, E, F, G, H, I, J, K, L, AB, CD, EF, GH, IJ, KL, PROCESSOR, BARGE, LEFT, RIGHT, CLOSEST, NONE
    }

    /** Returns pose for selected 2026 target: HUB A–L, OUTPOST (LEFT/RIGHT/BARGE), TOWER (PROCESSOR), or fuel pairs. */
    public Pose2d selectFuelLocation() {
        HubPositions sel = letterChooser.getSelected();
        if (sel == null || sel == HubPositions.NONE) {
            return null;
        }
        switch (sel) {
            case LEFT:
                return Locations.getLeftGatherStationFar();
            case RIGHT:
                return Locations.getRightGatherStationFar();
            case CLOSEST:
                return selectClosestFuelLocation();
            case PROCESSOR:
                return Locations.getProcLoc();   // 2026: TOWER
            case BARGE:
                return Locations.getBargeLoc();  // 2026: OUTPOST
            case AB:
            case CD:
            case EF:
            case GH:
            case IJ:
            case KL:
                return Locations.getFuelLocation(sel);
            default:
                return Locations.getHubLocation(sel);
        }
    }

    public Rotation2d selectGatherAngle() {
        Pose2d loc = selectFuelLocation();
        return (loc != null) ? loc.getRotation().plus(Rotation2d.fromDegrees(180)) : Rotation2d.kZero;
    }

    public Rotation2d getAlignAngle() {
        HubPositions selectedHubPos = letterChooser.getSelected();
        Rotation2d scoringPosition;
        switch (selectedHubPos) {
            case A:
            case B:
                scoringPosition = Rotation2d.fromDegrees(0);
                break;
            case C:
            case D:
                scoringPosition = Rotation2d.fromDegrees(60);
                break;
            case E:
            case F:
                scoringPosition = Rotation2d.fromDegrees(120);
                break;
            case G:
            case H:
                scoringPosition = Rotation2d.fromDegrees(180);
                break;
            case I:
            case J:
                scoringPosition = Rotation2d.fromDegrees(240);
                break;
            case K:
            case L:
                scoringPosition = Rotation2d.fromDegrees(300);
                break;
            case PROCESSOR:
                scoringPosition = Rotation2d.fromDegrees(270);
                break;
            case LEFT:
                scoringPosition = Rotation2d.fromDegrees(306);
                break;
            case RIGHT:
                scoringPosition = Rotation2d.fromDegrees(54);
                break;
            case BARGE:
                scoringPosition = Rotation2d.fromDegrees(0);
                break;

            case AB:
                scoringPosition = Rotation2d.fromDegrees(180);
                break;
            case CD:    
                scoringPosition = Rotation2d.fromDegrees(240);
                break;
            case EF:
                scoringPosition = Rotation2d.fromDegrees(300);
                break;
            case GH:
                scoringPosition = Rotation2d.fromDegrees(0);
                break;
            case IJ:
                scoringPosition = Rotation2d.fromDegrees(60);
                break;
            case KL:
                scoringPosition = Rotation2d.fromDegrees(120);
                break;
            default:
                scoringPosition = Rotation2d.fromDegrees(0);
        }
        return Locations.isBlue() ? scoringPosition : scoringPosition.plus(Rotation2d.fromDegrees(180));
    }

    public Pose2d selectClosestFuelLocation() {
        return r.driveSubsystem.getPose().nearest(
            List.of(Locations.getLeftGatherStationFar(), Locations.getRightGatherStationFar())
        );
    }
}
