package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

/**
 * Intakes FUEL into the lift mechanism. Per 2026 Game Manual, intake is allowed anytime
 * from DEPOT, OUTPOST, or NEUTRAL ZONE. No HUB status restriction on collection.
 */
public class FuelIntake extends Command {
    private final LiftIntakeRollerSubsystem intake;

    public FuelIntake(LiftIntakeRollerSubsystem intake) {
        this.intake = intake;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        intake.autoRunRollerIntake();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
