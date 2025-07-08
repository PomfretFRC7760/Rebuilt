package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

public class CoralJettison extends Command{
    private final LiftIntakeRollerSubsystem intake;
    public CoralJettison(LiftIntakeRollerSubsystem intake) {
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
