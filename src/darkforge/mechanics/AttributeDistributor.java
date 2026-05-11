package darkforge.mechanics;

import darkforge.model.Attribute;
import java.util.EnumMap;

public final class AttributeDistributor {

  private AttributeDistributor() {
  }

  public static void validate(EnumMap<Attribute, Integer> attributes, Attribute keyAttribute) {
    for (Attribute attr : Attribute.values())
      if (!attributes.containsKey(attr))
        throw new IllegalArgumentException("Missing attribute: " + attr.getDisplayName());

    int total = 0;
    for (int val : attributes.values())
      total += val;
    if (total != 24)
      throw new IllegalArgumentException("Total must be 24, got " + total);

    for (Attribute attr : Attribute.values()) {
      int value = attributes.get(attr);
      if (value < 2)
        throw new IllegalArgumentException(attr.getAbbreviation() + " must be at least 2, got " + value);
      int max = (attr == keyAttribute) ? 6 : 5;
      if (value > max) {
        if (attr == keyAttribute)
          throw new IllegalArgumentException(
              attr.getAbbreviation() + " cannot exceed 6 (key attribute max), got " + value);
        else
          throw new IllegalArgumentException(
              attr.getAbbreviation() + " cannot exceed 5 (non-key attribute max), got " + value);
      }
    }
  }
}
