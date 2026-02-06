package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.util.Units;
import java.util.function.DoubleSupplier;

public class DriveCommand extends Command {
  private final DoubleSupplier speed;
  private final DoubleSupplier rotation;
  private final DoubleSupplier aimTrigger;
  private final CANDriveSubsystem driveSubsystem;
  private final VisionSubsystem visionSubsystem;
  private boolean autoAimActive = false;
  
  private Command activePathfindingCommand = null; // Store the active pathfinding command
  
  private final PathConstraints constraints = new PathConstraints(
      3.0, 4.0,
      Units.degreesToRadians(540), Units.degreesToRadians(720)
  );

  public DriveCommand(DoubleSupplier speed, DoubleSupplier rotation,
                      CANDriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem, DoubleSupplier aimTrigger) {
    this.speed = speed;
    this.rotation = rotation;
    this.aimTrigger = aimTrigger;
    this.driveSubsystem = driveSubsystem;
    this.visionSubsystem = visionSubsystem;

    addRequirements(this.driveSubsystem);
  }

  @Override
  public void execute() {
      double triggerValue = aimTrigger.getAsDouble();

      // Rising-edge detect on trigger
      if (triggerValue > 0.75 && !autoAimActive) {
          autoAim();
          autoAimActive = true;
      }

      // Reset latch when trigger released
      if (triggerValue <= 0.75) {
          autoAimActive = false;
      }

      // Normal driving always allowed
      driveSubsystem.driveRobot(
          speed.getAsDouble(),
          rotation.getAsDouble()
      );
  }


  public void autoAim() {
    // Get vision target offset
    double tx = visionSubsystem.getTx();

    // Get current robot pose
    Pose2d currentPose = driveSubsystem.getPose();
    if (currentPose == null) {
        return;
    }

    // Current heading
    double currentHeadingRad = currentPose.getRotation().getRadians();

    // tx is in degrees → convert to radians
    double targetHeadingRad =
            currentHeadingRad + Units.degreesToRadians(tx);

    // Create new target pose:
    // SAME translation, NEW rotation
    Pose2d targetPose = new Pose2d(
            currentPose.getTranslation(),
            new edu.wpi.first.math.geometry.Rotation2d(targetHeadingRad)
    );

    // Cancel any existing pathfinding
    if (activePathfindingCommand != null) {
        activePathfindingCommand.cancel();
    }

    // Create and schedule pathfinding command
    activePathfindingCommand =
            AutoBuilder.pathfindToPose(
                    targetPose,
                    constraints,
                    0.0
            );

    CommandScheduler.getInstance().schedule(activePathfindingCommand);

    // Debug
    SmartDashboard.putNumber("AutoAim/tx", tx);
    SmartDashboard.putNumber(
            "AutoAim/TargetHeadingDeg",
            Math.toDegrees(targetHeadingRad)
    );
}

  @Override
  public boolean isFinished() {
    return false;
  }
}
