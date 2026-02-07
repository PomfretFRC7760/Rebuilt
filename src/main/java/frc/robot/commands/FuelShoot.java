package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeAndShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class FuelShoot extends Command {
    private final IntakeAndShooterSubsystem shooterSubsystem;
    private final VisionSubsystem visionSubsystem;
    private final BooleanSupplier aimButton;
    private final BooleanSupplier passingModeState;
    private final BooleanSupplier shootButton;

    public FuelShoot(IntakeAndShooterSubsystem shooterSubsystem, VisionSubsystem visionSubsystem,
                        BooleanSupplier aimButton, BooleanSupplier passingModeState, BooleanSupplier shootButton) {
        this.shooterSubsystem = shooterSubsystem;
        this.visionSubsystem = visionSubsystem;
        this.aimButton = aimButton;
        this.passingModeState = passingModeState;
        this.shootButton = shootButton;
        addRequirements(shooterSubsystem, visionSubsystem);
    }
    @Override
    public void initialize() {
        // Passing mode: fixed shoot speed
        if (passingModeState.getAsBoolean()) {
            if (aimButton.getAsBoolean()) {
                shooterSubsystem.spoolUpWheels(6000);
            }
        } else if (!passingModeState.getAsBoolean()) {
            if (aimButton.getAsBoolean()) {
                autoSpoolUp();
            }
        }

        if (shootButton.getAsBoolean()) {
            shooterSubsystem.shoot();
        }
    }
    
    public void autoSpoolUp() {
    // 1️⃣ Get optimal shot velocity from vision
    double optimalVelocityMetersPerSec = visionSubsystem.getOptimalShotVelocity();
    if (optimalVelocityMetersPerSec < 0) {
        // Target unreachable
        return;
    }

    // 2️⃣ Convert to wheel rotations per second
    double wheelDiameterMeters = 0.1016;
    double wheelCircumferenceMeters = Math.PI * wheelDiameterMeters;
    double wheelRotationsPerSec = optimalVelocityMetersPerSec / wheelCircumferenceMeters;

    // 3️⃣ Motor spins 2x faster than wheel
    double motorRotationsPerSec = wheelRotationsPerSec * 2.0;

    // 4️⃣ Convert to RPM and send to motor
    double motorRPM = motorRotationsPerSec * 60.0;
    shooterSubsystem.spoolUpWheels(motorRPM); // implement this in your shooter subsystem
    }
}
