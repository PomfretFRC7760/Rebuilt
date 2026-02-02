package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.FloorIntakeRotationSubsystem;

import java.util.function.BooleanSupplier;

/**
 * Climb command with 2026 REBUILT rule enforcement:
 * - LEVEL 1: AUTO only (points awarded only during autonomous)
 * - TELEOP: Only one level can be earned per robot
 * - TOWER contact: Robot must contact RUNGS or UPRIGHTS; may only additionally contact
 *   TOWER WALL, support structure, FUEL, or another ROBOT (Game Manual 6.5.2)
 */
public class ClimbCommand extends Command {

    /** TOWER contact rules per 2026 Game Manual Section 6.5.2. */
    private static final String TOWER_CONTACT_RULES =
            "TOWER contact: RUNGS, UPRIGHTS, TOWER WALL, support structure, FUEL, other ROBOT only";

    private final ClimbSubsystem climbSubsystem;
    private final FloorIntakeRotationSubsystem floorIntakeRotationSubsystem;
    private final BooleanSupplier leftBumper;
    private final BooleanSupplier rightBumper;

    public boolean armClimb = false;

    /** True once climb has been used during this TELEOP; reset when disabled. */
    private boolean hasClimbedThisTeleop = false;

    public ClimbCommand(ClimbSubsystem climbSubsystem, FloorIntakeRotationSubsystem floorIntakeRotationSubsystem, BooleanSupplier leftBumper, BooleanSupplier rightBumper) {
        this.climbSubsystem = climbSubsystem;
        this.floorIntakeRotationSubsystem = floorIntakeRotationSubsystem;
        this.leftBumper = leftBumper;
        this.rightBumper = rightBumper;
        addRequirements(climbSubsystem, floorIntakeRotationSubsystem);
    }

    @Override
    public void initialize() {
        SmartDashboard.putData("Arm climb (cannot be disarmed after arming!)", new InstantCommand(() -> armClimb()));
    }

    @Override
    public void execute() {
        if (DriverStation.isDisabled()) {
            hasClimbedThisTeleop = false;
        }

        if (armClimb) {
            SmartDashboard.putString("TOWER Contact Rules", TOWER_CONTACT_RULES);

            if (floorIntakeRotationSubsystem.getPosition() > -37.5 && floorIntakeRotationSubsystem.getPosition() < -35.5) {
                floorIntakeRotationSubsystem.autoPosition(-36.5);
                return;
            }

            if (DriverStation.isTeleop() && hasClimbedThisTeleop) {
                climbSubsystem.runMotor(0);
                floorIntakeRotationSubsystem.stallMotor();
                SmartDashboard.putString("Climb Status", "One level per TELEOP — climb locked");
                return;
            }

            if (leftBumper.getAsBoolean()) {
                floorIntakeRotationSubsystem.manualControl(0);
                floorIntakeRotationSubsystem.setCoast();
                climbSubsystem.runMotor(-0.75);
                if (DriverStation.isTeleop()) {
                    hasClimbedThisTeleop = true;
                }
                return;
            } else if (rightBumper.getAsBoolean()) {
                floorIntakeRotationSubsystem.manualControl(0);
                floorIntakeRotationSubsystem.setCoast();
                climbSubsystem.runMotor(0.75);
                if (DriverStation.isTeleop()) {
                    hasClimbedThisTeleop = true;
                }
                return;
            } else {
                climbSubsystem.runMotor(0);
                floorIntakeRotationSubsystem.stallMotor();
                if (DriverStation.isTeleop()) {
                    SmartDashboard.putString("Climb Status", hasClimbedThisTeleop ? "One level per TELEOP — used" : "Ready");
                } else if (DriverStation.isAutonomous()) {
                    SmartDashboard.putString("Climb Status", "AUTO — LEVEL 1 only");
                }
            }

            if (DriverStation.isDisabled()) {
                floorIntakeRotationSubsystem.setBrake();
            }
        }
    }

    public void armClimb() {
        armClimb = true;
    }
}
