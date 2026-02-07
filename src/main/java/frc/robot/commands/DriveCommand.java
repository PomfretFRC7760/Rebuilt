package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class DriveCommand extends Command {

    // Driver inputs
    private final DoubleSupplier speed;
    private final DoubleSupplier rotation;
    private final DoubleSupplier aimTrigger;
    private final BooleanSupplier passingModeToggle;

    // Subsystems
    private final CANDriveSubsystem driveSubsystem;
    private final VisionSubsystem visionSubsystem;

    // State
    private boolean passingMode = false;

    // Vision turn controller (tx -> omega)
    private final PIDController turnPID =
            new PIDController(0.13, 0.0, 0.001);

    public DriveCommand(
            DoubleSupplier speed,
            DoubleSupplier rotation,
            CANDriveSubsystem driveSubsystem,
            VisionSubsystem visionSubsystem,
            DoubleSupplier aimTrigger,
            BooleanSupplier passingModeToggle
    ) {
        this.speed = speed;
        this.rotation = rotation;
        this.driveSubsystem = driveSubsystem;
        this.visionSubsystem = visionSubsystem;
        this.aimTrigger = aimTrigger;
        this.passingModeToggle = passingModeToggle;

        // PID config
        turnPID.enableContinuousInput(-180.0, 180.0);
        turnPID.setTolerance(0.5); // degrees

        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {
        double triggerValue = aimTrigger.getAsDouble();

        // Toggle passing mode on button press
        if (passingModeToggle.getAsBoolean()) {
            passingMode = !passingMode;
        }

        SmartDashboard.putBoolean("Passing Mode", passingMode);
        SmartDashboard.putNumber("AprilTag ID", visionSubsystem.getAprilTagID());

        // =============================
        // AUTO AIM (TURN IN PLACE)
        // =============================
        if (triggerValue > 0.75 && visionSubsystem.hasTarget() && !passingMode) {
            double tx = visionSubsystem.getTx(); // degrees

            double omega = turnPID.calculate(tx, 0.0);

            // Deadband to stop jitter
            if (Math.abs(tx) < 0.5) {
                omega = 0.0;
            }

            // Clamp angular speed (rad/s)
            omega = MathUtil.clamp(omega, -2.5, 2.5);

            driveSubsystem.driveRobotRelative(
                new ChassisSpeeds(
                    0.0,   // no forward/back motion
                    0.0,   // ignored for diff drive
                    omega
                )
            );

            SmartDashboard.putBoolean("Auto Aim Active", true);
            SmartDashboard.putNumber("Auto Aim tx", tx);
            SmartDashboard.putBoolean("Auto Aim Aligned", turnPID.atSetpoint());
            return;
        }

        // =============================
        // NORMAL DRIVER CONTROL
        // =============================
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
