# 2026 REBUILT Programming Workbook
## Project Migration: From 2025 Reefscape to 2026 REBUILT

---

## Overall Overview

**Current Status**: Codebase is written for the 2025 game (Reefscape) and needs complete migration to the 2026 game (REBUILT)

**Key Differences**:=
- 2025: Coral, Algae, Reef
- 2026: FUEL, HUB, TOWER

---

## Priority 1: Critical Terminology and Naming Updates

### 1.1 Global Renaming
- [ ] **Rename all "Coral" → "Fuel"**
  - `CoralIntake.java` → `FuelIntake.java`
  - `CoralJettison.java` → `FuelJettison.java`
  - `CoralAutoPosition.java` → `FuelAutoPosition.java`
  - All variable names: `coral` → `fuel`
  - All comments and documentation strings

- [ ] **Rename all "Algae" → "Fuel"**
  - `AlgaeLocator.java` → `FuelLocator.java`
  - `AlgaeLocatorCommand.java` → `FuelLocatorCommand.java`
  - All variable names: `algae` → `fuel`
  - Note: Both Algae and Coral are FUEL in 2026, need to unify

- [ ] **Update location system terminology**
  - `Reef` → `HUB`
  - `ReefSticks` → `HubPositions` or similar
  - `getReefLocation()` → `getHubLocation()`
  - `getAlgaeReefLocation()` → `getFuelLocation()`

### 1.2 File Renaming
- [ ] Rename all related Java files
- [ ] Update all import statements
- [ ] Update all references

---

## Priority 2: Field and Location System Updates

### 2.1 AprilTag Field Layout
- [ ] **Update AprilTag field layout**
  - `Locations.java` line 62: `AprilTagFields.k2025ReefscapeAndyMark` → `AprilTagFields.k2026Rebuilt`
  - Verify all AprilTag IDs are correct (2026 uses IDs 1-32)
  - Update all AprilTag position references

### 2.2 Location System Refactoring
- [ ] **Update starting positions**
  - `blueStarts[]` and `redStarts[]` in `Locations.java` need to be updated to 2026 field coordinates
  - 2026 field dimensions: 317.7in × 651.2in
  - Update ROBOT STARTING LINE positions

- [ ] **Update HUB positions**
  - HUB is located 158.6 inches from ALLIANCE WALL
  - Each alliance has one HUB
  - Update all HUB-related position calculations

- [ ] **Update DEPOT positions**
  - DEPOT is located near ALLIANCE WALL
  - Each alliance has one DEPOT
  - Update collection station positions

- [ ] **Update TOWER positions**
  - TOWER is located on ALLIANCE WALL, between DRIVER STATION 2 and 3
  - Update climbing positions and angles

- [ ] **Delete/update no longer relevant positions**
  - Delete Reef-related position calculations
  - Update or delete Barge positions (if no longer needed)
  - Update Processor positions (if no longer needed)

### 2.3 Location Chooser Updates
- [ ] **Update `LocationChooser.java`**
  - Update `ReefSticks` enum → `HubPositions` or new naming
  - Remove no longer needed options (e.g., Reef positions A-L)
  - Add HUB-related options
  - Update `selectCoralStation()` → `selectFuelLocation()` or similar

- [ ] **Update `Locations.java`**
  - Rewrite all location calculation methods to adapt to 2026 field
  - Update `getReefLocation()` → `getHubLocation()`
  - Update `getAlgaeReefLocation()` → `getFuelLocation()`
  - Update all Transform2d offsets to adapt to new field geometry

---

## Priority 3: Game Mechanism Implementation

### 3.1 HUB Activation Logic
- [ ] **Implement HUB status tracking**
  - Create `HubStatusSubsystem` or similar subsystem
  - Read AUTO scoring results from FMS
  - Track current HUB activation status (Active/Inactive)
  - Implement SHIFT switching logic:
    - AUTO: Both HUBS active
    - TRANSITION SHIFT: Both HUBS active
    - SHIFT 1-4: Alternate activation based on AUTO results
    - END GAME: Both HUBS active

- [ ] **Update scoring logic**
  - Only score when HUB is active
  - Show warning or disable scoring commands when HUB is inactive

### 3.2 TOWER Climbing System
- [ ] **Update climbing positions**
  - LEVEL 1: 27.0 inches (68.58cm) - AUTO only
  - LEVEL 2: 45.0 inches (114.3cm)
  - LEVEL 3: 63.0 inches (1.6m)
  - Update position values in `LiftAndScore.java`

- [ ] **Update climbing commands**
  - Ensure LEVEL 1 is only available during AUTO
  - Ensure only one LEVEL score can be earned during TELEOP
  - Update `ClimbCommand.java` to adapt to new requirements

- [ ] **Update TOWER contact rules**
  - Robot can only contact RUNGS, UPRIGHTS, TOWER WALL, support structures, FUEL, or other robots
  - Ensure code logic complies with rules

### 3.3 FUEL Handling System
- [ ] **Unify FUEL handling**
  - Merge Coral and Algae handling logic (both are FUEL)
  - Update all intake/jettison commands
  - Ensure any number of FUEL can be controlled

- [ ] **Update FUEL collection locations**
  - DEPOT: 24 FUEL
  - OUTPOST CHUTE: 24 FUEL
  - NEUTRAL ZONE: 360-408 FUEL (depending on preload)
  - Update auto paths to collect FUEL

---

## Priority 4: Auto Path and Command Updates

### 4.1 Auto Path Refactoring
- [ ] **Update PathPlanner paths**
  - Delete all 2025 path files
  - Create new 2026 paths:
    - From starting position to HUB
    - From DEPOT to HUB
    - From NEUTRAL ZONE to HUB
    - TOWER climbing paths

- [ ] **Update auto sequences**
  - `buildFullAutoSequence()` in `DriveCommand.java` needs complete rewrite
  - Update `autoCoral()` → `autoFuel()` method
  - Implement new auto strategies:
    - AUTO: Score FUEL, possibly climb TOWER LEVEL 1
    - TELEOP: Score based on HUB status

### 4.2 Command Updates
- [ ] **Update `LiftAndScore.java`**
  - Rename all level constants
  - Update position values to adapt to HUB height (72 inches)
  - Update angle and position calculations

- [ ] **Update `DriveCommand.java`**
  - Update all position references
  - Update pathfinding logic
  - Add HUB status checks

- [ ] **Update all Intake/Jettison commands**
  - Unify to FUEL handling
  - Update speed and control logic

---

## 🔵 Priority 5: Vision and Localization Systems

### 5.1 Limelight Updates
- [ ] **Update AprilTag detection**
  - Verify Limelight configuration uses 2026 AprilTags
  - Update all AprilTag ID references
  - Test detection of all 32 AprilTags

- [ ] **Update vision localization**
  - `VisionSubsystem.java` needs verification
  - `LimeLocalizationSubsystem.java` needs checking
  - Ensure correct field layout is used

### 5.2 FUEL Detection
- [ ] **Update neural network pipeline**
  - If using neural network for FUEL detection, need to retrain model
  - Update pipeline switching logic
  - Test FUEL detection accuracy

---

## Priority 6: Subsystem Improvements

### 6.1 Drive System
- [ ] **Verify drive configuration**
  - Are CAN IDs correct? (Current: 1,2,3,4)
  - Are gear ratio and wheel diameter correct?
  - Update track width if robot dimensions changed

### 6.2 Lift System
- [ ] **Update lift positions**
  - Verify all position values adapt to HUB height
  - Test all level positions
  - Update PID parameters if needed

### 6.3 Intake System
- [ ] **Unify intake logic**
  - Merge Floor and Lift intake logic (if applicable)
  - Update speed and control
  - Test FUEL handling

### 6.4 Climb System
- [ ] **Verify climb mechanism**
  - Ensure all 3 LEVELs can be reached
  - Test contact detection
  - Update control logic

---

## Priority 7: Code Cleanup and Optimization

### 7.1 Remove Obsolete Code
- [ ] **Delete unused classes**
  - Check for unused commands
  - Delete obsolete position calculations
  - Clean up unused imports

- [ ] **Update comments and documentation**
  - Update all JavaDoc comments
  - Update README file
  - Add 2026 game-specific notes

### 7.2 Code Optimization
- [ ] **Refactor duplicate code**
  - Unify FUEL handling logic
  - Optimize position calculations
  - Improve error handling

- [ ] **Add constants class**
  - Create `Constants.java` or similar file
  - Centralize all game-specific constants:
    - HUB height
    - TOWER positions
    - FUEL dimensions
    - Field dimensions

### 7.3 Testing and Validation
- [ ] **Unit tests**
  - Test position calculations
  - Test HUB status logic
  - Test command sequences

- [ ] **Integration tests**
  - Test complete auto paths
  - Test HUB activation switching
  - Test TOWER climbing

---

## Technical Debt and Known Issues

### Known Issues
1. **CANDriveSubsystem.java line 24**
   - Comment mentions SparkFlex issue, using SparkMax as temporary fix
   - Need to resolve hardware issue or update code

2. **AprilTag field layout**
   - Currently using 2025 layout, must update

3. **Location system**
   - All positions based on 2025 field, need complete rewrite

### Items to Confirm
- [ ] Has robot physical dimensions changed?
- [ ] Are CAN IDs correct?
- [ ] Do motor configurations need updating?
- [ ] Are sensor configurations correct?

---

## Implementation Recommendations

### Phase 1: Basic Migration (Weeks 1-2)
1. Complete Priority 1 and 2 (terminology and location system)
2. Update all file naming
3. Update AprilTag field layout

### Phase 2: Game Mechanisms (Weeks 3-4)
1. Implement HUB activation logic
2. Update TOWER climbing system
3. Unify FUEL handling

### Phase 3: Auto and Commands (Weeks 5-6)
1. Create new paths
2. Update auto sequences
3. Test all commands

### Phase 4: Testing and Optimization (Weeks 7-8)
1. Comprehensive testing
2. Performance optimization
3. Code cleanup

---

## Reference Resources

- 2026 Game Manual: `2026GameManual (1).pdf`
- KitBot Guide: `2026-kitbot-java-guide.pdf`
- WPILib Documentation: https://docs.wpilib.org
- PathPlanner Documentation: https://pathplanner.dev
- REBUILT Field Drawings: (Need to download from FIRST website)

---

## Checklist

Before each commit, check:
- [ ] All terminology updated
- [ ] All position calculations correct
- [ ] HUB activation logic working
- [ ] TOWER climbing functionality normal
- [ ] Auto paths can run
- [ ] Code compiles
- [ ] No obvious errors or warnings

---

**Last Updated**: 2026-01-23
**Project Status**: Migration in Progress
**Responsible Person**: [To be filled]
