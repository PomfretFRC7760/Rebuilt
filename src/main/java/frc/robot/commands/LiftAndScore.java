package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.LiftSubsystem;
import frc.robot.subsystems.LiftRotationSubsystem;

public class LiftAndScore extends SequentialCommandGroup {
    private final LiftSubsystem lift;
    private final LiftRotationSubsystem liftRotationSubsystem;
    private final int liftLevel;

    public LiftAndScore(LiftSubsystem lift, LiftRotationSubsystem liftRotationSubsystem, int liftLevel) {
        this.lift = lift;
        this.liftLevel = liftLevel;
        this.liftRotationSubsystem = liftRotationSubsystem;

        // Build the sequence when the command is constructed
        addCommands(
            createLiftCommand(liftLevel),
            createLiftRotationCommand(liftLevel)
        );

        addRequirements(lift, liftRotationSubsystem);
    }

    private Command createLiftCommand(int level) {
        switch (level) {
            case 1:
                return new LiftAutoPosition(lift, 0);
            case 2:
                return new LiftAutoPosition(lift, 0);
            case 3:
                return new LiftAutoPosition(lift, 19.5);
            case 4:
                return new LiftAutoPosition(lift, 53);
            case 5:
                return new LiftAutoPosition(lift, 53);
            case 6: 
                return new LiftAutoPosition(lift, 0);
            case 7:
                return new LiftAutoPosition(lift, 53);
            case 8:
                return new LiftAutoPosition(lift, 53);
            case 9:
                return new LiftAutoPosition(lift, 53);
            default:
                return new LiftAutoPosition(lift,0);
        }
    }

    private Command createLiftRotationCommand(int level) {
        switch (level) {
            case 1:
                return new CoralAutoPosition(liftRotationSubsystem, 0); //stow
            case 2:
                return new CoralAutoPosition(liftRotationSubsystem, 22); //L1
            case 3:
                return new CoralAutoPosition(liftRotationSubsystem, 21); //L2
            case 4:
                return new CoralAutoPosition(liftRotationSubsystem, 26); //L3
            case 5:
                return new CoralAutoPosition(liftRotationSubsystem, 50); //L4
            case 6:
                return new CoralAutoPosition(liftRotationSubsystem, 37.5); //human player
            case 7:
                return new CoralAutoPosition(liftRotationSubsystem, 26); //Algae L2
            case 8:
                return new CoralAutoPosition(liftRotationSubsystem, 26); //Algae L3
            case 9:
                return new CoralAutoPosition(liftRotationSubsystem, 50); //barge
            default:
                return new CoralAutoPosition(liftRotationSubsystem, 0); //default stow
        } 
    }
}
