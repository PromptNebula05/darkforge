# DARKFORGE v4.0

A Java 21 character/crew management system for the Coriolis: The Great Dark tabletop RPG.

## What's New in Iteration 4

- **Equipment Catalog** loaded from JSON resources (weapons, armor, modules, cargo)
- **Generic interfaces** (InventoryHolder<T>, Equippable<T>) with composition via EquipmentLoadout<T>
- **Three serialization approaches** compared: Binary, Hand-rolled JSON, Gson
- **Extended stream analytics** for catalog and crew inventories
- **Swing GUI** for catalog browsing, inventory management, vehicle upgrades
- **Console catalog browser** (options 9-12)

## Requirements

- Java 21+
- lib/json-20240303.jar (org.json)
- lib/gson-2.10.1.jar (Google Gson)
- lib/junit-platform-console-standalone-1.10.2.jar (testing)

## Build & Run

Compile

	javac -cp "lib/*" -d out \
	src/darkforge/*.java

Run CLI

	java -cp "out:lib/*" darkforge.Main

Run GUI

	java -cp "out:lib/*" darkforge.gui.DarkforgeGui

Run serialization benchmark

	java -cp "out:lib/*" darkforge.persistence.SerializationBenchmark

Run tests

	java -jar lib/junit-platform-* -cp out --scan-classpath

## Project Structure

	src/darkforge/
	├── collection/ # Inventory, generics
	├── console/ # CLI menus
	├── crew/ # Crew, Vehicle, analytics
	├── data/ # GameDataProvider, catalog
	├── display/ # Displayable, formatters
	├── facade/ # Facade pattern
	├── gui/ # Swing GUI (NEW)
	├── model/ # GameEntity hierarchy
	├── persistence/ # Serialization (NEW)
	src/resources/ # JSON catalog files (NEW)
	src/test/ # JUnit 5 test suite

## Architecture

Hybrid composition + interfaces.

Explorer implements InventoryHolder<CharacterItem> and Equippable<Weapon>.
Vehicle implements InventoryHolder<CargoItem> and Equippable<VehicleModule>.

Facade pattern provides simplified access via FacadeDarkforge → FacadeCrew / FacadeCatalog.