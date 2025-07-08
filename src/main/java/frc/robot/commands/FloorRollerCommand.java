package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FloorIntakeRollerSubsystem;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

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
    }
    else if (shouldJettison.getAsBoolean()) {
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