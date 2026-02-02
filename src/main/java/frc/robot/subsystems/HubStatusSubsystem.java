package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Tracks current HUB activation status (Active/Inactive) for 2026 REBUILT.
 *
 * <p>Match intervals (per FMS / game manual):
 * <ul>
 *   <li>AUTO (20s): Both HUBS active.
 *   <li>TRANSITION SHIFT (first 10s of teleop): Both HUBS active.
 *   <li>SHIFT 1–4 (4 × 25s): Alternate activation from AUTO results. If our alliance won AUTO, our
 *       HUB is inactive for Shifts 1 & 3 and active for 2 & 4; otherwise active for 1 & 3,
 *       inactive for 2 & 4.
 *   <li>END GAME (last 30s of teleop): Both HUBS active.
 * </ul>
 *
 * <p>AUTO winner is read from FMS game-specific message (first char 'R' = Red, 'B' = Blue). If
 * unknown, we assume we did not win (active in 1 & 3).
 */
public class HubStatusSubsystem extends SubsystemBase {

    public enum HubActivationStatus {
        ACTIVE,
        INACTIVE
    }

    private static final double TRANSITION_DURATION_S = 10.0;
    private static final double SHIFT_DURATION_S = 25.0;
    private static final double TELEOP_TOTAL_S = 140.0;

    private HubActivationStatus hubStatus = HubActivationStatus.INACTIVE;

    public HubStatusSubsystem() {}

    @Override
    public void periodic() {
        hubStatus = computeHubStatus();
        if (DriverStation.isTeleop() && hubStatus == HubActivationStatus.INACTIVE) {
            SmartDashboard.putString("HUB Status", "INACTIVE — scoring disabled (fuel would not count)");
        } else {
            SmartDashboard.putString("HUB Status", hubStatus == HubActivationStatus.ACTIVE ? "ACTIVE" : "—");
        }
    }

    public boolean isHubActive() {
        return hubStatus == HubActivationStatus.ACTIVE;
    }

    public HubActivationStatus getHubStatus() {
        return hubStatus;
    }

    /**
     * Computes current HUB status from match phase and FMS game data.
     * AUTO / Transition / End game: both active. Shifts 1–4: alternate by AUTO winner.
     */
    private HubActivationStatus computeHubStatus() {
        if (DriverStation.isAutonomous()) {
            return HubActivationStatus.ACTIVE;
        }
        if (!DriverStation.isTeleop()) {
            return HubActivationStatus.INACTIVE;
        }

        double remaining = DriverStation.getMatchTime();
        double elapsed = TELEOP_TOTAL_S - remaining;
        elapsed = Math.max(0, Math.min(TELEOP_TOTAL_S, elapsed));

        if (elapsed < TRANSITION_DURATION_S) {
            return HubActivationStatus.ACTIVE;
        }
        double afterTransition = elapsed - TRANSITION_DURATION_S;
        if (afterTransition >= 4 * SHIFT_DURATION_S) {
            return HubActivationStatus.ACTIVE;
        }

        int shift = (int) (afterTransition / SHIFT_DURATION_S) + 1;
        boolean weWonAuto = weWonAutonomous();
        boolean activeInShift = (shift == 1 || shift == 3) ? !weWonAuto : weWonAuto;
        return activeInShift ? HubActivationStatus.ACTIVE : HubActivationStatus.INACTIVE;
    }

    /** True if our alliance won autonomous (from FMS game-specific message). */
    private boolean weWonAutonomous() {
        String msg = DriverStation.getGameSpecificMessage();
        if (msg == null || msg.isEmpty()) {
            return false;
        }
        char first = Character.toUpperCase(msg.charAt(0));
        Alliance us = DriverStation.getAlliance().orElse(Alliance.Blue);
        if (first == 'R') {
            return us == Alliance.Red;
        }
        if (first == 'B') {
            return us == Alliance.Blue;
        }
        return false;
    }
}