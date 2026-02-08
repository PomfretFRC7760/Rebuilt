package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ClimbSubsystem;

import java.util.function.BooleanSupplier;

public class ClimbCommand extends Command {

    private final ClimbSubsystem climbSubsystem;
    private final BooleanSupplier armClimb;
    private final BooleanSupplier climbButton;

    private boolean climbArmedState = false;
    private boolean autoClimbed = false;

    public ClimbCommand(ClimbSubsystem climbSubsystem, BooleanSupplier armClimb, BooleanSupplier climbButton) {
        this.climbSubsystem = climbSubsystem;
        this.armClimb = armClimb;
        this.climbButton = climbButton;
        addRequirements(climbSubsystem);
    }

    @Override
    public void execute() {
        SmartDashboard.putBoolean("Climb Armed State", climbArmedState);
        if (DriverStation.isTeleop()) {
            if (!climbArmedState) {
                if (armClimb.getAsBoolean()) {
                    climbSubsystem.setPosition(20); //placeholder pre climb position
                    climbArmedState = true;
                    return;
                }
            } else if (climbArmedState && !autoClimbed) {
                if (armClimb.getAsBoolean()) {
                    climbSubsystem.setPosition(0); //vertical position
                    climbArmedState = false;
                    return;
                }
                else if (climbButton.getAsBoolean()) {
                    climbSubsystem.setPosition(-20); //placeholder climb position
                    return;
                }
            } else if (climbArmedState && autoClimbed) {
                climbSubsystem.setPosition(20); //placeholder pre climb position
                autoClimbed = false;
                return;
            }
        }
    }

    public void autoPhaseArmClimb() {
        climbSubsystem.setPosition(20);//placeholder pre climb position
        climbArmedState = true;
    }

    public void autoPhaseClimb() {
        climbSubsystem.setPosition(-10); //placeholder auto climb position
        autoClimbed = true;
    }
}
