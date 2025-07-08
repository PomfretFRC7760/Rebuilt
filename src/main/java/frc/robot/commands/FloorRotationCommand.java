package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.FloorIntakeRotationSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.RobotContainer;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class FloorRotationCommand extends Command {
    private final FloorIntakeRotationSubsystem intakeSubsystem;
    private final BooleanSupplier dPadUp;
    private final BooleanSupplier dPadDown;
    private final BooleanSupplier dPadLeft;
    private final BooleanSupplier dPadRight;
    private final DoubleSupplier leftStickY;
    private boolean manualControl = false;
    private final RobotContainer robotContainer;

    public FloorRotationCommand(BooleanSupplier dPadUp, BooleanSupplier dPadDown, BooleanSupplier dPadLeft, BooleanSupplier dPadRight, DoubleSupplier leftStickY, FloorIntakeRotationSubsystem intakeSubsystem, RobotContainer robotContainer) {
        this.intakeSubsystem = intakeSubsystem;
        this.dPadUp = dPadUp;
        this.dPadDown = dPadDown;
        this.dPadLeft = dPadLeft;
        this.dPadRight = dPadRight;
        this.leftStickY = leftStickY;
        this.robotContainer = robotContainer;
        addRequirements(intakeSubsystem);
    }

    @Override

    public void initialize(){
    }

    @Override
    public void execute() {
        if (!robotContainer.climbCommand.armClimb) {
            if (dPadDown.getAsBoolean() || dPadUp.getAsBoolean() || dPadLeft.getAsBoolean() || dPadRight.getAsBoolean()) {
                manualControl = false;
            }
            if (manualControl) {
                intakeSubsystem.manualControl(leftStickY.getAsDouble());
            }
            if (!manualControl) {
                intakeSubsystem.updatePosition();
                if (dPadUp.getAsBoolean()) {
                    intakeSubsystem.autoPosition(0);
                } else if (dPadDown.getAsBoolean()) {
                    intakeSubsystem.autoPosition(-36.5);
                } else if (dPadLeft.getAsBoolean()) {
                    intakeSubsystem.autoPosition(-18.5);
                } else if (dPadRight.getAsBoolean()) {
                    intakeSubsystem.autoPosition(-24.5);
                }
            }
        }
    }

    public void enableManualControl() {
        manualControl = true;
    }
}
