package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.HubStatusSubsystem;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;

import java.util.function.BooleanSupplier;

/**
 * Default command for lift roller: intake (A) and jettison (B). Per 2026 Game Manual 6.5,
 * jettison (HUB scoring) only when HUB is active. Intake allowed anytime.
 */
public class LiftRollerCommand extends Command {
  private final LiftIntakeRollerSubsystem rollerSubsystem;
  private final LiftRotationSubsystem liftRotationSubsystem;
  private final BooleanSupplier shouldIntake;
  private final BooleanSupplier shouldJettison;
  private final HubStatusSubsystem hubStatus;

  public LiftRollerCommand(LiftIntakeRollerSubsystem subsystem, LiftRotationSubsystem liftRotationSubsystem, BooleanSupplier shouldIntake, BooleanSupplier shouldJettison, HubStatusSubsystem hubStatus) {
    rollerSubsystem = subsystem;
    this.shouldIntake = shouldIntake;
    this.shouldJettison = shouldJettison;
    this.liftRotationSubsystem = liftRotationSubsystem;
    this.hubStatus = hubStatus;
    addRequirements(rollerSubsystem, liftRotationSubsystem);
  }

  @Override
  public void execute() {
      if (shouldIntake.getAsBoolean()) {
        rollerSubsystem.runRollerIntake();
      } else if (shouldJettison.getAsBoolean() && hubStatus.isHubActive()) {
        rollerSubsystem.runRollerJettison();
      } else {
        if (DriverStation.isTeleop()) {
          rollerSubsystem.stallRoller();
        }
      }
      liftRotationSubsystem.updatePosition();
    }

  @Override
  public boolean isFinished() {
    return true;
  }
}