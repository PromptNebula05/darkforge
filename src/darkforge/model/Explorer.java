package darkforge.model;

import darkforge.data.GameDataProvider;
import darkforge.data.ProfessionData;
import darkforge.display.Displayable;

import java.util.*;

public abstract class Explorer extends GameEntity
        implements Displayable {

  private final EnumMap<Attribute, Integer>
          attributes;
  private Origin origin;
  private Specialty specialty;
  private final List<Talent> talents;
  private final LinkedHashMap<String,
          List<Equipment>> equipmentBySource =
          new LinkedHashMap<>();
  private String quirk;
  private String keepsake;
  private String appearance;
  private String resolvedContact;
  private String resolvedFaction;
  private String explorerReason;

  protected Explorer(String name,
                     String description) {
    super(name, description);
    this.attributes =
            new EnumMap<>(Attribute.class);
    this.talents = new ArrayList<>();
  }

  // =========================================
  // Abstract methods
  // =========================================

  public abstract Attribute getKeyAttribute();
  public abstract List<Talent> getKeyTalents();
  public abstract List<List<Equipment>>
  getStartingEquipmentSets();
  public abstract String getProfessionName();
  public abstract List<Specialty>
  getSpecialties();

  // =========================================
  // ProfessionData helper for subclasses
  // =========================================

  protected ProfessionData loadProfessionData() {
    String displayName = getProfessionName();
    ProfessionData pd = GameDataProvider.getTheInstance()
            .getProfession(displayName);
    if (pd == null) {
      pd = GameDataProvider.getTheInstance()
              .getProfession(
                      displayName.replace(" ", ""));
    }
    return pd;
  }

  // =========================================
  // Attributes
  // =========================================

  public void setAttributes(
          EnumMap<Attribute, Integer> attrs) {
    for (Attribute a : Attribute.values()) {
      if (!attrs.containsKey(a)) {
        throw new IllegalArgumentException(
                "Missing attribute: " + a);
      }
    }
    this.attributes.putAll(attrs);
  }

  public int getAttribute(Attribute attr) {
    return attributes.getOrDefault(attr, 0);
  }

  public EnumMap<Attribute, Integer>
  getAttributes() {
    return new EnumMap<>(attributes);
  }

  // =========================================
  // Derived stats
  // =========================================

  public int getHealth() {
    return getAttribute(Attribute.STRENGTH)
            + getAttribute(Attribute.AGILITY);
  }

  public int getHope() {
    return getAttribute(Attribute.LOGIC)
            + getAttribute(Attribute.EMPATHY);
  }

  public int getHeart() {
    return getAttribute(Attribute.INSIGHT)
            + getAttribute(Attribute.PERCEPTION);
  }

  // =========================================
  // Origin & Specialty
  // =========================================

  public Origin getOrigin() { return origin; }
  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

  public Specialty getSpecialty() {
    return specialty;
  }
  public void setSpecialty(Specialty specialty) {
    this.specialty = specialty;
  }

  // =========================================
  // Talents
  // =========================================

  public List<Talent> getTalents() {
    return Collections.unmodifiableList(
            talents);
  }

  public boolean addTalent(Talent talent) {
    for (Talent existing : talents) {
      if (existing.getName()
              .equalsIgnoreCase(
                      talent.getName())) {
        existing.increaseLevel();
        return false;
      }
    }
    talents.add(talent);
    return true;
  }

  // =========================================
  // Equipment
  // =========================================

  public List<Equipment> getEquipment() {
    List<Equipment> all = new ArrayList<>();
    for (List<Equipment> list :
            equipmentBySource.values()) {
      all.addAll(list);
    }
    return Collections.unmodifiableList(all);
  }

  public LinkedHashMap<String, List<Equipment>>
  getEquipmentBySource() {
    return new LinkedHashMap<>(
            equipmentBySource);
  }

  public void setEquipment(
          List<Equipment> equipment) {
    this.equipmentBySource.clear();
    this.equipmentBySource.put(
            "Starting Gear",
            new ArrayList<>(equipment));
  }

  public void addEquipment(String source,
                           List<Equipment> equipment) {
    this.equipmentBySource
            .computeIfAbsent(source,
                    k -> new ArrayList<>())
            .addAll(equipment);
  }

  // =========================================
  // Personal details
  // =========================================

  public String getQuirk() { return quirk; }
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

  public String getResolvedContact() {
    return resolvedContact;
  }
  public void setResolvedContact(
          String resolvedContact) {
    this.resolvedContact = resolvedContact;
  }

  public String getResolvedFaction() {
    return resolvedFaction;
  }
  public void setResolvedFaction(
          String resolvedFaction) {
    this.resolvedFaction = resolvedFaction;
  }

  public String getExplorerReason() {
    return explorerReason;
  }
  public void setExplorerReason(
          String explorerReason) {
    this.explorerReason = explorerReason;
  }

  // =========================================
  // Display (GameEntity + Displayable)
  // =========================================

  @Override
  public String display() {
    String spec = (specialty != null)
            ? " (" + specialty.getName() + ")"
            : "";
    return String.format(
            "%s — %s%s — Health %d / Hope %d "
                    + "/ Heart %d",
            name, getProfessionName(), spec,
            getHealth(), getHope(), getHeart());
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