package frc.robot.util;

/**
 * Global FUEL definition for 2026 REBUILT (Game Manual 6.5).
 *
 * <p>Unifies former Coral and Algae (2025) into single FUEL game element. All fuel handling
 * (intake, jettison, positioning, location) uses this definition for consistency.
 *
 * <p>Game rules: FUEL scored in active HUB = 1 pt; inactive HUB = no points. Intake allowed
 * anytime (DEPOT, OUTPOST, NEUTRAL ZONE). Robot may control any number of FUEL.
 */
public final class FuelConstants {

    private FuelConstants() {}

    /** 2026 game element name. Use for logging, dashboard, or cross-reference. */
    public static final String GAME_ELEMENT_NAME = "FUEL";

    /** Game Manual 6.5.1: FUEL is scored when it passes through top opening and sensor array. */
    public static final String RULE_SCORING_CRITERIA = "FUEL scored in HUB when through top opening and sensor array";

    /**
     * No maximum FUEL limit: robot may control any number of FUEL. Intake/jettison logic must
     * not impose artificial caps (rollers run continuously; no count-based limiting).
     */
    public static final boolean RULE_NO_FUEL_MAXIMUM = true;

    /** Named command for jettisoning fuel (PathPlanner autos, etc.). */
    public static final String NAMED_COMMAND_JETTISON = "Jettison Fuel";

    /** Named commands for fuel scoring positions (HUB). Must match NamedCommands.registerCommand. */
    public static final String NAMED_COMMAND_FUEL_L2 = "Fuel L2";
    public static final String NAMED_COMMAND_FUEL_L3 = "Fuel L3";

    /** Display labels for fuel lift/pickup choosers (unified FUEL, no coral/algae). */
    public static final String LABEL_FUEL = "FUEL";
    public static final String LABEL_FUEL_L1 = "FUEL L1";
    public static final String LABEL_FUEL_L2 = "FUEL L2";
    public static final String LABEL_FUEL_L3 = "FUEL L3";

    // ─── 2026 FUEL COLLECTION LOCATION CAPACITIES (reference only, Game Manual) ───

    /** DEPOT: 24 FUEL. Enclosed area connected to alliance wall. */
    public static final int DEPOT_FUEL_COUNT = 24;

    /** OUTPOST CHUTE: 24 FUEL. Human player handoff area. */
    public static final int OUTPOST_CHUTE_FUEL_COUNT = 24;

    /**
     * NEUTRAL ZONE: minimum FUEL. Varies with preload; FRC Q&A cites ~360 ±24 (336–384).
     * Use 360 as typical min for path/auto reference.
     */
    public static final int NEUTRAL_ZONE_FUEL_MIN = 360;

    /** NEUTRAL ZONE: maximum FUEL (preload-dependent). Use for path/auto reference. */
    public static final int NEUTRAL_ZONE_FUEL_MAX = 408;
}
