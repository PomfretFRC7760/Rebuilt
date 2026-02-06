package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {

    // Limelight name — empty string means "default" Limelight
    private static final String limelight = "";

    public VisionSubsystem() {
    }

    /** Horizontal offset from crosshair (degrees) */
    public double getTx() {
        return LimelightHelpers.getTX(limelight);
    }

    /** Vertical offset from crosshair (degrees) */
    public double getTy() {
        return LimelightHelpers.getTY(limelight);
    }

    /** AprilTag ID currently seen (-1 if none) */
    public int getAprilTagID() {
        return (int) LimelightHelpers.getFiducialID(limelight);
    }

    /** True if Limelight currently sees a valid target */
    public boolean hasTarget() {
        return LimelightHelpers.getTV(limelight);
    }
}
