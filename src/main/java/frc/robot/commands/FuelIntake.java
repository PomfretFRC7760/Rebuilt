package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeAndShooterSubsystem;

public class FuelIntake extends Command {
    private final IntakeAndShooterSubsystem intakeSubsystem;
    private final BooleanSupplier intakeButton;
    private final BooleanSupplier shootButton;

    public FuelIntake(IntakeAndShooterSubsystem intakeSubsystem, BooleanSupplier intakeButton, BooleanSupplier shootButton) {
        this.intakeSubsystem = intakeSubsystem;
        this.intakeButton = intakeButton;
        this.shootButton = shootButton;
        addRequirements(intakeSubsystem);
    }

    @Override
    public void initialize() {
        if (intakeButton.getAsBoolean() && !shootButton.getAsBoolean()) {
            intakeSubsystem.intake();
        }
    }
}
