# DARKFORGE v5.0

A Java 21 character/crew management system for the *Coriolis: The Great Dark* tabletop RPG.

## What's New in Iteration 5

- **`darkforge.concurrency` package** — parallel dice-pool probability simulator built on `ExecutorService` + `Callable<BatchResult>` + `AtomicLong`
    - `DiceSimulator` — fixed-pool coordinator (sized to available cores), seeded per-batch RNG, `runParallel` / `runSequential` produce identical batch sets at the same seed
    - `RollBatchTask` — `Callable<BatchResult>` implementing the Coriolis: The Great Dark push rule (re-roll any die not showing a 6 or a 1)
    - `ProbabilityTable` — lock-free `AtomicLong` aggregator; batch counters merged once per batch
    - `BatchResult` — immutable batch value object (rolls, successes, sixes, banes)
- **`BackgroundTask<T>`** — generic `SwingWorker<T, Integer>` adapter for off-EDT GUI work, with fluent `onResult` / `onError` handlers
- **GUI threading** — Catalog reload and serialization benchmark now run via `BackgroundTask`; new `DiceSimulatorPanel` tab with live progress (Phase 2)
- **`FacadeConcurrency`** — new subfacade wired by `FacadeDarkforge.initialize()`, with executor shutdown hook
- **CLI option `[14] Dice Probability Simulator`** — runs parallel + sequential at the same seed, prints success rate, average sixes, banes, and speedup
- **`ConcurrencyException`** — uniform error type for executor/batch failures (`DarkForgeException` is now unchecked: `extends RuntimeException`)
- **JUnit 5 concurrency tests** — convergence (±1% against rulebook values), parallel-vs-sequential equivalence, `ProbabilityTable` thread-safety, `RollBatchTask` ↔ `DicePool` statistical equivalence

## Requirements

- Java 21+
- `lib/json-20240303.jar` (org.json)
- `lib/gson-2.11.0.jar` (Google Gson)
- `lib/junit-platform-console-standalone-1.10.2.jar` (testing)

## Build & Run

Compile

```bash
javac -cp "lib/*" -d out \
    src/darkforge/*.java
```

Run CLI

```bash
java -cp "out:lib/*" darkforge.Main
```

Run GUI

```bash
java -cp "out:lib/*" darkforge.gui.DarkforgeGui
```

Run serialization benchmark

```bash
java -cp "out:lib/*" darkforge.persistence.SerializationBenchmark
```

Run dice probability simulator (CLI option `[14]`)

```bash
java -cp "out:lib/*" darkforge.Main
# then choose [14] Dice Probability Simulator
```

Run tests

```bash
java -jar lib/junit-platform-* -cp out --scan-classpath
```

## Project Structure

```
src/darkforge/
├── collection/    # Inventory, generics
├── concurrency/   # ExecutorService, Callable, AtomicLong, SwingWorker adapter (NEW)
├── cli/           # CLI menus (option [14] added)
├── crew/          # Crew, Vehicle, analytics
├── data/          # GameDataProvider, catalog
├── display/       # Displayable, formatters
├── exception/     # DarkForgeException (now unchecked), ConcurrencyException (NEW)
├── facade/        # Facade pattern (FacadeConcurrency added)
├── gui/           # Swing GUI (DiceSimulatorPanel added, background tasks wired)
├── model/         # GameEntity hierarchy
└── persistence/   # Serialization
src/resources/     # JSON catalog files
test/darkforge/    # JUnit 5 test suite (concurrency tests added)
```

## Architecture

Hybrid composition + interfaces.

`Explorer` implements `InventoryHolder<CharacterItem>` and `Equippable<Weapon>`.

`Vehicle` implements `InventoryHolder<CargoItem>` and `Equippable<VehicleModule>`.

Facade pattern provides simplified access via `FacadeDarkforge` → `FacadeCrew` / `FacadeCatalog` / `FacadeConcurrency`.

### Concurrency model

- `DiceSimulator` owns a fixed-size `ExecutorService` (one thread per available core) for the lifetime of the application; shut down via `FacadeConcurrency.shutdown()` from `Main`'s `finally` block.
- Roll batches are submitted as `Callable<BatchResult>` via `invokeAll`; each task owns its own seeded `java.util.Random` so no RNG is shared across threads.
- Aggregation uses `AtomicLong` per counter, hit once per batch (not once per roll), keeping contention bounded by batch count.
- GUI off-EDT work goes through `BackgroundTask<T>` (a `SwingWorker<T, Integer>` adapter); `done()` dispatches result/error back to the EDT.

### Rulebook fidelity

Dice mechanics follow *Coriolis: The Great Dark* exactly: each 6 is a success, and pushing re-rolls any die not showing a 6 or a 1. Sixes lock as successes; ones lock as banes (Hope damage on Base dice, gear damage on Gear dice). The convergence test asserts a 3-die pool reaches the analytic targets within ±1%: unpushed ≈ 42.1% ($1 - (5/6)^3$), pushed ≈ 62.3% ($1 - (13/18)^3$).
