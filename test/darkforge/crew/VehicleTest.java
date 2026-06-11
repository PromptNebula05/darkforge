package darkforge.crew;

import darkforge.model.Equipment;
import darkforge.model.EquipmentWeight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    // =========================================
    // Rover factories — stat verification
    // =========================================

    @Test
    void rhinoHasCorrectStats() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        assertEquals(3,
                rhino.getManeuverability());
        assertEquals(2, rhino.getSpeed());
        assertEquals(9, rhino.getMaxHull());
        assertEquals(6, rhino.getArmor());
        assertEquals(4,
                rhino.getBlightProtection());
        assertEquals(5,
                rhino.getPassengers());
        assertEquals(1500, rhino.getCargo());
        assertEquals("Wheeled",
                rhino.getPropulsion());
        assertTrue(rhino.getType().isRover());
    }

    @Test
    void crocodileHasCorrectStats() {
        Vehicle croc = Vehicle.createCrocodile(
                "Ironhide", "Olive");
        assertEquals(2,
                croc.getManeuverability());
        assertEquals(2, croc.getSpeed());
        assertEquals(11, croc.getMaxHull());
        assertEquals(7, croc.getArmor());
        assertEquals(3,
                croc.getBlightProtection());
        assertEquals(8,
                croc.getPassengers());
        assertEquals(2000, croc.getCargo());
        assertEquals("Tracked",
                croc.getPropulsion());
    }

    @Test
    void sphinxHasCorrectStats() {
        Vehicle sphinx = Vehicle.createSphinx(
                "Whisper", "White");
        assertEquals(4,
                sphinx.getManeuverability());
        assertEquals(3, sphinx.getSpeed());
        assertEquals(7, sphinx.getMaxHull());
        assertEquals(5, sphinx.getArmor());
        assertEquals(2,
                sphinx.getBlightProtection());
        assertEquals(6,
                sphinx.getPassengers());
        assertEquals(1000, sphinx.getCargo());
        assertEquals("Hover",
                sphinx.getPropulsion());
    }

    // =========================================
    // Shuttle factories — stat verification
    // =========================================

    @Test
    void grasshopperHasCorrectStats() {
        Vehicle gh =
                Vehicle.createGrasshopper(
                        "Jumpstart", "Green");
        assertEquals(3,
                gh.getManeuverability());
        assertEquals(4, gh.getSpeed());
        assertEquals(14, gh.getMaxHull());
        assertEquals(6, gh.getArmor());
        assertEquals(0,
                gh.getBlightProtection());
        assertEquals(6, gh.getPassengers());
        assertEquals(3000, gh.getCargo());
        assertEquals("Graviton",
                gh.getPropulsion());
        assertTrue(
                gh.getType().isShuttle());
    }

    @Test
    void owlHasCorrectStats() {
        Vehicle owl = Vehicle.createOwl(
                "Nightwatch", "Black");
        assertEquals(2,
                owl.getManeuverability());
        assertEquals(3, owl.getSpeed());
        assertEquals(16, owl.getMaxHull());
        assertEquals(7, owl.getArmor());
        assertEquals(0,
                owl.getBlightProtection());
        assertEquals(8,
                owl.getPassengers());
        assertEquals(4000, owl.getCargo());
    }

    @Test
    void mantaHasCorrectStats() {
        Vehicle manta = Vehicle.createManta(
                "Stingray", "Blue");
        assertEquals(4,
                manta.getManeuverability());
        assertEquals(4, manta.getSpeed());
        assertEquals(12, manta.getMaxHull());
        assertEquals(5, manta.getArmor());
        assertEquals(0,
                manta.getBlightProtection());
        assertEquals(5,
                manta.getPassengers());
        assertEquals(2000, manta.getCargo());
    }

    // =========================================
    // Upgrades (Inventory<Equipment>)
    // =========================================

    @Test
    void installUpgradeReducesSlots() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        int initialSlots =
                rhino.getRemainingSlots();

        Equipment turret = new Equipment(
                "Turret Mount", "Weapon slot",
                EquipmentWeight.HEAVY, 3);
        rhino.installUpgrade(turret);

        assertEquals(initialSlots - 1,
                rhino.getRemainingSlots());
    }

    @Test
    void installUpgradeExceedingSlotsThrows() {
        // Sphinx has 5 slots
        Vehicle sphinx = Vehicle.createSphinx(
                "Whisper", "White");

        for (int i = 0; i < 5; i++) {
            sphinx.installUpgrade(
                    new Equipment(
                            "Module " + i, "desc",
                            EquipmentWeight.LIGHT, 1));
        }

        assertEquals(0,
                sphinx.getRemainingSlots());

        assertThrows(
                IllegalStateException.class,
                () -> sphinx.installUpgrade(
                        new Equipment(
                                "Overflow", "desc",
                                EquipmentWeight.LIGHT, 1)));
    }

    @Test
    void getRemainingSlotsTracks() {
        // Rhino has 7 slots
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        assertEquals(7,
                rhino.getRemainingSlots());

        rhino.installUpgrade(new Equipment(
                "Scanner", "desc",
                EquipmentWeight.LIGHT, 1));
        rhino.installUpgrade(new Equipment(
                "Shield Gen", "desc",
                EquipmentWeight.HEAVY, 3));
        assertEquals(5,
                rhino.getRemainingSlots());
    }

    @Test
    void removeUpgradeFreesSlot() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        Equipment scanner = new Equipment(
                "Scanner", "desc",
                EquipmentWeight.LIGHT, 1);

        rhino.installUpgrade(scanner);
        int afterInstall =
                rhino.getRemainingSlots();

        rhino.removeUpgrade(scanner);
        assertEquals(afterInstall + 1,
                rhino.getRemainingSlots());
    }

    @Test
    void upgradeSearchByName() {
        Vehicle owl = Vehicle.createOwl(
                "Nightwatch", "Black");
        owl.installUpgrade(new Equipment(
                "Stealth Plating", "desc",
                EquipmentWeight.HEAVY, 3));

        assertNotNull(
                owl.getUpgrades()
                        .getByName("Stealth Plating"));
        assertNull(
                owl.getUpgrades()
                        .getByName("Nonexistent"));
    }

    // =========================================
    // Hull tracking
    // =========================================

    @Test
    void hullStartsAtMax() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        assertEquals(rhino.getMaxHull(),
                rhino.getCurrentHull());
    }

    @Test
    void takeDamageReducesHull() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        rhino.takeDamage(3);
        assertEquals(6,
                rhino.getCurrentHull());
    }

    @Test
    void takeDamageFloorsAtZero() {
        Vehicle sphinx = Vehicle.createSphinx(
                "Whisper", "White");
        sphinx.takeDamage(100);
        assertEquals(0,
                sphinx.getCurrentHull());
        assertTrue(sphinx.isDestroyed());
    }

    @Test
    void repairCapsAtMaxHull() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        rhino.takeDamage(5);
        rhino.repair(100);
        assertEquals(rhino.getMaxHull(),
                rhino.getCurrentHull());
    }

    // =========================================
    // Name and paint
    // =========================================

    @Test
    void customNamePreserved() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        assertEquals("Dustrunner",
                rhino.getName());
    }

    @Test
    void paintColorPreserved() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Crimson");
        assertEquals("Crimson",
                rhino.getPaintColor());
    }

    // =========================================
    // Display
    // =========================================

    @Test
    void displayContainsAllFields() {
        Vehicle rhino = Vehicle.createRhino(
                "Dustrunner", "Sand");
        String output = rhino.display();

        assertTrue(
                output.contains("Dustrunner"));
        assertTrue(
                output.contains("Rhino"));
        assertTrue(
                output.contains("ROVER"));
        assertTrue(
                output.contains("Sand"));
        assertTrue(
                output.contains("Wheeled"));
        assertTrue(
                output.contains("Hull"));
        assertTrue(
                output.contains("Armor"));
        assertTrue(
                output.contains("Mnv"));
        assertTrue(
                output.contains("Blight"));
        assertTrue(
                output.contains("Slots"));
    }

    @Test
    void displayShowsUpgrades() {
        Vehicle owl = Vehicle.createOwl(
                "Nightwatch", "Black");
        owl.installUpgrade(new Equipment(
                "Stealth Plating", "Radar evasion",
                EquipmentWeight.HEAVY, 3));

        String output = owl.display();
        assertTrue(
                output.contains("Upgrades"));
        assertTrue(
                output.contains(
                        "Stealth Plating"));
    }

    // =========================================
    // Category checks
    // =========================================

    @Test
    void roverFactoriesProduceRovers() {
        assertTrue(Vehicle.createRhino(
                "R", "X").getType().isRover());
        assertTrue(Vehicle.createCrocodile(
                "C", "X").getType().isRover());
        assertTrue(Vehicle.createSphinx(
                "S", "X").getType().isRover());
    }

    @Test
    void shuttleFactoriesProduceShuttles() {
        assertTrue(Vehicle.createGrasshopper(
                "G", "X").getType().isShuttle());
        assertTrue(Vehicle.createOwl(
                "O", "X").getType().isShuttle());
        assertTrue(Vehicle.createManta(
                "M", "X").getType().isShuttle());
    }
}