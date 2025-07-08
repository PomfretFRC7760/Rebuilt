package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.LiftRotationSubsystem;

public class CoralAutoPosition extends Command{
    private final LiftRotationSubsystem intake;
    private double targetPosition;
    public CoralAutoPosition(LiftRotationSubsystem intake, double targetPosition) {
        this.intake = intake;
        this.targetPosition = targetPosition;
        addRequirements(intake);
    }
    @Override
    public void initialize() {
        intake.autoPosition(targetPosition);
        System.out.println("autoPosition");
    }
    @Override
    public boolean isFinished() {
        return true;
    }
}
