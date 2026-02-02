// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.config.SparkMaxConfig;


/**
 * Floor roller for FUEL intake and eject. Per 2026 rules, robot may control any number of
 * FUEL—no maximum. Rollers run continuously (no count cap).
 */
public class FloorIntakeRollerSubsystem extends SubsystemBase {
  private final SparkMax motor;

  private SparkClosedLoopController controller;
  private SparkMaxConfig config;
  private RelativeEncoder encoder;

  private double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM, targetRPM;

  public FloorIntakeRollerSubsystem() {
    // Set up the roller motor as a brushed motor
    motor = new SparkMax(8, MotorType.kBrushless);

    controller = motor.getClosedLoopController();
        encoder = motor.getEncoder();
        config = new SparkMaxConfig();

        kP = 0.5; 
        kI = 0;
        kD = 0; 
        kIz = 0; 
        kFF = 0; 
        kMaxOutput = 1;
        kMinOutput = -1;
        maxRPM = 5700;

        config.encoder
        .positionConversionFactor(1)
        .velocityConversionFactor(1);

        config.closedLoop
        .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
        // Set PID values for position control. We don't need to pass a closed loop
        // slot, as it will default to slot 0.
        .p(kP)
        .i(kI)
        .d(kD)
        .velocityFF(1.0 / maxRPM)
        .outputRange(kMinOutput, kMaxOutput);

        motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
        encoder.setPosition(0);
  }

  /** This is a method that makes the roller spin */
  public void runRollerIntake() {
    motor.set(-1);
  }
  public void stallRoller() {
    //locks the motor at it's current position
    controller.setReference(encoder.getPosition(), ControlType.kPosition, ClosedLoopSlot.kSlot0);
  }
  public void runRollerJettison() {
    motor.set(1);
  }
  public void stopRoller() {
    motor.set(0);
  }
  public void manualControlJettison(double rightTrigger) {
    motor.set(rightTrigger);
  }
  public void manualControlIntake(double leftTrigger) {
    motor.set(leftTrigger);
}
}
