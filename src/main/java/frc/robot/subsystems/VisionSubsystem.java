package frc.robot.subsystems;

import java.util.Optional;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;
import frc.robot.util.FuelLocator;

public class VisionSubsystem extends SubsystemBase {

    private final LimeLocalizationSubsystem limeF;
    private final FuelLocator fuelLocator;

    // Constructor that takes a CANDriveSubsystem instance
    public VisionSubsystem(CANDriveSubsystem driveSubsystem, FuelLocator fuelLocator) {
        this.limeF = new LimeLocalizationSubsystem("", driveSubsystem);
        this.fuelLocator = fuelLocator;
    }

    public Pose2d limeFpose() {
        Optional<Pose2d> limePose = limeF.getPose();
        return limePose.orElse(null);
    }

    public void setPipeline0(){
        LimelightHelpers.setPipelineIndex("",0);
    }

    public void setPipeline1(){
        LimelightHelpers.setPipelineIndex("",1);
    }

    public void setPipeline2(){
        LimelightHelpers.setPipelineIndex("",2);
    }

    public Pose2d getFuelPose() {
        return fuelLocator.locateFuel();
    }

    public boolean validateTarget(){
        return fuelLocator.validateTarget();
    }
}
