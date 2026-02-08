package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.GyroSubsystem;

public class GyroCommand extends Command {
    private final GyroSubsystem gyroSubsystem;
    private final BooleanSupplier resetButton;

    public GyroCommand(GyroSubsystem gyroSubsystem, BooleanSupplier resetButton) {
        this.gyroSubsystem = gyroSubsystem;
        this.resetButton = resetButton;
        addRequirements(this.gyroSubsystem);
    }
    @Override
    public void execute() {
        if (resetButton.getAsBoolean()) {
            gyroSubsystem.gyroReset();
        }
    }
}
