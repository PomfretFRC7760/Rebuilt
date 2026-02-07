package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class DriveCommand extends Command {

    // Driver inputs
    private final DoubleSupplier speed;
    private final DoubleSupplier rotation;
    private final DoubleSupplier aimTrigger;
    private final BooleanSupplier passingModeToggle;

    // Subsystems
    private final DriveSubsystem driveSubsystem;
    private final VisionSubsystem visionSubsystem;

    // State
    private boolean passingMode = false;

    // Vision PID (tx -> omega)
    private final PIDController turnPID = new PIDController(0.13, 0.0, 0.001);

    // Heading lock
    private Double lockedHeadingDeg = null;

    // Deadband and clamp
    private static final double TX_DEADBAND = 0.5;
    private static final double MAX_OMEGA = 2.5; // rad/s
    private static final double maxSpeed = 6.05;
    private static final double deadband = 0.1;

    private double xstick;

    public DriveCommand(DoubleSupplier speed, DoubleSupplier rotation, DriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem, DoubleSupplier aimTrigger, BooleanSupplier passingModeToggle) {
        this.speed = speed;
        this.rotation = rotation;
        this.driveSubsystem = driveSubsystem;
        this.visionSubsystem = visionSubsystem;
        this.aimTrigger = aimTrigger;
        this.passingModeToggle = passingModeToggle;

        turnPID.enableContinuousInput(-180.0, 180.0);
        turnPID.setTolerance(TX_DEADBAND);

        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        double triggerValue = aimTrigger.getAsDouble();

        // Toggle passing mode (ideally edge-detect)
        if (passingModeToggle.getAsBoolean()) {
            passingMode = !passingMode;
        }

        SmartDashboard.putBoolean("Passing Mode", passingMode);
        SmartDashboard.putNumber("AprilTag ID", visionSubsystem.getAprilTagID());

        // =============================
        // AUTO AIM + HEADING LOCK
        // =============================
        if (triggerValue > 0.75 && visionSubsystem.hasTarget() && !passingMode) {

            double omega;

            // Phase 1: Rotate using vision if not aligned
            if (!turnPID.atSetpoint()) {
                double tx = visionSubsystem.getTx(); // degrees

                omega = turnPID.calculate(tx, 0.0);

                // Deadband
                if (Math.abs(tx) < TX_DEADBAND) {
                    omega = 0.0;
                }

                // Capture heading once aligned
                if (turnPID.atSetpoint()) {
                    lockedHeadingDeg = driveSubsystem.getPose().getRotation().getDegrees();
                }

            } else {
                // Phase 2: Heading lock using gyro
                if (lockedHeadingDeg == null) {
                    lockedHeadingDeg = driveSubsystem.getPose().getRotation().getDegrees();
                }

                double headingError = lockedHeadingDeg - driveSubsystem.getPose().getRotation().getDegrees();
                omega = turnPID.calculate(headingError, 0.0);
            }

            // Clamp
            omega = MathUtil.clamp(omega, -MAX_OMEGA, MAX_OMEGA);
            double xstickraw = speed.getAsDouble();
            if (Math.abs(xstickraw) >= deadband) {
                // Remove the deadband and rescale to full range
                if (xstickraw > 0) {
                    xstick = (xstickraw - deadband) / (1.0 - deadband);
                } else {
                    xstick = (xstickraw + deadband) / (1.0 - deadband);
                }
            }
            else if (Math.abs(xstickraw) < 0.1) {
                xstick = 0;
            }
            double forward = xstick * maxSpeed;

            driveSubsystem.driveRobotRelative(
                new ChassisSpeeds(
                    forward, // forward/back
                    0.0,                 // strafing ignored for diff drive
                    omega
                )
            );

            SmartDashboard.putBoolean("Auto Aim Active", true);
            SmartDashboard.putNumber("Auto Aim Omega", omega);
            SmartDashboard.putBoolean("Auto Aim Aligned", turnPID.atSetpoint());
            return;
        }

        // =============================
        // NORMAL DRIVER CONTROL
        // =============================
        lockedHeadingDeg = null;
        turnPID.reset();

        SmartDashboard.putBoolean("Auto Aim Active", false);

        driveSubsystem.driveRobot(
            speed.getAsDouble(),
            rotation.getAsDouble()
        );
    }

    public boolean isPassingMode() {
        return passingMode;
    }
}
