package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TalentCategoryTest {

    @Test
    void shouldHaveExactlyNineCategories() {
        assertEquals(9, TalentCategory.values().length,
                "Coriolis Ch. 3 defines exactly 9 talent categories");
    }

    @Test
    void eachCategoryShouldHaveCorrectDisplayName() {
        assertAll("display names must match Coriolis Ch. 3 talent list headers",
                () -> assertEquals("Combat", TalentCategory.COMBAT.getDisplayName()),
                () -> assertEquals("Social", TalentCategory.SOCIAL.getDisplayName()),
                () -> assertEquals("Vehicle & Exo", TalentCategory.VEHICLE_EXO.getDisplayName()),
                () -> assertEquals("Knowledge", TalentCategory.KNOWLEDGE.getDisplayName()),
                () -> assertEquals("Insight", TalentCategory.INSIGHT.getDisplayName()),
                () -> assertEquals("Equipment", TalentCategory.EQUIPMENT.getDisplayName()),
                () -> assertEquals("Recovery", TalentCategory.RECOVERY.getDisplayName()),
                () -> assertEquals("Stealth & Mobility", TalentCategory.STEALTH_MOBILITY.getDisplayName()),
                () -> assertEquals("Resilience", TalentCategory.RESILIENCE.getDisplayName())
        );
    }

    @Test
    void valueOfShouldRoundTripForAllConstants() {
        for (TalentCategory category : TalentCategory.values()) {
            assertEquals(category, TalentCategory.valueOf(category.name()),
                    "valueOf(name()) should return the same constant for " + category);
        }
    }
}