package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.HubStatusSubsystem;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.util.AutoConfig;
import frc.robot.util.LocationChooser;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.util.Units;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import frc.robot.commands.FuelLocatorCommand;

public class DriveCommand extends Command {
  private final DoubleSupplier speed;
  private final DoubleSupplier rotation;
  private final BooleanSupplier robotCentric;
  private final BooleanSupplier abortAuto;
  private final CANDriveSubsystem driveSubsystem;
  private final LocationChooser locationChooser;
  private final AutoConfig autoConfig;
  private final LiftSubsystem liftSubsystem;
  private final LiftIntakeRollerSubsystem liftIntakeRollerSubsystem;
  private final LiftRotationSubsystem liftRotationSubsystem;
  private final HubStatusSubsystem hubStatus;

  private final FuelLocatorCommand fuelLocatorCommand;
  
  private boolean robotCentricMode = false;
  private boolean lastRobotCentricButtonState = false;
  private Command pathfindingCommand = null; // Store the pathfinding command
  private Command activePathfindingCommand = null; // Store the active pathfinding command
  
  private final PathConstraints constraints = new PathConstraints(
      3.0, 4.0,
      Units.degreesToRadians(540), Units.degreesToRadians(720)
  );

  public DriveCommand(DoubleSupplier speed, DoubleSupplier rotation,
                      BooleanSupplier robotCentric, BooleanSupplier abortAuto,
                      CANDriveSubsystem driveSubsystem, LocationChooser locationChooser,
                      AutoConfig autoConfig, LiftSubsystem liftSubsystem,
                      LiftIntakeRollerSubsystem liftIntakeRollerSubsystem, FuelLocatorCommand fuelLocatorCommand, LiftRotationSubsystem liftRotationSubsystem, HubStatusSubsystem hubStatus) {
    this.speed = speed;
    this.rotation = rotation;
    this.robotCentric = robotCentric;
    this.abortAuto = abortAuto;
    this.driveSubsystem = driveSubsystem;
    this.locationChooser = locationChooser;
    this.autoConfig = autoConfig;
    this.liftSubsystem = liftSubsystem;
    this.liftIntakeRollerSubsystem = liftIntakeRollerSubsystem;
    this.fuelLocatorCommand = fuelLocatorCommand;
    this.liftRotationSubsystem = liftRotationSubsystem;
    this.hubStatus = hubStatus;

    addRequirements(this.driveSubsystem, this.liftSubsystem, this.liftIntakeRollerSubsystem, this.liftRotationSubsystem);
  }

  @Override
  public void execute() {
    // Toggle robot-centric mode on button press (rising edge detection)
    boolean currentButtonState = robotCentric.getAsBoolean();
    if (currentButtonState && !lastRobotCentricButtonState) {
      robotCentricMode = !robotCentricMode;  // Toggle mode
    }
    lastRobotCentricButtonState = currentButtonState;  // Update last state

    // Drive based on selected mode
    driveSubsystem.driveRobot(speed.getAsDouble(), rotation.getAsDouble());

    // Display selected pose
    Pose2d selectedPose = locationChooser.selectFuelLocation();
    SmartDashboard.putString("Selected Robot Pose", 
    (selectedPose != null) ? selectedPose.toString() : "None");
    SmartDashboard.putBoolean("target found", fuelLocatorCommand.validateTarget());
    Pose2d fuelPose = fuelLocatorCommand.getFuelPose();
    SmartDashboard.putString("Fuel Pose", fuelPose != null ? fuelPose.toString() : "Unacceptable");
  }

  public void driveToSelectedPose() {
    Pose2d selectedPose = locationChooser.selectFuelLocation();

    if (selectedPose == null) {
        return;
    }

    pathfindingCommand = createPathfindingCommand(selectedPose);

    activePathfindingCommand = pathfindingCommand.andThen(simulationPoseReset(selectedPose));

    CommandScheduler.getInstance().schedule(activePathfindingCommand);
  }

  public void driveToFuel(){
    if (fuelLocatorCommand.getFuelPose() != null) {
      Pose2d fuelPose = fuelLocatorCommand.getFuelPose();
      pathfindingCommand = createPathfindingCommand(fuelPose);

      activePathfindingCommand = pathfindingCommand.andThen(simulationPoseReset(fuelPose));

      CommandScheduler.getInstance().schedule(activePathfindingCommand);
    }
  }

  private Command simulationPoseReset(Pose2d targetPose) {
    if (driveSubsystem.getGyroAngle() == 0.0) {
      return new InstantCommand(() -> driveSubsystem.resetPose(targetPose));
    }
    else {
      return Commands.none();
    }
  }


  // Helper method to create pathfinding commands dynamically
  private Command createPathfindingCommand(Pose2d targetPose) {
    return AutoBuilder.pathfindToPose(targetPose, constraints, 0.0).andThen(simulationPoseReset(targetPose));
  }

  // Generic auto fuel method
  private Command autoFuel(Pose2d fuelPose, Pose2d stationPose, int liftLevel, boolean enabled, int pickup) {
    if (!enabled) return Commands.none();
    if (stationPose == null) {
      Command pathToFuel = createPathfindingCommand(fuelPose);
      Command liftCommand = new LiftAndScore(liftSubsystem, liftRotationSubsystem, liftLevel);
      Command jettisonCommand = new FuelShoot(liftIntakeRollerSubsystem, hubStatus);
      Command resetLift = new LiftAndScore(liftSubsystem, liftRotationSubsystem, pickup);

      return pathToFuel.andThen(liftCommand).andThen(jettisonCommand).andThen(resetLift);
    } else {
      Command pathToFuel = createPathfindingCommand(fuelPose);
      Command pathToStation = createPathfindingCommand(stationPose);
      Command liftCommand = new LiftAndScore(liftSubsystem, liftRotationSubsystem, liftLevel);
      Command intakeCommand = new FuelIntake(liftIntakeRollerSubsystem);
      Command jettisonCommand = new FuelShoot(liftIntakeRollerSubsystem, hubStatus);
      Command resetLift = new LiftAndScore(liftSubsystem, liftRotationSubsystem, pickup);

      return pathToStation
          .andThen(intakeCommand)
          .andThen(pathToFuel)
          .andThen(liftCommand)
          .andThen(jettisonCommand)
          .andThen(resetLift);
    }
  }


  public Command buildFullAutoSequence() {
    Command fuel1 = autoFuel(autoConfig.fuel1Pose, null, autoConfig.lift1, autoConfig.enable1, autoConfig.pickup2);
    Command fuel2 = autoFuel(autoConfig.fuel2Pose, autoConfig.station2Pose, autoConfig.lift2, autoConfig.enable2, autoConfig.pickup3);
    Command fuel3 = autoFuel(autoConfig.fuel3Pose, autoConfig.station3Pose, autoConfig.lift3, autoConfig.enable3, autoConfig.pickup4);
    Command fuel4 = autoFuel(autoConfig.fuel4Pose, autoConfig.station4Pose, autoConfig.lift4, autoConfig.enable4, 6);
  
    return fuel1.andThen(fuel2).andThen(fuel3).andThen(fuel4);
  }

  public void autoAbort() {
    if (activePathfindingCommand != null && !activePathfindingCommand.isFinished()) {
      activePathfindingCommand.cancel();
      pathfindingCommand = null;
      activePathfindingCommand = null;
    }
  }
  

  @Override
  public void end(boolean isInterrupted) {
    if (activePathfindingCommand != null) {
      activePathfindingCommand.cancel();
      pathfindingCommand = null;
      activePathfindingCommand = null;
    }
  }


  @Override
  public boolean isFinished() {
    return false;
  }
}
