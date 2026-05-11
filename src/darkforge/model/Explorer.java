package darkforge.model;

import darkforge.display.Displayable;

import java.util.*;

public abstract class Explorer extends GameEntity implements Displayable {

  private final EnumMap<Attribute, Integer> attributes;
  private Origin origin;
  private Specialty specialty;
  private final List<Talent> talents;
  private String quirk;
  private String keepsake;
  private String appearance;
  private String resolvedContact;
  private String resolvedFaction;
  private String explorerReason;

  protected Explorer(String name, String description) {
    super(name, description);
    this.attributes = new EnumMap<>(Attribute.class);
    this.talents = new ArrayList<>();
  }

  public abstract Attribute getKeyAttribute();

  public abstract List<Talent> getKeyTalents();

  public abstract List<List<Equipment>> getStartingEquipmentSets();

  public abstract String getProfessionName();

  public abstract List<Specialty> getSpecialties();

  public void setAttributes(EnumMap<Attribute, Integer> attrs) {
    for (Attribute a : Attribute.values()) {
      if (!attrs.containsKey(a)) {
        throw new IllegalArgumentException("Missing attribute: " + a);
      }
    }
    this.attributes.putAll(attrs);
  }

  public int getAttribute(Attribute attr) {
    return attributes.getOrDefault(attr, 0);
  }

  public EnumMap<Attribute, Integer> getAttributes() {
    return new EnumMap<>(attributes);
  }

  public int getHealth() {
    return getAttribute(Attribute.STRENGTH) + getAttribute(Attribute.AGILITY);
  }

  public int getHope() {
    return getAttribute(Attribute.LOGIC) + getAttribute(Attribute.EMPATHY);
  }

  public int getHeart() {
    return getAttribute(Attribute.INSIGHT) + getAttribute(Attribute.PERCEPTION);
  }

  public Origin getOrigin() {
    return origin;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

  public Specialty getSpecialty() {
    return specialty;
  }

  public void setSpecialty(Specialty specialty) {
    this.specialty = specialty;
  }

  public List<Talent> getTalents() {
    return Collections.unmodifiableList(talents);
  }

  public boolean addTalent(Talent talent) {
    for (Talent existing : talents) {
      if (existing.getName().equalsIgnoreCase(talent.getName())) {
        existing.increaseLevel();
        return false;
      }
    }
    talents.add(talent);
    return true;
  }

  public String getQuirk() {
    return quirk;
  }

  public void setQuirk(String quirk) {
    this.quirk = quirk;
  }

  public String getKeepsake() {
    return keepsake;
  }

  public void setKeepsake(String keepsake) {
    this.keepsake = keepsake;
  }

  public String getAppearance() {
    return appearance;
  }

  public void setAppearance(String appearance) {
    this.appearance = appearance;
  }

  /**
   * Returns the resolved contact name (set during Explorer creation via D6 roll).
   */
  public String getResolvedContact() {
    return resolvedContact;
  }

  public void setResolvedContact(String resolvedContact) {
    this.resolvedContact = resolvedContact;
  }

  /**
   * Returns the resolved faction name (set during Explorer creation; may be from
   * D6 roll).
   */
  public String getResolvedFaction() {
    return resolvedFaction;
  }

  public void setResolvedFaction(String resolvedFaction) {
    this.resolvedFaction = resolvedFaction;
  }

  /**
   * Returns the reason this Explorer joined the Explorers Guild (Ch. 2 step 10).
   */
  public String getExplorerReason() {
    return explorerReason;
  }

  public void setExplorerReason(String explorerReason) {
    this.explorerReason = explorerReason;
  }

  @Override
  public String display() {
    String spec = (specialty != null) ? " (" + specialty.getName() + ")" : "";
    return String.format("%s — %s%s — Health %d / Hope %d / Heart %d",
        name, getProfessionName(), spec, getHealth(), getHope(), getHeart());
  }

  @Override
  public String toFormattedString() {
    return display();
  }

  @Override
  public String toSummary() {
    return display();
  }
}
