package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeAndShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class FuelShoot extends Command {
    private final IntakeAndShooterSubsystem shooterSubsystem;
    private final VisionSubsystem visionSubsystem;
    private final DoubleSupplier aimTrigger;
    private final BooleanSupplier passingModeState;
    private final DoubleSupplier shootTrigger;

    public FuelShoot(IntakeAndShooterSubsystem shooterSubsystem, VisionSubsystem visionSubsystem,
                        DoubleSupplier aimTrigger, BooleanSupplier passingModeState, DoubleSupplier shootTrigger) {
        this.shooterSubsystem = shooterSubsystem;
        this.visionSubsystem = visionSubsystem;
        this.aimTrigger = aimTrigger;
        this.passingModeState = passingModeState;
        this.shootTrigger = shootTrigger;
        addRequirements(shooterSubsystem, visionSubsystem);
    }
    @Override
    public void execute() {
        // Passing mode: fixed shoot speed
        if (passingModeState.getAsBoolean()) {
            if (aimTrigger.getAsDouble() > 0.75) {
                shooterSubsystem.spoolUpWheels(6000);
            }
        } else if (!passingModeState.getAsBoolean()) {
            if (aimTrigger.getAsDouble() > 0.75) {
                autoSpoolUp();
            }
        }

        if (shootTrigger.getAsDouble() > 0.75) {
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
