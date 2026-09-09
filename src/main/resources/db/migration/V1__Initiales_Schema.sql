-- ---------------------------------------------------------------------------
-- Initiales Schema der Parkhausverwaltung.
--
-- Kein DROP und kein "IF NOT EXISTS": Eine Flyway-Migration läuft genau
-- einmal und wird in flyway_schema_history vermerkt. Existiert eine Tabelle
-- bereits, SOLL die Migration abbrechen - das ist ein Hinweis, kein Normalfall.
--
-- Kein Schemaname vor den Tabellen: Die Migration läuft gegen die Datenbank
-- aus der JDBC-URL. So lässt sie sich unverändert auch gegen eine
-- Testdatenbank ausführen.
--
-- Keine Angabe zur Zeichenkodierung: Die Tabellen erben das utf8mb4 der
-- Datenbank.
-- ---------------------------------------------------------------------------

CREATE TABLE fahrzeug
(
    nummernschild VARCHAR(45) NOT NULL,
    typ           VARCHAR(45) NOT NULL,

    PRIMARY KEY (nummernschild)
) ENGINE = InnoDB;


CREATE TABLE parketage
(
    etage_nr       INT NOT NULL,
    anzahl_plaetze INT NOT NULL,

    PRIMARY KEY (etage_nr)
) ENGINE = InnoDB;


CREATE TABLE garage
(
    platz_nr               INT         NOT NULL,
    parketage_etage_nr     INT         NOT NULL,
    fahrzeug_nummernschild VARCHAR(45) NOT NULL,

    -- Die Platznummer wird von der Anwendung vergeben (kleinster freier
    -- Platz), nicht von der Datenbank - deshalb kein AUTO_INCREMENT.
    PRIMARY KEY (platz_nr),

    -- Ein Fahrzeug kann nur auf einem Platz stehen. Bisher hat das allein der
    -- Code verhindert; hier steht die Regel an der Stelle, an der sie sich
    -- nicht umgehen lässt.
    CONSTRAINT uk_garage_fahrzeug UNIQUE (fahrzeug_nummernschild),

    -- RESTRICT: Ein Fahrzeug, das gerade parkt, darf nicht gelöscht werden -
    -- sonst verschwände sein Parkplatz mit.
    CONSTRAINT fk_garage_fahrzeug
        FOREIGN KEY (fahrzeug_nummernschild) REFERENCES fahrzeug (nummernschild)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- Ebenfalls RESTRICT: Eine Etage, auf der Fahrzeuge stehen, lässt sich
    -- nicht entfernen. CASCADE würde die Belegungen stillschweigend mit
    -- löschen - die Autos stünden dann im Parkhaus, aber nirgends erfasst.
    CONSTRAINT fk_garage_parketage
        FOREIGN KEY (parketage_etage_nr) REFERENCES parketage (etage_nr)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB;
