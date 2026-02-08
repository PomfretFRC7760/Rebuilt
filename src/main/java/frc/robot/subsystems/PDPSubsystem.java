package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PDPSubsystem extends SubsystemBase {

    private final PowerDistribution pdp;

    public PDPSubsystem() {
        // CTRE PDP, CAN ID 0
        pdp = new PowerDistribution(0, ModuleType.kCTRE);
    }

    @Override
    public void periodic() {
        // Bus voltage
        SmartDashboard.putNumber("PDP Voltage", pdp.getVoltage());

        // Total current
        SmartDashboard.putNumber("PDP Total Current", pdp.getTotalCurrent());

        // Total power
        SmartDashboard.putNumber("PDP Total Power", pdp.getTotalPower());

        // Temperature
        SmartDashboard.putNumber("PDP Temperature", pdp.getTemperature());

        // Individual channel currents (0–15)
        for (int channel = 0; channel < 9; channel++) {
            SmartDashboard.putNumber(
                "PDP Channel " + channel + " Current",
                pdp.getCurrent(channel)
            );
        }
    }
}
