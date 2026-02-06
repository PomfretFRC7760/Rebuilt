// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.FloorRollerCommand;
import frc.robot.commands.GyroCommand;
import frc.robot.commands.LiftAndScore;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;
import frc.robot.subsystems.FloorIntakeRollerSubsystem;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.commands.LiftCommand;
import frc.robot.subsystems.FloorIntakeRotationSubsystem;
import frc.robot.commands.FloorRotationCommand;
import frc.robot.commands.LiftRollerCommand;
import frc.robot.commands.CameraCommand;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.commands.FuelShoot;
import frc.robot.commands.LimelightPoseReset; 
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.util.AutoConfig;
import frc.robot.util.LocationChooser;
import frc.robot.commands.LiftAutoPosition;
import frc.robot.util.FuelConstants;
import frc.robot.util.FuelLocator;
import frc.robot.commands.FuelLocatorCommand;
import frc.robot.subsystems.LiftRotationSubsystem;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.HubStatusSubsystem;
import frc.robot.commands.ClimbCommand;
/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems
  private final FuelLocator fuelLocator = new FuelLocator(this);
  private final FloorIntakeRollerSubsystem rollerSubsystem = new FloorIntakeRollerSubsystem();
  private final GyroSubsystem gyroSubsystem = new GyroSubsystem();

  private final GyroCommand gyroCommand = new GyroCommand(gyroSubsystem);
  
  public final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem(gyroSubsystem);
  private final LiftSubsystem liftSubsystem = new LiftSubsystem();

  private final FloorIntakeRotationSubsystem floorIntakeRotationSubsystem = new FloorIntakeRotationSubsystem();

  

  private final LiftIntakeRollerSubsystem liftIntakeRollerSubsystem = new LiftIntakeRollerSubsystem();
  private final CameraSubsystem cameraSubsystem = new CameraSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem(driveSubsystem, fuelLocator);

  public final DriveCommand driveCommand;

  public final FuelLocatorCommand fuelLocatorCommand = new FuelLocatorCommand(visionSubsystem);


  private final LocationChooser locationChooser = new LocationChooser(this);

  private LimelightPoseReset limelightPoseReset = new LimelightPoseReset(driveSubsystem, visionSubsystem);

  private LiftRotationSubsystem liftRotationSubsystem = new LiftRotationSubsystem();

  public final AutoConfig autoConfig;

  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final HubStatusSubsystem hubStatusSubsystem = new HubStatusSubsystem();

  // The driver's controller
  private final CommandXboxController driverController = new CommandXboxController(
      0);

  // The operator's controller
  private final CommandXboxController operatorController = new CommandXboxController(
      1);

  // The autonomous chooser
  private final SendableChooser<Boolean> autoMode = new SendableChooser<>();
  private final SendableChooser<Command> autoChooser;

  private Pose2d lastSelectedPose = null;
  

  private final LiftCommand liftCommand = new LiftCommand(() -> driverController.povUp().getAsBoolean(), () -> driverController.povDown().getAsBoolean(), () -> driverController.povLeft().getAsBoolean(), () -> driverController.povRight().getAsBoolean(), () -> driverController.x().getAsBoolean(), () -> driverController.rightBumper().getAsBoolean(), () -> operatorController.getRightY(), liftSubsystem, liftRotationSubsystem);

  private final FloorRotationCommand floorRotationCommand = new FloorRotationCommand(() -> operatorController.povUp().getAsBoolean(), () -> operatorController.povDown().getAsBoolean(), () -> operatorController.povLeft().getAsBoolean(), () -> operatorController.povRight().getAsBoolean(), () -> operatorController.getLeftY(), floorIntakeRotationSubsystem, this);

  public final ClimbCommand climbCommand = new ClimbCommand(climbSubsystem, floorIntakeRotationSubsystem, () -> operatorController.leftBumper().getAsBoolean(), () -> operatorController.rightBumper().getAsBoolean());

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    NamedCommands.registerCommand("Stow", new LiftAndScore(liftSubsystem, liftRotationSubsystem, 1));
    autoConfig = new AutoConfig(this);

    configureBindings();

    driveCommand = new DriveCommand(
        () -> -driverController.getLeftY(),
        () -> -driverController.getRightX(),
        () -> driverController.y().getAsBoolean(), () -> driverController.x().getAsBoolean(),
        driveSubsystem,
        locationChooser, autoConfig, liftSubsystem, liftIntakeRollerSubsystem, fuelLocatorCommand, liftRotationSubsystem, hubStatusSubsystem
    );
    driveSubsystem.setDefaultCommand(driveCommand);

    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // Set the A button to run the "RollerCommand" command with a fixed
    // value ejecting the gamepiece while the button is held

    // before

    
      
    
  

    // Set the default command for the drive subsystem to an instance of the
    // DriveCommand with the values provided by the joystick axes on the driver
    // controller. The Y axis of the controller is inverted so that pushing the
    // stick away from you (a negative value) drives the robot forwards (a positive
    // value). Similarly for the X axis where we need to flip the value so the
    // joystick matches the WPILib convention of counter-clockwise positive

    // Set the default command for the roller subsystem to an instance of
    // RollerCommand with the values provided by the triggers on the operator
    // controller
    SmartDashboard.putData("Reset gyro", new InstantCommand(() -> driveSubsystem.resetGyro()).ignoringDisable(true));
    liftSubsystem.setDefaultCommand(liftCommand);
    SmartDashboard.putData("Reset lift encoders", new InstantCommand(() -> liftCommand.resetLiftPosition()).ignoringDisable(true));
    rollerSubsystem.setDefaultCommand(new FloorRollerCommand(rollerSubsystem, () -> operatorController.a().getAsBoolean(), () -> operatorController.b().getAsBoolean()));
    floorIntakeRotationSubsystem.setDefaultCommand(floorRotationCommand);
    liftIntakeRollerSubsystem.setDefaultCommand(new LiftRollerCommand(liftIntakeRollerSubsystem, liftRotationSubsystem, () -> driverController.a().getAsBoolean(), () -> driverController.b().getAsBoolean(), hubStatusSubsystem));
    cameraSubsystem.setDefaultCommand(new CameraCommand(cameraSubsystem));
    climbSubsystem.setDefaultCommand(climbCommand);
    SmartDashboard.putData("Reset pose with Limelight", new InstantCommand(() -> limelightPoseReset.resetPose()).ignoringDisable(true));
    SmartDashboard.putData("Enable lift manual control", new InstantCommand(() -> liftCommand.liftManualControlSwitch()));
    SmartDashboard.putData("Enable fuel intake manual control", new InstantCommand(() -> floorRotationCommand.enableManualControl()));
    SmartDashboard.putData("Enable fuel intake manual control", new InstantCommand(() -> liftCommand.fuelManualControlSwitch()));
    SmartDashboard.putData("Abort semi-autonomous", new InstantCommand(() -> driveCommand.autoAbort()));
    SmartDashboard.putData("Drive to fuel", new InstantCommand(() -> driveCommand.driveToFuel()));
    SmartDashboard.putData("Apriltag pipeline", new InstantCommand(() -> visionSubsystem.setPipeline0()).ignoringDisable(true));
    SmartDashboard.putData("Neural network pipeline", new InstantCommand(() -> visionSubsystem.setPipeline1()).ignoringDisable(true));
    SmartDashboard.putData("Experimental neural network pipeline", new InstantCommand(() -> visionSubsystem.setPipeline2()).ignoringDisable(true));
    SmartDashboard.putData("Reset fuel intake encoder", new InstantCommand(() -> floorIntakeRotationSubsystem.resetEncoder()).ignoringDisable(true));
    SmartDashboard.putData("Reset fuel intake encoder", new InstantCommand(() -> liftRotationSubsystem.resetEncoder()).ignoringDisable(true));
  }

  public void updateSelectedPose() {
    Pose2d currentPose = locationChooser.selectFuelLocation(); // Get current selection

    if (currentPose != null && !currentPose.equals(lastSelectedPose)) {
        lastSelectedPose = currentPose; // Update last pose
        driveCommand.driveToSelectedPose(); // Trigger drive function
    }
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
