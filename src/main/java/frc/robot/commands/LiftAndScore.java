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

        addCommands(
            createLiftCommand(liftLevel),
            createLiftRotationCommand(liftLevel)
        );

        addRequirements(lift, liftRotationSubsystem);
    }

    private Command createLiftCommand(int level) {
        switch (level) {
            case 1:
                return new LiftAutoPosition(lift, 0); // Stow - retract mechanism
            case 2:
                return new LiftAutoPosition(lift, 27.0); // TOWER LEVEL 1 - AUTO only (27 in)
            case 3:
                return new LiftAutoPosition(lift, 45.0); // TOWER LEVEL 2 (45 in)
            case 4:
                return new LiftAutoPosition(lift, 63.0); // TOWER LEVEL 3 (63 in)
            case 6:
                return new LiftAutoPosition(lift, 0); // Human Player pickup
            case 7:
                return new LiftAutoPosition(lift, 53); // HUB scoring preset
            case 8:
                return new LiftAutoPosition(lift, 53); // HUB scoring preset
            case 9:
                return new LiftAutoPosition(lift, 53); // OUTPOST intake (was Barge)
            default:
                return new LiftAutoPosition(lift, 0); // Fallback to stow
        }
    }

    private Command createLiftRotationCommand(int level) {
        switch (level) {
            case 1:
                return new FuelAutoPosition(liftRotationSubsystem, 0); // Stow
            case 2:
                return new FuelAutoPosition(liftRotationSubsystem, 22); // TOWER L1
            case 3:
                return new FuelAutoPosition(liftRotationSubsystem, 21); // TOWER L2
            case 4:
                return new FuelAutoPosition(liftRotationSubsystem, 26); // TOWER L3
            case 6:
                return new FuelAutoPosition(liftRotationSubsystem, 37.5); // Human Player
            case 7:
                return new FuelAutoPosition(liftRotationSubsystem, 26); // HUB scoring
            case 8:
                return new FuelAutoPosition(liftRotationSubsystem, 26); // HUB scoring
            case 9:
                return new FuelAutoPosition(liftRotationSubsystem, 50); // OUTPOST intake
            default:
                return new FuelAutoPosition(liftRotationSubsystem, 0); // Fallback to stow
        }
    }
}
