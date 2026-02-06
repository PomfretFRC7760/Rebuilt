package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HubStatusSubsystem;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

/**
 * Jettisons FUEL into the HUB (scoring). Per 2026 Game Manual 6.5: only score when HUB is
 * active—FUEL in inactive HUB earns no points. Gated by HubStatusSubsystem.
 */
public class FuelShoot extends Command {
    private final LiftIntakeRollerSubsystem intake;
    private final HubStatusSubsystem hubStatus;

    public FuelShoot(LiftIntakeRollerSubsystem intake, HubStatusSubsystem hubStatus) {
        this.intake = intake;
        this.hubStatus = hubStatus;
        addRequirements(intake);
    }

    @Override
    public void initialize() {
        if (hubStatus.isHubActive()) {
            intake.autoRunRollerJettison();
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
