-- DARKFORGE v6.0 — relational schema (Coriolis: The Great Dark)
-- Applied at startup by DatabaseManager via the hybrid bootstrap.
-- Re-running against an existing darkforge.db is a no-op (IF NOT EXISTS).

PRAGMA foreign_keys = ON;   -- SQLite enforces FKs per-connection only when set

CREATE TABLE IF NOT EXISTS crew (
                                    crew_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name       TEXT    NOT NULL UNIQUE,
                                    bird_name  TEXT
);

CREATE TABLE IF NOT EXISTS explorer (
                                        explorer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        name        TEXT    NOT NULL,
                                        profession  TEXT    NOT NULL,   -- raw display name, e.g. 'Odd Jobber' (no CHECK; ExplorerFactory validates on reload)
                                        strength    INTEGER NOT NULL CHECK (strength   BETWEEN 2 AND 6),
    agility     INTEGER NOT NULL CHECK (agility    BETWEEN 2 AND 6),
    logic       INTEGER NOT NULL CHECK (logic      BETWEEN 2 AND 6),
    perception  INTEGER NOT NULL CHECK (perception BETWEEN 2 AND 6),
    insight     INTEGER NOT NULL CHECK (insight    BETWEEN 2 AND 6),
    empathy     INTEGER NOT NULL CHECK (empathy    BETWEEN 2 AND 6)
    -- Derived stats (Health = STR+AGL, Hope = LOG+EMP, Heart = INS+PER)
    -- are NOT stored; computed in Java by Explorer.getHealth/Hope/Heart().
    );

CREATE TABLE IF NOT EXISTS crew_membership (
                                               crew_id     INTEGER NOT NULL,
                                               explorer_id INTEGER NOT NULL,
                                               role        TEXT    NOT NULL
                                               CHECK (role IN (
                                               'DELVER','SCOUT','BURROWER','GUARD','ARCHAEOLOGIST')),
    PRIMARY KEY (crew_id, explorer_id),                       -- composite PK
    FOREIGN KEY (crew_id)     REFERENCES crew(crew_id)         ON DELETE CASCADE,
    FOREIGN KEY (explorer_id) REFERENCES explorer(explorer_id) ON DELETE CASCADE
    );
