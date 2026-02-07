package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.HttpCamera;

public class CameraSubsystem extends SubsystemBase {

    private final UsbCamera usbCamera;
    private final HttpCamera limelightCamera;

    public CameraSubsystem() {
        // USB camera (port 0)
        usbCamera = CameraServer.startAutomaticCapture("USB Camera", 0);
        usbCamera.setResolution(320, 240);
        usbCamera.setFPS(30);

        // Limelight MJPEG stream
        limelightCamera = new HttpCamera(
                "Limelight",
                "http://10.77.60.200:5800/"
        );

        CameraServer.addCamera(limelightCamera);
        CameraServer.startAutomaticCapture(limelightCamera);
    }
}
