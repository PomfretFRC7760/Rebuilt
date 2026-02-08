package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.*;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkLowLevel.MotorType;

public class ClimbSubsystem extends SubsystemBase {
    private final SparkMax motor = new SparkMax(8, MotorType.kBrushless);
    private SparkClosedLoopController controller = motor.getClosedLoopController();

    private RelativeEncoder encoder = motor.getEncoder();
    private SparkMaxConfig config = new SparkMaxConfig();
    private double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, targetRPM;

    public ClimbSubsystem() {
        kP = 0.1; 
        kI = 0;
        kD = 0; 
        kIz = 0; 
        kFF = 0; 
        kMaxOutput = 1; 
        kMinOutput = -1;
        maxRPM = 5700;
        config.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        .p(kP)
        .i(kI)
        .d(kD)
        .velocityFF(1.0 / maxRPM)
        .outputRange(kMinOutput, kMaxOutput);
    }
    public void setPosition(double position){
        controller.setSetpoint(position,SparkFlex.ControlType.kMAXMotionPositionControl);
    }
}