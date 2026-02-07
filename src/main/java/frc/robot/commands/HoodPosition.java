package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.VisionSubsystem;

public class HoodPosition extends Command {
    private final HoodSubsystem hoodSubsystem;
    private final VisionSubsystem visionSubsystem;
    private final BooleanSupplier aimButton;
    private final BooleanSupplier passingModeState;

    // Hood motor constants
    private static final double HOOD_REDUCTION_RATIO = 20.0; // motor rotations per hood degree

    public HoodPosition(HoodSubsystem hoodSubsystem, VisionSubsystem visionSubsystem,
                        BooleanSupplier aimButton, BooleanSupplier passingModeState) {
        this.hoodSubsystem = hoodSubsystem;
        this.visionSubsystem = visionSubsystem;
        this.aimButton = aimButton;
        this.passingModeState = passingModeState;

        addRequirements(hoodSubsystem, visionSubsystem);
    }

    @Override
    public void initialize() {
        // Passing mode: fixed hood angle
        if (passingModeState.getAsBoolean()) {
            hoodSubsystem.setPosition(30); // placeholder hood angle for passing balls
            return;
        }

        // Auto-aim mode: use vision to calculate hood angle
        if (aimButton.getAsBoolean()) {
            autoPositionHood();
        }
    }
    
    public void autoPositionHood() {
        double optimalHoodAngleDeg = visionSubsystem.getOptimalShotAngleDegrees();
            if (optimalHoodAngleDeg < 0) {
                // invalid shot (target not visible or unreachable)
                return;
            }

            // Convert to motor rotations if necessary
            double motorRotations = optimalHoodAngleDeg * HOOD_REDUCTION_RATIO;

            // Move hood
            hoodSubsystem.setPosition(motorRotations);
    }
}
