package frc.robot;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.XboxController;
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
import frc.robot.commands.FuelIntakeAndShoot;
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

  //commands
  private DriveCommand driveCommand;

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
    climbSubsystem.setDefaultCommand(
    new ClimbCommand(climbSubsystem, () -> driverController.getPOV() == 180, () -> driverController.getPOV() == 0));

    driveCommand = new DriveCommand(
        () -> -driverController.getLeftY(),
        () -> -driverController.getRightX(),
        driveSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), () -> driverController.getXButton() 
    );
    driveSubsystem.setDefaultCommand(driveCommand);

    hoodSubsystem.setDefaultCommand(new HoodPosition(hoodSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), () -> driveCommand.isPassingMode()));

    gyroSubsystem.setDefaultCommand(new GyroCommand(gyroSubsystem, () -> driverController.getStartButton()));

    intakeAndShooterSubsystem.setDefaultCommand(new FuelIntakeAndShoot(intakeAndShooterSubsystem, visionSubsystem, () -> driverController.getLeftTriggerAxis(), 
    () -> driveCommand.isPassingMode(), () -> driverController.getRightTriggerAxis(), () -> driverController.getRightBumperButton()));
  }

  private void createNamedCommands() {
    //NamedCommands.registerCommand("Stow", new LiftAndScore(liftSubsystem, liftRotationSubsystem, 1));
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
