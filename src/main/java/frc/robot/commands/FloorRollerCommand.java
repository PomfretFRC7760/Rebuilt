package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FloorIntakeRollerSubsystem;

import java.util.function.BooleanSupplier;

/**
 * Floor intake roller: intake (A) and jettison (B). Floor jettison ejects FUEL—does not
 * score into HUB—so no HUB status gate. Intake allowed anytime per 2026 Game Manual.
 */
public class FloorRollerCommand extends Command {
  private final FloorIntakeRollerSubsystem rollerSubsystem;
  private final BooleanSupplier shouldIntake;
  private final BooleanSupplier shouldJettison;

  public FloorRollerCommand(FloorIntakeRollerSubsystem subsystem, BooleanSupplier shouldIntake, BooleanSupplier shouldJettison) {
    rollerSubsystem = subsystem;
    this.shouldIntake = shouldIntake;
    this.shouldJettison = shouldJettison;
    addRequirements(rollerSubsystem);
  }

  @Override
  public void execute() {
    if (shouldIntake.getAsBoolean()) {
      rollerSubsystem.runRollerIntake();
    } else if (shouldJettison.getAsBoolean()) {
      rollerSubsystem.runRollerJettison();
    } else {
      if (DriverStation.isTeleop()) {
        rollerSubsystem.stallRoller();
      }
    }
  }

  @Override
  public boolean isFinished() {
    return true;
  }
}