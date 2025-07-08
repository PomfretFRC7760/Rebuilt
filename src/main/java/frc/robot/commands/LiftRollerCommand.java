package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;

import java.util.function.BooleanSupplier;

public class LiftRollerCommand extends Command {
  private final LiftIntakeRollerSubsystem rollerSubsystem;
  private final LiftRotationSubsystem liftRotationSubsystem;
  private final BooleanSupplier shouldIntake;
  private final BooleanSupplier shouldJettison;

  public LiftRollerCommand(LiftIntakeRollerSubsystem subsystem, LiftRotationSubsystem liftRotationSubsystem, BooleanSupplier shouldIntake, BooleanSupplier shouldJettison) {
    rollerSubsystem = subsystem;
    this.shouldIntake = shouldIntake;
    this.shouldJettison = shouldJettison;
    this.liftRotationSubsystem = liftRotationSubsystem;
    addRequirements(rollerSubsystem, liftRotationSubsystem);
  }

  @Override
  public void execute() {
      if (shouldIntake.getAsBoolean()) {
        rollerSubsystem.runRollerIntake();
      }
      else if (shouldJettison.getAsBoolean()) {
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