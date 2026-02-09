package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.GyroSubsystem;

public class GyroCommand extends Command {
    private final GyroSubsystem gyroSubsystem;
    private final BooleanSupplier resetButton;
    private final BooleanSupplier calibrateButton;

    public GyroCommand(GyroSubsystem gyroSubsystem, BooleanSupplier resetButton, BooleanSupplier calibrateButton) {
        this.gyroSubsystem = gyroSubsystem;
        this.resetButton = resetButton;
        this.calibrateButton = calibrateButton;
        addRequirements(this.gyroSubsystem);
    }
    @Override
    public void execute() {
        if (resetButton.getAsBoolean()) {
            gyroSubsystem.gyroReset();
        }
        else if (resetButton.getAsBoolean() && calibrateButton.getAsBoolean()) {
            gyroSubsystem.gyroCalibration();
            gyroSubsystem.gyroReset();
        }
    }
}
