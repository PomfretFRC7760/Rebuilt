package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftSubsystem;

public class LiftAutoPosition extends Command{
    private final LiftSubsystem lift;
    private double targetPosition;
    public LiftAutoPosition(LiftSubsystem lift, double targetPosition) {
        this.lift = lift;
        this.targetPosition = targetPosition;
        addRequirements(lift);
    }
    @Override
    public void initialize() {
        lift.setLiftPosition(targetPosition);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

}
