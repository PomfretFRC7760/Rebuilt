package frc.robot.util;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.util.LocationChooser;

public class Locations {

    public static double robotWidth = 0.9398;
    public static double robotLength = 1.0414;
    public static Transform2d halfRobot = new Transform2d(robotLength / 2.0, 0, new Rotation2d());

    public static Transform2d halfRobotGatherLeftFar =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(1.5),
                    Units.inchesToMeters(16),
                    Rotation2d.kZero);
    public static Transform2d halfRobotGatherLeftClose =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(1.5),
                    Units.inchesToMeters(-16),
                    Rotation2d.kZero);
    public static Transform2d halfRobotGatherRightFar =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(1.5),
                    Units.inchesToMeters(-16),
                    Rotation2d.kZero);
    public static Transform2d halfRobotGatherRightClose =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(1.5),
                    Units.inchesToMeters(16),
                    Rotation2d.kZero);

    public static Transform2d halfRobotFuelRight =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(3.25),
                    Units.inchesToMeters(5.5),
                    Rotation2d.kZero);
    public static Transform2d halfRobotFuelLeft =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(3.5),
                    Units.inchesToMeters(-8),
                    Rotation2d.kZero);

    public static Transform2d halfRobotFuel =
            new Transform2d(robotLength / 2.0 + Units.inchesToMeters(6), 0, Rotation2d.kZero);

    public static Transform2d halfRobotFuelLevel1 =
            new Transform2d(
                    robotLength / 2.0 + Units.inchesToMeters(7.5),
                    Units.inchesToMeters(0),
                    new Rotation2d());

    public static AprilTagFieldLayout tags =
            AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);

    /** 2026 REBUILT AndyMark: field 16.518 x 8.043 m. ROBOT STARTING LINE 158.32 in from wall (per FE-2026 dwg). */
    private static final double FIELD_LENGTH_M = 16.518;
    private static final double FIELD_WIDTH_M = 8.043;
    private static final double START_LINE_OFFSET_M = Units.inchesToMeters(158.32); // AndyMark perimeter

    /** Blue start x = offset from blue wall; Red start x = field length - offset. Y: left 6.19, center 4.025, right 1.86 (m). */
    static Pose2d[] blueStarts = {
        new Pose2d(START_LINE_OFFSET_M, 6.19, Rotation2d.k180deg),   // driver 1 left
        new Pose2d(START_LINE_OFFSET_M, 4.025, Rotation2d.k180deg),  // driver 2 center
        new Pose2d(START_LINE_OFFSET_M, 1.86, Rotation2d.k180deg)    // driver 3 right
    };

    static Pose2d[] redStarts = {
        new Pose2d(FIELD_LENGTH_M - START_LINE_OFFSET_M, 1.86, Rotation2d.kZero),   // driver 1 left (mirrored y)
        new Pose2d(FIELD_LENGTH_M - START_LINE_OFFSET_M, 4.025, Rotation2d.kZero),  // driver 2 center
        new Pose2d(FIELD_LENGTH_M - START_LINE_OFFSET_M, 6.19, Rotation2d.kZero)    // driver 3 right
    };

    public static Pose2d getStartLoc(int idx) {
        if (isBlue()) {
            if (idx >= blueStarts.length) return null;
            return blueStarts[idx];
        } else {
            if (idx >= redStarts.length) return null;
            return redStarts[idx];
        }
    }

    /** 2026 REBUILT: Blue HUB tags 18,21; Red HUB tags 8,11 (was 7,10 - 7 is TRENCH in 2026). */
    public static Translation2d getHub() {
        Pose2d front, back;
        if (isBlue()) {
            front = tags.getTagPose(18).get().toPose2d();
            back = tags.getTagPose(21).get().toPose2d();
        } else {
            front = tags.getTagPose(8).get().toPose2d();
            back = tags.getTagPose(11).get().toPose2d();
        }

        return front.getTranslation().plus(back.getTranslation()).times(0.5);
    }

    /** 2026 REBUILT: HUB tags Blue 18,19,20,21,24,25,26,27 / Red 8,9,10,11,2,3,4,5. A-L map to these. */
    public static Pose2d getHubLocation(LocationChooser.HubPositions position) {
        switch (position) {
            case B:
                if (isBlue()) {
                    return invert(tags.getTagPose(18).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(8).get().toPose2d().plus(halfRobotFuelRight));
                }
            case A:
                if (isBlue()) {
                    return invert(tags.getTagPose(18).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(8).get().toPose2d().plus(halfRobotFuelLeft));
                }
            case D:
                if (isBlue()) {
                    return invert(tags.getTagPose(19).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(9).get().toPose2d().plus(halfRobotFuelRight));
                }
            case C:
                if (isBlue()) {
                    return invert(tags.getTagPose(19).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(9).get().toPose2d().plus(halfRobotFuelLeft));
                }
            case F:
                if (isBlue()) {
                    return invert(tags.getTagPose(20).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(10).get().toPose2d().plus(halfRobotFuelRight));
                }
            case E:
                if (isBlue()) {
                    return invert(tags.getTagPose(20).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(10).get().toPose2d().plus(halfRobotFuelLeft));
                }
            case H:
                if (isBlue()) {
                    return invert(tags.getTagPose(21).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(11).get().toPose2d().plus(halfRobotFuelRight));
                }
            case G:
                if (isBlue()) {
                    return invert(tags.getTagPose(21).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(11).get().toPose2d().plus(halfRobotFuelLeft));
                }
            case J:
                if (isBlue()) {
                    return invert(tags.getTagPose(24).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(2).get().toPose2d().plus(halfRobotFuelRight));
                }
            case I:
                if (isBlue()) {
                    return invert(tags.getTagPose(24).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(2).get().toPose2d().plus(halfRobotFuelLeft));
                }
            case L:
                if (isBlue()) {
                    return invert(tags.getTagPose(25).get().toPose2d().plus(halfRobotFuelRight));
                } else {
                    return invert(tags.getTagPose(3).get().toPose2d().plus(halfRobotFuelRight));
                }
            case K:
                if (isBlue()) {
                    return invert(tags.getTagPose(25).get().toPose2d().plus(halfRobotFuelLeft));
                } else {
                    return invert(tags.getTagPose(3).get().toPose2d().plus(halfRobotFuelLeft));
                }
            default:
                return null;
        }
    }

    /** 2026 REBUILT: same HUB tag mapping as getHubLocation. */
    public static Pose2d getFuelLocation(LocationChooser.HubPositions position) {
        switch (position) {
            case AB:
            default:
                if (isBlue()) {
                    return invert(tags.getTagPose(18).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(8).get().toPose2d().plus(halfRobotFuel));
                }
            case CD:
                if (isBlue()) {
                    return invert(tags.getTagPose(19).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(9).get().toPose2d().plus(halfRobotFuel));
                }
            case EF:
                if (isBlue()) {
                    return invert(tags.getTagPose(20).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(10).get().toPose2d().plus(halfRobotFuel));
                }
            case GH:
                if (isBlue()) {
                    return invert(tags.getTagPose(21).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(11).get().toPose2d().plus(halfRobotFuel));
                }
            case IJ:
                if (isBlue()) {
                    return invert(tags.getTagPose(24).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(2).get().toPose2d().plus(halfRobotFuel));
                }
            case KL:
                if (isBlue()) {
                    return invert(tags.getTagPose(25).get().toPose2d().plus(halfRobotFuel));
                } else {
                    return invert(tags.getTagPose(3).get().toPose2d().plus(halfRobotFuel));
                }
        }
    }

    /** 2026 REBUILT: gather = OUTPOST. Blue OUTPOST 29,30; Red OUTPOST 13,14. */
    public static Pose2d getLeftGatherStationFar() {
        if (!isBlue()) {
            return invert(tags.getTagPose(13).get().toPose2d().plus(halfRobotGatherLeftFar));
        } else {
            return invert(tags.getTagPose(29).get().toPose2d().plus(halfRobotGatherLeftFar));
        }
    }

    public static Pose2d getRightGatherStationFar() {
        if (!isBlue()) {
            return invert(tags.getTagPose(14).get().toPose2d().plus(halfRobotGatherRightFar));
        } else {
            return invert(tags.getTagPose(30).get().toPose2d().plus(halfRobotGatherRightFar));
        }
    }

    public static Pose2d getLeftGatherStationClose() {
        if (!isBlue()) {
            return invert(tags.getTagPose(13).get().toPose2d().plus(halfRobotGatherLeftClose));
        } else {
            return invert(tags.getTagPose(29).get().toPose2d().plus(halfRobotGatherLeftClose));
        }
    }

    public static Pose2d getRightGatherStationClose() {
        if (!isBlue()) {
            return invert(tags.getTagPose(14).get().toPose2d().plus(halfRobotGatherRightClose));
        } else {
            return invert(tags.getTagPose(30).get().toPose2d().plus(halfRobotGatherRightClose));
        }
    }

    /** 2026 REBUILT: tag 7 = Red TRENCH. Kept for compatibility; use for Red-side reference pose. */
    public static Pose2d getTag7() {
        Pose2d tag = tags.getTagPose(7).get().toPose2d().plus(halfRobotFuelLeft);
        return invert(tag);
    }

    public static boolean isBlue() {
        return DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Blue;
    }

    public static Pose2d invert(Pose2d in) {
        return new Pose2d(in.getTranslation(), in.getRotation().plus(Rotation2d.k180deg));
    }

    public static Pose2d invertAndOffset(Pose2d in) {
        if (isBlue()) {
            return new Pose2d(
                new Translation2d(in.getX() - 0.75, in.getY()), // Offset x by -0.75m
                in.getRotation().plus(Rotation2d.k180deg) // Invert rotation
            );
        } else {
            return new Pose2d(
                new Translation2d(in.getX() + 0.75, in.getY()), // Offset x by +0.75m
                in.getRotation().plus(Rotation2d.k180deg) // Invert rotation
            );
        }
    }
    

    /** 2026 REBUILT: Proc = TOWER. Blue TOWER 31,32; Red TOWER 15,16. */
    public static Pose2d getProcLoc() {
        if (isBlue()) {
            return (tags.getTagPose(31).get().toPose2d().plus(halfRobot));
        } else {
            return (tags.getTagPose(16).get().toPose2d().plus(halfRobot));
        }
    }

    /** 2026 REBUILT: Barge = OUTPOST. Blue 29, Red 13. */
    public static Pose2d getBargeLoc() {
        if (isBlue()) {
            return invertAndOffset(tags.getTagPose(29).get().toPose2d().plus(halfRobot));
        } else {
            return invertAndOffset(tags.getTagPose(13).get().toPose2d().plus(halfRobot));
        }
    }
}