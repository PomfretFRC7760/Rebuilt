package frc.robot.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.IntakeAndShooterSubsystem;

public class FuelIntake extends Command {
    private final IntakeAndShooterSubsystem intakeSubsystem;
    private final BooleanSupplier intakeButton;
    private final DoubleSupplier shootTrigger;

    public FuelIntake(IntakeAndShooterSubsystem intakeSubsystem, BooleanSupplier intakeButton, DoubleSupplier shootTrigger) {
        this.intakeSubsystem = intakeSubsystem;
        this.intakeButton = intakeButton;
        this.shootTrigger = shootTrigger;
        addRequirements(intakeSubsystem);
    }

    @Override
    public void execute() {
        if (intakeButton.getAsBoolean() && shootTrigger.getAsDouble() < 0.75) {
            intakeSubsystem.intake();
        }
    }
}
