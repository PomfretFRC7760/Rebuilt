package frc.robot.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * 2026 REBUILT fuel collection zone references. Use for auto paths and location choosers.
 * Capacities: {@link FuelConstants#DEPOT_FUEL_COUNT}, {@link FuelConstants#OUTPOST_CHUTE_FUEL_COUNT},
 * {@link FuelConstants#NEUTRAL_ZONE_FUEL_MIN}, {@link FuelConstants#NEUTRAL_ZONE_FUEL_MAX}.
 */
public final class FuelCollectionLocations {

    private FuelCollectionLocations() {}

    /** Field center (neutral zone). Optimal collection: 360–408 FUEL. */
    private static final double FIELD_LENGTH_M = 16.518;
    private static final double FIELD_WIDTH_M = 8.043;
    public static final Translation2d NEUTRAL_ZONE_CENTER = new Translation2d(FIELD_LENGTH_M / 2.0, FIELD_WIDTH_M / 2.0);

    /** Pose at neutral zone center, facing alliance HUB. */
    public static Pose2d getNeutralZoneCenterPose() {
        boolean isBlue = DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Blue;
        Rotation2d rot = isBlue ? Rotation2d.k180deg : Rotation2d.kZero;
        return new Pose2d(NEUTRAL_ZONE_CENTER, rot);
    }

    /** Capacity for optimal path ordering: NEUTRAL_ZONE > DEPOT > OUTPOST_CHUTE. */
    public static int getCapacityPriority(String zone) {
        switch (zone.toUpperCase()) {
            case "NEUTRAL_ZONE":
                return FuelConstants.NEUTRAL_ZONE_FUEL_MAX;
            case "DEPOT":
                return FuelConstants.DEPOT_FUEL_COUNT;
            case "OUTPOST":
                return FuelConstants.OUTPOST_CHUTE_FUEL_COUNT;
            default:
                return 0;
        }
    }
}
