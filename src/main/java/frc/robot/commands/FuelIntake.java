package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

public class FuelIntake extends Command{
    private final LiftIntakeRollerSubsystem intake;
    public FuelIntake(LiftIntakeRollerSubsystem intake) {
        this.intake = intake;
        addRequirements(intake);
    }
    @Override
    public void initialize() {
        intake.autoRunRollerIntake();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
