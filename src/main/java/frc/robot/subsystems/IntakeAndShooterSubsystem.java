package frc.robot.subsystems;

import com.revrobotics.spark.*;
import com.revrobotics.spark.config.SparkFlexConfig;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class IntakeAndShooterSubsystem extends SubsystemBase {
    private final SparkFlex leftShooter  = new SparkFlex(5, MotorType.kBrushless);
    private final SparkFlex rightShooter = new SparkFlex(6, MotorType.kBrushless);
    private final SparkMax indexer = new SparkMax(7, MotorType.kBrushless);

    private final SparkClosedLoopController leftController;
    private final SparkClosedLoopController rightController;
    public IntakeAndShooterSubsystem() {
        leftController = leftShooter.getClosedLoopController();
        rightController = rightShooter.getClosedLoopController();
        leftShooter.setInverted(false);
        rightShooter.setInverted(true);

        leftShooter.getEncoder().setPosition(0);
        rightShooter.getEncoder().setPosition(0);

        SparkFlexConfig configMotor = new SparkFlexConfig();

        configMotor.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(6e-5)
                .d(0.00225)
                .velocityFF(0.00179)
                .outputRange(-1, 1);
        // Apply the global config and set the Shooter SPARK for follower mode
        leftShooter.configure(configMotor, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        rightShooter.configure(configMotor, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
    }
    public void spoolUpWheels(double speed){
        leftController.setSetpoint(speed,SparkFlex.ControlType.kVelocity);
        rightController.setSetpoint(speed,SparkFlex.ControlType.kVelocity);
    }
    public void shoot() {
        indexer.set(100);
    }
    public void stop(){
        indexer.set(0);
        leftController.setSetpoint(0,SparkFlex.ControlType.kVelocity);
        rightController.setSetpoint(0,SparkFlex.ControlType.kVelocity);
    }
    public void intake(){
        indexer.set(-100);
        leftController.setSetpoint(-1500,SparkFlex.ControlType.kVelocity);
        rightController.setSetpoint(-1500,SparkFlex.ControlType.kVelocity);
    }
    public void eject(){
        indexer.set(-100);
        leftController.setSetpoint(2000,SparkFlex.ControlType.kVelocity);
        rightController.setSetpoint(2000,SparkFlex.ControlType.kVelocity);
    }
}