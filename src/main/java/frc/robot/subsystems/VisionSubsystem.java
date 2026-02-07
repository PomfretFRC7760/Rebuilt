package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import frc.robot.util.LimelightHelpers;

import java.io.IOException;
import java.util.Optional;

public class VisionSubsystem extends SubsystemBase {

    private static final String LIMELIGHT_NAME = "";

    // ================================
    // LIMELIGHT MOUNTING CONSTANTS
    // ================================
    private static final double LIMELIGHT_HEIGHT_METERS = 0.65;
    private static final double LIMELIGHT_PITCH_DEGREES = 25.0;

    // ================================
    // SHOOTER MOUNTING CONSTANTS
    // ================================
    private static final double SHOOTER_X_METERS = 0.1; // forward from robot center
    private static final double SHOOTER_Y_METERS = 0.0; // sideways from robot center
    private static final double SHOOTER_Z_METERS = 0.85; // height of ball exit

    private static final double GRAVITY = 9.81; // m/s²

    private AprilTagFieldLayout fieldLayout;

    public VisionSubsystem() {
       fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);
    }

    // ================================
    // LIMELIGHT RAW VALUES
    // ================================
    public double getTx() { return LimelightHelpers.getTX(LIMELIGHT_NAME); }
    public double getTy() { return LimelightHelpers.getTY(LIMELIGHT_NAME); }
    public boolean hasTarget() { return LimelightHelpers.getTV(LIMELIGHT_NAME); }

    // ================================
    // APRILTAG HANDLING
    // ================================
    public int getAprilTagID() {
        int id = (int) LimelightHelpers.getFiducialID(LIMELIGHT_NAME);
        switch (id) {
            case 9: case 10: case 8: case 5:
            case 11: case 2: case 18: case 27:
            case 26: case 25: case 21: case 24:
                return id;
            default:
                return -1;
        }
    }

    private double getAprilTagHeightMeters(int tagID) {
        if (fieldLayout == null) return -1;
        Optional<Pose3d> tagPose = fieldLayout.getTagPose(tagID);
        return tagPose.map(Pose3d::getZ).orElse(-1.0);
    }

    // ================================
    // DISTANCE CALCULATION
    // ================================
    /**
     * Calculates horizontal distance from the shooter to the target (m),
     * accounting for Limelight offset and robot translation.
     */
    public double getHorizontalDistanceMeters() {
        if (!hasTarget()) return -1;

        int tagID = getAprilTagID();
        if (tagID == -1) return -1;

        double tagHeight = getAprilTagHeightMeters(tagID);
        if (tagHeight <= 0) return -1;

        // Compute distance using Limelight vertical angle
        double limelightTyRad = Units.degreesToRadians(LIMELIGHT_PITCH_DEGREES + getTy());
        double limelightToTargetMeters = (tagHeight - LIMELIGHT_HEIGHT_METERS) / Math.tan(limelightTyRad);

        // Project to horizontal distance from **shooter** position
        double shooterOffset = SHOOTER_X_METERS; // if Limelight is in front/back, include X
        return Math.max(0.0, limelightToTargetMeters - shooterOffset);
    }

    // ================================
    // 2D KINEMATICS FUNCTIONS
    // ================================
    /**
     * Returns optimal shot angle (deg) for given distance and target height.
     */
    public double getOptimalShotAngleDegrees() {
        int tagID = getAprilTagID();
        if (tagID == -1) return -1;

        double targetHeight = getAprilTagHeightMeters(tagID);
        double x = getHorizontalDistanceMeters();
        double y = targetHeight - SHOOTER_Z_METERS;

        if (x <= 0) return -1;

        // Use physics formula: y = x * tan(theta) - g*x^2 / (2*v^2*cos^2(theta))
        // Solve quadratic for tan(theta) assuming velocity unknown → pick 45 deg for max range
        double angleRad = Math.atan2(y, x);
        return Units.radiansToDegrees(angleRad);
    }

    /**
     * Returns required initial velocity (m/s) to hit target at optimal angle.
     */
    public double getOptimalShotVelocity() {
        int tagID = getAprilTagID();
        if (tagID == -1) return -1;

        double targetHeight = getAprilTagHeightMeters(tagID);
        double x = getHorizontalDistanceMeters();
        double y = targetHeight - SHOOTER_Z_METERS;

        double angleRad = Math.toRadians(getOptimalShotAngleDegrees());
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);

        if (x <= 0 || cosTheta == 0) return -1;

        // Solve projectile motion for initial velocity
        double v = Math.sqrt(GRAVITY * x * x / (2 * cosTheta * cosTheta * (x * Math.tan(angleRad) - y)));
        return v;
    }
}
