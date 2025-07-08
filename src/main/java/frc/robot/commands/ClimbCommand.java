package frc.robot.commands;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.FloorIntakeRotationSubsystem;

import java.util.function.BooleanSupplier;

public class ClimbCommand extends Command {

    private final ClimbSubsystem climbSubsystem;
    private final FloorIntakeRotationSubsystem floorIntakeRotationSubsystem;
    private final BooleanSupplier leftBumper;
    private final BooleanSupplier rightBumper;

    public boolean armClimb = false;
    public ClimbCommand(ClimbSubsystem climbSubsystem, FloorIntakeRotationSubsystem floorIntakeRotationSubsystem, BooleanSupplier leftBumper, BooleanSupplier rightBumper) {
        this.climbSubsystem = climbSubsystem;
        this.floorIntakeRotationSubsystem = floorIntakeRotationSubsystem;
        this.leftBumper = leftBumper;
        this.rightBumper = rightBumper;
        addRequirements(climbSubsystem, floorIntakeRotationSubsystem);
    }
    @Override
    public void initialize(){
        SmartDashboard.putData("Arm climb (cannot be disarmed after arming!)", new InstantCommand(() -> armClimb()));
    }
    @Override
    public void execute() {
        if (armClimb) {
            if (floorIntakeRotationSubsystem.getPosition() > -37.5 && floorIntakeRotationSubsystem.getPosition() < -35.5){
                floorIntakeRotationSubsystem.autoPosition(-36.5);
                return;
            }
            if (leftBumper.getAsBoolean()) {
                floorIntakeRotationSubsystem.manualControl(0);
                floorIntakeRotationSubsystem.setCoast();
                climbSubsystem.runMotor(-0.75);
                
                return;
            } else if (rightBumper.getAsBoolean()) {
                floorIntakeRotationSubsystem.manualControl(0);
                floorIntakeRotationSubsystem.setCoast();
                climbSubsystem.runMotor(0.75);
                
                return;
            } else {
                climbSubsystem.runMotor(0);
                floorIntakeRotationSubsystem.stallMotor();
            }
            if (DriverStation.isDisabled()) {
                floorIntakeRotationSubsystem.setBrake();
            }

        }
    }
    public void armClimb(){
        armClimb = true;
    }
}
