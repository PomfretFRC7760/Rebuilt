package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.simulation.PDPSim;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.GyroCommand;
import frc.robot.commands.HoodPosition;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IntakeAndShooterSubsystem;
import frc.robot.subsystems.PDPSubsystem;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.commands.FuelShoot;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.commands.ClimbCommand;
public class RobotContainer {
  //subsystems
  private final GyroSubsystem gyroSubsystem = new GyroSubsystem();
  
  public final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem(gyroSubsystem);

  private final CameraSubsystem cameraSubsystem = new CameraSubsystem();

  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  private final PDPSubsystem PDPSubsystem = new PDPSubsystem();

  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();

  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();

  private final IntakeAndShooterSubsystem intakeAndShooterSubsystem = new IntakeAndShooterSubsystem();

  // The driver's controller
  private final CommandXboxController driverController = new CommandXboxController(
      0);

  // The operator's controller
  private final CommandXboxController operatorController = new CommandXboxController(
      1);

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser;
  
  public RobotContainer() {
    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
    configureBindings();
    createNamedCommands();
  }

  private void configureBindings() {

    private final LiftCommand liftCommand = new LiftCommand(() -> driverController.povUp().getAsBoolean(), () -> driverController.povDown().getAsBoolean(), () -> driverController.povLeft().getAsBoolean(), () -> driverController.povRight().getAsBoolean(), () -> driverController.x().getAsBoolean(), () -> driverController.rightBumper().getAsBoolean(), () -> operatorController.getRightY(), liftSubsystem, liftRotationSubsystem);

    private final FloorRotationCommand floorRotationCommand = new FloorRotationCommand(() -> operatorController.povUp().getAsBoolean(), () -> operatorController.povDown().getAsBoolean(), () -> operatorController.povLeft().getAsBoolean(), () -> operatorController.povRight().getAsBoolean(), () -> operatorController.getLeftY(), floorIntakeRotationSubsystem, this);

    climbCommand = new ClimbCommand(climbSubsystem, floorIntakeRotationSubsystem, () -> operatorController.leftBumper().getAsBoolean(), () -> operatorController.rightBumper().getAsBoolean());

    SmartDashboard.putData("Reset gyro", new InstantCommand(() -> driveSubsystem.resetGyro()).ignoringDisable(true));
    liftSubsystem.setDefaultCommand(liftCommand);
    SmartDashboard.putData("Reset lift encoders", new InstantCommand(() -> liftCommand.resetLiftPosition()).ignoringDisable(true));
    rollerSubsystem.setDefaultCommand(new FloorRollerCommand(rollerSubsystem, () -> operatorController.a().getAsBoolean(), () -> operatorController.b().getAsBoolean()));
    floorIntakeRotationSubsystem.setDefaultCommand(floorRotationCommand);
    liftIntakeRollerSubsystem.setDefaultCommand(new LiftRollerCommand(liftIntakeRollerSubsystem, liftRotationSubsystem, () -> driverController.a().getAsBoolean(), () -> driverController.b().getAsBoolean(), hubStatusSubsystem));
    cameraSubsystem.setDefaultCommand(new CameraCommand(cameraSubsystem));
    climbSubsystem.setDefaultCommand(climbCommand);

    driveSubsystem.setDefaultCommand(new DriveCommand(
        () -> -driverController.getLeftY(),
        () -> -driverController.getRightX(),
        () -> driverController.y().getAsBoolean(), () -> driverController.x().getAsBoolean(),
        driveSubsystem,
        locationChooser, autoConfig, liftSubsystem, liftIntakeRollerSubsystem, fuelLocatorCommand, liftRotationSubsystem, hubStatusSubsystem
    ));
  }

  private void createNamedCommands() {
    NamedCommands.registerCommand("Stow", new LiftAndScore(liftSubsystem, liftRotationSubsystem, 1));
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
