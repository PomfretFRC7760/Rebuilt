package frc.robot.subsystems;

import com.revrobotics.spark.*;
import com.revrobotics.spark.config.SparkFlexConfig;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.controllers.PPLTVController;
import com.pathplanner.lib.config.RobotConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;

public class DriveSubsystem extends SubsystemBase {

    // Motors
    private final SparkFlex leftLeader  = new SparkFlex(1, MotorType.kBrushless);
    private final SparkFlex leftFollower = new SparkFlex(2, MotorType.kBrushless);
    private final SparkFlex rightLeader = new SparkFlex(3, MotorType.kBrushless);
    private final SparkFlex rightFollower = new SparkFlex(4, MotorType.kBrushless); 

    

    private final SparkClosedLoopController leftController;
    private final SparkClosedLoopController rightController;

    private final DifferentialDrive differentialDrive;
    private final GyroSubsystem gyroSubsystem;

    private static final double TRACK_WIDTH = 0.6;
    private static final double WHEEL_DIAMETER = 0.1524;
    private static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
    private static final double GEAR_RATIO = 8.46;

    private final DifferentialDriveKinematics kinematics =
            new DifferentialDriveKinematics(TRACK_WIDTH);

    private final DifferentialDriveOdometry odometry;
    private Pose2d robotPose = new Pose2d();

    private RobotConfig config;

    public DriveSubsystem(GyroSubsystem gyroSubsystem) {
        this.gyroSubsystem = gyroSubsystem;


        // Invert ONE side
        rightLeader.setInverted(true);
        leftLeader.setInverted(false);

        differentialDrive = new DifferentialDrive(leftLeader, rightLeader);
        differentialDrive.setSafetyEnabled(false);

        leftLeader.getEncoder().setPosition(0);
        rightLeader.getEncoder().setPosition(0);

        leftController = leftLeader.getClosedLoopController();
        rightController = rightLeader.getClosedLoopController();

        SparkFlexConfig configMotor = new SparkFlexConfig();

        configMotor.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(6e-5)
                .d(0.00225)
                .velocityFF(0.00179)
                .outputRange(-1, 1);
        SparkFlexConfig leftFollowerConfig = new SparkFlexConfig();
        SparkFlexConfig rightFollowerConfig = new SparkFlexConfig();
        // Apply the global config and set the leader SPARK for follower mode
        leftFollowerConfig
            .apply(configMotor)
            .follow(leftLeader);

    // Apply the global config and set the leader SPARK for follower mode
        rightFollowerConfig
            .apply(configMotor)
            .follow(rightLeader);

        leftLeader.configure(configMotor, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        rightLeader.configure(configMotor, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        leftFollower.configure(leftFollowerConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);
        rightFollower.configure(rightFollowerConfig, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters);

        odometry = new DifferentialDriveOdometry(
                Rotation2d.fromDegrees(gyroSubsystem.getGyroAngle()),
                getLeftDistanceMeters(),
                getRightDistanceMeters()
        );

        setupPathPlanner();
    }

    private void setupPathPlanner() {
        try {
            config = RobotConfig.fromGUISettings();

            AutoBuilder.configure(
                    this::getPose,
                    this::resetPose,
                    this::getRobotRelativeSpeeds,
                    this::driveRobotRelative,
                    new PPLTVController(0.02),
                    config,
                    () -> DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue)
                            == DriverStation.Alliance.Red,
                    this
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getLeftDistanceMeters() {
        return (leftLeader.getEncoder().getPosition() / GEAR_RATIO) * WHEEL_CIRCUMFERENCE;
    }

    private double getRightDistanceMeters() {
        return (rightLeader.getEncoder().getPosition() / GEAR_RATIO) * WHEEL_CIRCUMFERENCE;
    }

    public void updateOdometry() {
        robotPose = odometry.update(
                Rotation2d.fromDegrees(gyroSubsystem.getGyroAngle()),
                new DifferentialDriveWheelPositions(
                        getLeftDistanceMeters(),
                        getRightDistanceMeters()
                )
        );
    }

    @Override
    public void periodic() {
        updateOdometry();
        SmartDashboard.putString("Pose", robotPose.toString());
    }

    public ChassisSpeeds getRobotRelativeSpeeds() {
        double leftMps =
                (leftLeader.getEncoder().getVelocity() / 60.0 / GEAR_RATIO) * WHEEL_CIRCUMFERENCE;
        double rightMps =
                (rightLeader.getEncoder().getVelocity() / 60.0 / GEAR_RATIO) * WHEEL_CIRCUMFERENCE;

        return kinematics.toChassisSpeeds(
                new DifferentialDriveWheelSpeeds(leftMps, rightMps)
        );
    }

    public void driveRobotRelative(ChassisSpeeds speeds) {
        DifferentialDriveWheelSpeeds ws = kinematics.toWheelSpeeds(speeds);

        leftController.setSetpoint(
                ws.leftMetersPerSecond / WHEEL_CIRCUMFERENCE * 60 * GEAR_RATIO,
                SparkFlex.ControlType.kMAXMotionVelocityControl
        );

        rightController.setSetpoint(
                ws.rightMetersPerSecond / WHEEL_CIRCUMFERENCE * 60 * GEAR_RATIO,
                SparkFlex.ControlType.kMAXMotionVelocityControl
        );
    }

    public void driveRobot(double speed, double rotation) {
        differentialDrive.arcadeDrive(speed, rotation);
    }

    public Pose2d getPose() {
        return robotPose;
    }

    public void resetPose(Pose2d pose) {
        gyroSubsystem.setGyroAngle(pose.getRotation().getDegrees());
        odometry.resetPosition(
                Rotation2d.fromDegrees(gyroSubsystem.getGyroAngle()),
                new DifferentialDriveWheelPositions(0, 0),
                pose
        );
        robotPose = pose;
    }

    public double getGyroAngle() {
        return gyroSubsystem.getGyroAngle();
    }

    public double getRotationSpeed() {
        return gyroSubsystem.getRotationSpeed();
    }
    public void resetGyro(){
        gyroSubsystem.gyroReset();
        robotPose = odometry.update(
    Rotation2d.fromDegrees(gyroSubsystem.getGyroAngle()),
    getLeftDistanceMeters(),
    getRightDistanceMeters()
);

    }

}
