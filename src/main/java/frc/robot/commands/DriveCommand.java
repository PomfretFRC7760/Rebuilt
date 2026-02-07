package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.VisionSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.Waypoint;

import edu.wpi.first.math.util.Units;
import java.util.function.DoubleSupplier;
import java.util.List;
import java.util.function.BooleanSupplier;

public class DriveCommand extends Command {
  private final DoubleSupplier speed;
  private final DoubleSupplier rotation;
  private final DoubleSupplier aimTrigger;
  private final CANDriveSubsystem driveSubsystem;
  private final VisionSubsystem visionSubsystem;
  private final BooleanSupplier passingModeToggle;
  private boolean autoAimActive = false;
  private boolean passingMode = false;
  private List<Waypoint> waypoints;
  private PathPlannerPath path;

  
  private Command activePathfindingCommand = null; // Store the active pathfinding command
  
  private final PathConstraints constraints = new PathConstraints(
      3.0, 4.0,
      Units.degreesToRadians(540), Units.degreesToRadians(720)
  );

  public DriveCommand(DoubleSupplier speed, DoubleSupplier rotation,
                      CANDriveSubsystem driveSubsystem, VisionSubsystem visionSubsystem, DoubleSupplier aimTrigger, BooleanSupplier passingModeToggle) {
    this.speed = speed;
    this.rotation = rotation;
    this.aimTrigger = aimTrigger;
    this.driveSubsystem = driveSubsystem;
    this.visionSubsystem = visionSubsystem;
    this.passingModeToggle = passingModeToggle;

    addRequirements(this.driveSubsystem);
  }

  @Override
  public void execute() {
      double triggerValue = aimTrigger.getAsDouble();
      SmartDashboard.putNumber("tag number", visionSubsystem.getAprilTagID());
      // Rising-edge detect on trigger
      if (triggerValue > 0.75 && !autoAimActive && visionSubsystem.getAprilTagID() != -1 && !passingMode) {
          autoAim();
          SmartDashboard.putString("aim active", "aim active");
          autoAimActive = true;
      }

      // Reset latch when trigger released
      if (triggerValue <= 0.75) {
          autoAimActive = false;
          SmartDashboard.putString("aim active", "aim inactive");
      }

      if (passingModeToggle.getAsBoolean()) {
        if (passingMode) {
          passingMode = false;
        }
        else if (!passingMode) {
          passingMode = true;
        }
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
    SmartDashboard.putNumber("tx", tx);

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

    SmartDashboard.putString("Current pose", currentPose.toString());
    SmartDashboard.putString("Target pose", targetPose.toString());

    List<Waypoint> waypoints = PathPlannerPath.waypointsFromPoses(
        currentPose,
        targetPose
    );
    path = new PathPlannerPath(
        waypoints,
        constraints,
        null, // The ideal starting state, this is only relevant for pre-planned paths, so can be null for on-the-fly paths.
        new GoalEndState(0.0, new edu.wpi.first.math.geometry.Rotation2d(targetHeadingRad)) // Goal end state. You can set a holonomic rotation here. If using a differential drivetrain, the rotation will have no effect.
    );  

    path.preventFlipping = true;
    // Create and schedule pathfinding command
    activePathfindingCommand =
            AutoBuilder.followPath(path);

    CommandScheduler.getInstance().schedule(activePathfindingCommand);

    // Debug
    SmartDashboard.putNumber("AutoAim/tx", tx);
    SmartDashboard.putNumber(
            "AutoAim/TargetHeadingDeg",
            Math.toDegrees(targetHeadingRad)
    );
}

public boolean isPassingMode() {
    return passingMode;
}
}
