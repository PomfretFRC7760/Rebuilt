package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

public class FuelJettison extends Command{
    private final LiftIntakeRollerSubsystem intake;
    public FuelJettison(LiftIntakeRollerSubsystem intake) {
        this.intake = intake;
        addRequirements(intake);
    }
    @Override
    public void initialize() {
        intake.autoRunRollerJettison();
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
