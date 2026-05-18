package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TalentCategoryTest {

  @Test
  void shouldHaveExactlyNineCategories() {
    assertEquals(10, TalentCategory.values().length,
        "Coriolis Ch. 3 defines exactly 9 talent categories");
  }

  @Test
  void eachCategoryShouldHaveNonBlankDisplayNameAndRoundTrip() {
    for (TalentCategory category : TalentCategory.values()) {
      assertFalse(category.getDisplayName().isBlank(),
          category + " should have a non-blank display name");
      assertEquals(category, TalentCategory.valueOf(category.name()),
          "valueOf(name()) should round-trip for " + category);
    }
  }
}
