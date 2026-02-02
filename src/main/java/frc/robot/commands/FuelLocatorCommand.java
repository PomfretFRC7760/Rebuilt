package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.VisionSubsystem;

/**
 * Locates FUEL in the field via vision for pathfinding. Used with drive-to-fuel and auto sequences.
 * Per 2026 Game Manual, FUEL can be collected from DEPOT, OUTPOST, or NEUTRAL ZONE.
 */
public class FuelLocatorCommand extends Command {
    private final VisionSubsystem visionSubsystem;
    private Pose2d fuelPose;
    public FuelLocatorCommand(VisionSubsystem visionSubsystem) {
        this.visionSubsystem = visionSubsystem;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        fuelPose = visionSubsystem.getFuelPose();
        
    }

    public Pose2d getFuelPose(){
        Pose2d pose = visionSubsystem.getFuelPose();
        return pose;
    }

    public boolean validateTarget(){
        return visionSubsystem.validateTarget();
    }

    public void setPipeline1(){
        visionSubsystem.setPipeline1();
    }
    
}
