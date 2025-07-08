package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftIntakeRollerSubsystem;

public class CoralIntake extends Command{
    private final LiftIntakeRollerSubsystem intake;
    public CoralIntake(LiftIntakeRollerSubsystem intake) {
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