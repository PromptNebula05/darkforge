# DARKFORGE v3.0

Console-based Coriolis explorer and crew management project for CS622.

This iteration adds Java Generics, Collections, and enhanced File I/O to the existing explorer management system. Players can now assemble crews of explorers, assign crew roles, choose a Garuda Bird companion, select vehicles, and persist full crew compositions as JSON.

## Current Iteration Focus

- Generic containers with bounded type parameters (`Inventory<T extends GameEntity>`, `Registry<K, V extends GameEntity>`).
- Generic interface (`Selectable<T>`) with three implementations (D6Table, D66Table, WeightedSelector).
- Wildcard bounds (`? extends T`, `? super T`) for type-safe collection operations.
- Multiple collection classes: `EnumMap`, `LinkedHashMap`, `LinkedHashSet`, `ArrayList`, `HashSet`.
- Collections utilities: `Collections.unmodifiableList/Set/Map`, composable `Comparator` chains.
- Crew persistence via `CrewSerializer`/`CrewDeserializer`/`CrewFileManager` with try-with-resources.
- Standalone `GenericsDemo` class demonstrating all generic patterns.

## Implemented Features

### Explorer Management (Iterations 1–2)

- Create a new explorer by choosing profession, name, origin, specialty, attributes, talents, and personal details.
- Render a formatted character sheet in the console.
- Save explorers to `.darkforge.json` files.
- Load explorers from disk with corruption checks and user-facing error messages.
- Search, view, and delete saved explorers.

### Crew Assembly (Iteration 3)

- Assemble a crew of 4–5 explorers through a step-by-step wizard.
- Assign crew roles (Delver, Scout, Burrower, Guard, Archaeologist) with fitness-based suggestions.
- Choose a Garuda Bird companion (Ward, Guide, or Specter) with type-specific powers and energy management.
- Select a shuttle and rover with stat blocks from the rulebook.
- Customize vehicle names and paint colors.
- Distribute supply points across the crew.
- Save and load complete crew compositions as `.darkforge-crew.json` files.
- Browse talents by category or search by name.

### Generics & Collections (Iteration 3)

- `Inventory<T extends GameEntity>` — bounded generic container with capacity enforcement, search, filter, sort.
- `EquipmentInventory` — weight-based specialization of `Inventory<Equipment>` (Ch. 6 encumbrance).
- `Registry<K, V extends GameEntity>` — two-parameter generic for keyed grouping.
- `Selectable<T>` — generic interface unifying D6Table, D66Table, and WeightedSelector.
- `WeightedSelector<T>` — probability-weighted random selection.
- `ExplorerComparators` — composable `Comparator` chains for multi-key sorting.
- `CrewAnalytics` — attribute averages, talent coverage, optimal role assignment.

## Tech Stack

- Java 21
- `org.json` via `lib/json-20240303.jar`
- JUnit 5.10.2 for tests

The included IntelliJ project metadata is configured for Temurin 21.

## Project Layout
Domain: Coriolis explorer creation and management