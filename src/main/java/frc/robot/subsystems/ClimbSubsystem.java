package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class ClimbSubsystem extends SubsystemBase {
    private final VictorSPX motor;

    public ClimbSubsystem() {
        motor = new VictorSPX(12);
    }

    public void runMotor(double Speed) {
        motor.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, Speed);
    }
    
}