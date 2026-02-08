package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.GyroCommand;
import frc.robot.commands.HoodPosition;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.GyroSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IntakeAndShooterSubsystem;
import frc.robot.subsystems.PDPSubsystem;
import frc.robot.subsystems.CameraSubsystem;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.commands.FuelIntakeAndShoot;
import frc.robot.subsystems.VisionSubsystem;
import frc.robot.commands.ClimbCommand;
public class RobotContainer {
  //subsystems
  private final GyroSubsystem gyroSubsystem = new GyroSubsystem();
  
  public final DriveSubsystem driveSubsystem = new DriveSubsystem(gyroSubsystem);

  private final CameraSubsystem cameraSubsystem = new CameraSubsystem();

  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  private final PDPSubsystem PDPSubsystem = new PDPSubsystem();

  private final HoodSubsystem hoodSubsystem = new HoodSubsystem();

  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();

  private final IntakeAndShooterSubsystem intakeAndShooterSubsystem = new IntakeAndShooterSubsystem();

  //commands
  private DriveCommand driveCommand;
  
  private ClimbCommand climbCommand;
  
  private FuelIntakeAndShoot fuelIntakeAndShoot;

  private HoodPosition hoodPosition;

  private GyroCommand gyroCommand;

  // The driver's controller
  private final XboxController driverController = new XboxController(
      0);

  // The operator's controller
  private final XboxController operatorController = new XboxController(
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
    climbCommand = new ClimbCommand(climbSubsystem, () -> driverController.getStartButtonPressed(), () -> driverController.getYButtonPressed());

    climbSubsystem.setDefaultCommand(climbCommand);

    driveCommand = new DriveCommand(
        () -> -driverController.getLeftY(),
        () -> -driverController.getRightX(),
        driveSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), () -> driverController.getXButtonPressed() 
    );

    driveSubsystem.setDefaultCommand(driveCommand);

    hoodPosition = new HoodPosition(hoodSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), () -> driveCommand.isPassingMode());

    hoodSubsystem.setDefaultCommand(hoodPosition);

    gyroCommand = new GyroCommand(gyroSubsystem, () -> driverController.getBackButtonPressed());
    
    gyroSubsystem.setDefaultCommand(gyroCommand.ignoringDisable(true));

    fuelIntakeAndShoot = new FuelIntakeAndShoot(intakeAndShooterSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), 
    () -> driveCommand.isPassingMode(), () -> driverController.getRightTriggerAxis(), () -> driverController.getLeftBumperButton());

    intakeAndShooterSubsystem.setDefaultCommand(fuelIntakeAndShoot);
  }

  private void createNamedCommands() {
    NamedCommands.registerCommand("Arm Climb", new InstantCommand(() -> climbCommand.autoPhaseArmClimb()));
    NamedCommands.registerCommand("Climb L1", new InstantCommand(() -> climbCommand.autoPhaseClimb()));
    NamedCommands.registerCommand("Intake Fuel", new InstantCommand(() -> intakeAndShooterSubsystem.intake()));
    NamedCommands.registerCommand("Spool up", new InstantCommand(() -> fuelIntakeAndShoot.autoSpoolUp()));
    NamedCommands.registerCommand("Shoot", new InstantCommand(() -> intakeAndShooterSubsystem.shoot()));
    NamedCommands.registerCommand("Aim hood", new InstantCommand(() -> hoodPosition.autoPositionHood()));
  } 

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
