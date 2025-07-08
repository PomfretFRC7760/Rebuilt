package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftCommand extends Command {
    private final LiftSubsystem liftSubsystem;
    private final BooleanSupplier dPadUp;
    private final BooleanSupplier dPadDown;
    private final BooleanSupplier dPadLeft;
    private final BooleanSupplier dPadRight;
    private final DoubleSupplier rightStickY;
    private final BooleanSupplier xButton;
    private final BooleanSupplier leftShoulder;
    private final LiftRotationSubsystem liftRotationSubsystem;
    private boolean liftManualControlEnabled = false;
    private boolean coralManualControlEnabled = false;

    private final SendableChooser<Boolean> algae = new SendableChooser<>();
    public LiftCommand(BooleanSupplier dPadUp, BooleanSupplier dPadDown, BooleanSupplier dPadLeft, BooleanSupplier dPadRight, BooleanSupplier xButton, BooleanSupplier leftShoulder, DoubleSupplier rightStickY, LiftSubsystem liftSubsystem, LiftRotationSubsystem liftRotationSubsystem) {
        this.dPadUp = dPadUp;
        this.dPadDown = dPadDown;
        this.dPadLeft = dPadLeft;
        this.dPadRight = dPadRight;
        this.rightStickY = rightStickY;
        this.liftSubsystem = liftSubsystem;
        this.xButton = xButton;
        this.leftShoulder = leftShoulder;
        this.liftRotationSubsystem = liftRotationSubsystem;
        addRequirements(liftSubsystem, liftRotationSubsystem);

        
    }

    @Override
    public void initialize() {
        algae.setDefaultOption("Coral", false);
        algae.addOption("Algae", true);
        SmartDashboard.putData("Scoring mode", algae);
    }

    @Override
    public void execute() {
        double speed = rightStickY.getAsDouble();
        // Toggle manual control mode
        if (dPadDown.getAsBoolean() || dPadUp.getAsBoolean() || dPadLeft.getAsBoolean() || dPadRight.getAsBoolean() || xButton.getAsBoolean() || leftShoulder.getAsBoolean()) {
            liftManualControlEnabled = false;
            coralManualControlEnabled = false;
        }

        if (liftManualControlEnabled) {
            // Manual control mode
            liftSubsystem.manualOverrideControl(speed);
        }
        else if (coralManualControlEnabled) {
            // Manual control mode
            liftRotationSubsystem.manualControl(speed);
        } else if (liftManualControlEnabled && coralManualControlEnabled) {
            liftSubsystem.manualOverrideControl(speed);
            liftRotationSubsystem.manualControl(speed);
        } else {
            if (algae.getSelected()) {
                if (dPadUp.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 7).schedule();
                }

                else if (dPadLeft.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 8).schedule();

                }

                else if (dPadRight.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 9).schedule();
                }
            }
            else {
                if (dPadUp.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 2).schedule();
                }

                else if (dPadDown.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 5).schedule();
                }

                else if (dPadLeft.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 3).schedule();

                }

                else if (dPadRight.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 4).schedule();
                }

                else if (xButton.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 1).schedule();
                }

                else if (leftShoulder.getAsBoolean()) {
                    new LiftAndScore(liftSubsystem, liftRotationSubsystem, 6).schedule();
                }
            }
            liftSubsystem.updatePosition();
        }
    }

    public void resetLiftPosition() {
        liftSubsystem.resetPosition();
    }

    public void liftManualControlSwitch() {
        liftManualControlEnabled = true;
    }
    public void coralManualControlSwitch() {
        coralManualControlEnabled = true;
    }
}
