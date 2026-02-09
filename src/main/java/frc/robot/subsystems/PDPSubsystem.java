package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PDPSubsystem extends SubsystemBase {

    private final PowerDistribution pdp;

    public PDPSubsystem() {
        pdp = new PowerDistribution(0, ModuleType.kCTRE);
        SmartDashboard.putData("PDP", pdp);
    }

    @Override
    public void periodic() {
        // Publish bus metrics
        
    }
}
