package darkforge.model;

import darkforge.collection.*;
import darkforge.data.GameDataProvider;
import darkforge.data.ProfessionData;
import darkforge.display.CharacterSheetFormatter;
import darkforge.display.Displayable;

import java.io.Serial;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An Explorer (player character) in Coriolis.
 * Implements InventoryHolder and Equippable for
 * generic inventory and equip operations, with
 * composed Inventory and EquipmentLoadout fields.
 * Existing equipmentBySource preserved for
 * backward compatibility with serialization
 * and character-sheet formatting.
 */
public abstract class Explorer
        extends GameEntity
        implements Displayable,
        InventoryHolder<CharacterItem>,
        Equippable<Weapon> {

  @Serial
  private static final long
          serialVersionUID = 10L;

  // =========================================
  // Fields
  // =========================================

  private final EnumMap<Attribute, Integer>
          attributes;
  private Origin origin;
  private Specialty specialty;
  private final Inventory<Talent> talents;
  private final LinkedHashMap<String,
          List<Equipment>> equipmentBySource =
          new LinkedHashMap<>();
  private String quirk;
  private String keepsake;
  private String appearance;
  private String resolvedContact;
  private String resolvedFaction;
  private String explorerReason;

  private final Inventory<CharacterItem>
          itemInventory;
  private final EquipmentLoadout<Weapon>
          weaponLoadout;

  // =========================================
  // Constructor
  // =========================================

  protected Explorer(String name,
                     String description) {
    super(name, description);
    this.attributes =
            new EnumMap<>(Attribute.class);
    this.talents =
            new Inventory<>(name, -1);
    this.itemInventory =
            new Inventory<>(name, -1);
    // 3 weapons at hand (Ch. 6)
    this.weaponLoadout =
            new EquipmentLoadout<>(
                    name, 3);
  }

  // =========================================
  // Abstract methods
  // =========================================

  public abstract Attribute
  getKeyAttribute();

  public abstract List<Talent>
  getKeyTalents();

  public abstract List<List<Equipment>>
  getStartingEquipmentSets();

  public abstract String
  getProfessionName();

  public abstract List<Specialty>
  getSpecialties();

  // =========================================
  // ProfessionData helper for subclasses
  // =========================================

  protected ProfessionData
  loadProfessionData() {
    String displayName =
            getProfessionName();
    ProfessionData pd =
            GameDataProvider.getTheInstance()
                    .getProfession(displayName);
    if (pd == null) {
      pd = GameDataProvider
              .getTheInstance()
              .getProfession(
                      displayName.replace(
                              " ", ""));
    }
    return pd;
  }

  // =========================================
  // Attributes
  // =========================================

  /**
   * Bulk-set all attributes. Validates that
   * every Attribute key is present.
   */
  public void setAttributes(
          EnumMap<Attribute, Integer>
                  attrs) {
    for (Attribute a
            : Attribute.values()) {
      if (!attrs.containsKey(a)) {
        throw
                new IllegalArgumentException(
                        "Missing attribute: "
                                + a);
      }
    }
    this.attributes.putAll(attrs);
  }

  /** Set a single attribute value. */
  public void setAttribute(
          Attribute attr, int value) {
    attributes.put(attr, value);
  }

  public int getAttribute(Attribute attr) {
    return attributes
            .getOrDefault(attr, 0);
  }

  public EnumMap<Attribute, Integer>
  getAttributes() {
    return new EnumMap<>(attributes);
  }

  // =========================================
  // Derived stats
  // =========================================

  public int getHealth() {
    return getAttribute(
            Attribute.STRENGTH)
            + getAttribute(
            Attribute.AGILITY);
  }

  public int getHope() {
    return getAttribute(
            Attribute.LOGIC)
            + getAttribute(
            Attribute.EMPATHY);
  }

  public int getHeart() {
    return getAttribute(
            Attribute.INSIGHT)
            + getAttribute(
            Attribute.PERCEPTION);
  }

  // =========================================
  // Origin & Specialty
  // =========================================

  public Origin getOrigin() {
    return origin;
  }

  public void setOrigin(Origin origin) {
    this.origin = origin;
  }

  public Specialty getSpecialty() {
    return specialty;
  }

  public void setSpecialty(
          Specialty specialty) {
    this.specialty = specialty;
  }

  // =========================================
  // Talents (Inventory<Talent>)
  // =========================================

  public Inventory<Talent> getTalents() {
    return talents;
  }

  /**
   * Add a talent; if already present,
   * increases its level instead.
   * @return true if newly added
   */
  public boolean addTalent(Talent talent) {
    Talent existing =
            talents.getByName(
                    talent.getName());
    if (existing != null) {
      existing.increaseLevel();
      return false;
    }
    talents.add(talent);
    return true;
  }

  /**
   * Groups talents by TalentCategory using
   * Inventory's Iterable support.
   */
  public Map<TalentCategory, List<Talent>>
  getTalentsByCategory() {
    Map<TalentCategory, List<Talent>>
            grouped = new EnumMap<>(
            TalentCategory.class);
    for (Talent t : talents) {
      grouped.computeIfAbsent(
                      t.getCategory(),
                      k -> new ArrayList<>())
              .add(t);
    }
    return grouped;
  }

  // =========================================
  // Equipment (legacy)
  // =========================================

  public List<Equipment> getEquipment() {
    List<Equipment> all =
            new ArrayList<>();
    for (List<Equipment> list :
            equipmentBySource.values()) {
      all.addAll(list);
    }
    return Collections
            .unmodifiableList(all);
  }

  public LinkedHashMap<String,
          List<Equipment>>
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

  public String getQuirk() {
    return quirk;
  }

  public void setQuirk(String quirk) {
    this.quirk = quirk;
  }

  public String getKeepsake() {
    return keepsake;
  }

  public void setKeepsake(
          String keepsake) {
    this.keepsake = keepsake;
  }

  public String getAppearance() {
    return appearance;
  }

  public void setAppearance(
          String appearance) {
    this.appearance = appearance;
  }

  public String getResolvedContact() {
    return resolvedContact;
  }

  public void setResolvedContact(
          String resolvedContact) {
    this.resolvedContact =
            resolvedContact;
  }

  public String getResolvedFaction() {
    return resolvedFaction;
  }

  public void setResolvedFaction(
          String resolvedFaction) {
    this.resolvedFaction =
            resolvedFaction;
  }

  public String getExplorerReason() {
    return explorerReason;
  }

  public void setExplorerReason(
          String explorerReason) {
    this.explorerReason =
            explorerReason;
  }

  // =========================================
  // InventoryHolder<CharacterItem>
  // =========================================

  @Override
  public Inventory<CharacterItem>
  getInventory() {
    return itemInventory;
  }

  @Override
  public boolean addItem(
          CharacterItem item) {
    return itemInventory.add(item);
  }

  @Override
  public boolean removeItem(
          CharacterItem item) {
    return itemInventory.remove(item);
  }

  @Override
  public List<CharacterItem> searchItems(
          Predicate<CharacterItem>
                  filter) {
    return itemInventory.getAll()
            .stream()
            .filter(filter)
            .collect(Collectors.toList());
  }

  @Override
  public List<CharacterItem>
  getAllItems() {
    return itemInventory.getAll();
  }

  // =========================================
  // Equippable<Weapon>
  // =========================================

  @Override
  public boolean equip(Weapon weapon) {
    if (!itemInventory.contains(
            weapon)) {
      return false;
    }
    return weaponLoadout.equip(weapon);
  }

  @Override
  public boolean unequip(Weapon weapon) {
    return weaponLoadout.unequip(
            weapon);
  }

  @Override
  public List<Weapon> getEquipped() {
    return weaponLoadout.getEquipped();
  }

  @Override
  public boolean isEquipped(
          Weapon weapon) {
    return weaponLoadout.isEquipped(
            weapon);
  }

  // =========================================
  // Carry weight
  // =========================================

  /**
   * Current carry weight of the item
   * inventory using EquipmentWeight values.
   */
  public double getCurrentCarryWeight() {
    return itemInventory.getAll()
            .stream()
            .mapToDouble(ci ->
                    ci.getWeightClass()
                            .getWeightValue())
            .sum();
  }

  /** Max carry weight = STR + 4 (Ch. 6). */
  public double getMaxCarryWeight() {
    return getAttribute(
            Attribute.STRENGTH) + 4;
  }

  public boolean isOverEncumbered() {
    return getCurrentCarryWeight()
            > getMaxCarryWeight();
  }

  public EquipmentLoadout<Weapon>
  getWeaponLoadout() {
    return weaponLoadout;
  }

  // =========================================
  // Display (GameEntity + Displayable)
  // =========================================

  @Override
  public String display() {
    String spec = (specialty != null)
            ? " (" + specialty.getName()
              + ")"
            : "";
    return String.format(
            "%s \u2014 %s%s \u2014 Health %d"
                    + " / Hope %d / Heart %d",
            name, getProfessionName(), spec,
            getHealth(), getHope(),
            getHeart());
  }

  @Override
  public String toFormattedString() {
    return new CharacterSheetFormatter()
            .formatCharacterSheet(this);
  }

  @Override
  public String toSummary() {
    return display();
  }
}