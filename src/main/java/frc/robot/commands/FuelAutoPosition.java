package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftRotationSubsystem;

/**
 * Positions the lift rotation (wrist) for FUEL intake or scoring. Used with LiftAndScore.
 * Per 2026 Game Manual 6.5.1, FUEL scores when it passes through HUB top opening and sensor array.
 */
public class FuelAutoPosition extends Command {
    private final LiftRotationSubsystem intake;
    private final double targetPosition;

    public FuelAutoPosition(LiftRotationSubsystem intake, double targetPosition) {
        this.intake = intake;
        this.targetPosition = targetPosition;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        intake.autoPosition(targetPosition);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
