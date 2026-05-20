# DARKFORGE v2.0

Console-based Coriolis explorer management project for CS622.

This iteration focuses on handling text and exceptions in a plain Java application. Players can create explorers through an interactive wizard, save them as JSON, reload them later, browse the current session roster, search by name, and delete saved files.

## Current Iteration Focus

- Text-driven console UX through a multi-step creation wizard and main menu.
- JSON-backed game data loaded at startup from resource files.
- Exception-driven validation and recovery for invalid input, corrupted save files, and data-loading failures.
- Separation of concerns through facade, creation, mechanics, display, data, and persistence packages.
- JUnit 5 coverage across CLI, creation, data, display, mechanics, model, facade, and persistence layers.

## Implemented Features

- Create a new explorer by choosing:
  - profession
  - name or generated name
  - origin or random D66 roll
  - specialty
  - attribute distribution
  - talent point allocation
  - personal details
- Render a formatted character sheet in the console.
- Save explorers to `.darkforge.json` files.
- Load explorers from disk with corruption checks and user-facing error messages.
- Search in-session explorers by name.
- View all explorers created or loaded during the current session.
- Delete saved explorer files from the save directory.

## Tech Stack

- Java 21
- `org.json` via `lib/json-20240303.jar`
- JUnit 5.10.2 for tests

The included IntelliJ project metadata is configured for Temurin 21.

## Project Layout

```text
darkforge/
├── lib/                  External jars
├── src/
│   ├── darkforge/        Application source
│   └── resources/        JSON game data and name tables
├── test/                 JUnit test suite
└── out/                  Compiled output
```

Key packages:

- `darkforge.cli`: console menu and creation wizard
- `darkforge.creation`: explorer factory, name generation, search utilities
- `darkforge.data`: JSON loading and validation of game data
- `darkforge.display`: console formatting for explorer sheets and summaries
- `darkforge.exception`: domain-specific checked exceptions
- `darkforge.facade`: top-level API that coordinates subsystems
- `darkforge.mechanics`: attribute validation, dice, and table helpers
- `darkforge.model`: explorer domain model
- `darkforge.persistence`: save/load serialization and corruption detection

## Prerequisites

- Java 21 JDK
- A shell with `javac` and `java` on the path
- Optional: IntelliJ IDEA or VS Code Java tooling for easier test execution

This README assumes you are running commands inside WSL.

## Build And Run

This repository does not currently use Maven or Gradle. Compile it as a plain Java project and copy the resource files into the output directory so they are available on the runtime classpath.

### WSL

```bash
mkdir -p out
javac -cp "lib/json-20240303.jar" -d out $(find src/darkforge -name "*.java")
cp -r src/resources/* out/
java -cp "out:lib/json-20240303.jar" darkforge.Main
```

If you launch the project from an IDE, make sure `src/resources` is marked as a resources root or otherwise copied onto the runtime classpath.

## Running Tests

Tests are written with JUnit 5 and live under `test/`.

The simplest way to run them is through an IDE that already resolves JUnit 5.10.2, such as IntelliJ IDEA using the existing project metadata.

If you prefer CLI-based test execution, you will need to provide the JUnit 5 jars on the classpath in addition to the compiled `src` and `test` classes. This repository does not currently include a dedicated build script or wrapper for that setup.

## Runtime Data And Saves

- Static game data is loaded from JSON files under `src/resources`.
- Profession-specific first and last names are stored in `src/resources/names`.
- Saved explorers are written to the WSL user's home directory under `~/.darkforge/saves`.
- Save files use the `.darkforge.json` extension.

## Menu Flow

When the application starts, it initializes game data and opens the main menu:

1. Create New Explorer
2. Save Explorer to File
3. Load Explorer from File
4. View All Explorers
5. Search Explorers
6. Delete Save File
0. Quit

The creation wizard and menu intentionally use exception-handling loops so invalid input and corrupted files can be recovered from without crashing the application.

## Notes For Development

- `Main` initializes the `FacadeDarkforge` singleton before any user interaction.
- `GameDataProvider` loads and validates all JSON-backed tables once at startup.
- `ExplorerDeserializer` wraps malformed or invalid saved data in `CharacterCorruptionException` so the CLI can show structured recovery messages.
- `ExplorerFileManager` demonstrates try-with-resources for writers, readers, and directory streams.

## Course Context

- Course: CS622
- Iteration theme: Assignment 2, handling text and exceptions
- Domain: Coriolis explorer creation and management