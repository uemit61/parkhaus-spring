-- ---------------------------------------------------------------------------
-- Stammdaten der Parketagen.
--
-- Ohne diese Zeilen meldet das Parkhaus 0 freie Plätze: Aus ihnen berechnet
-- sich sowohl die Gesamtzahl der Plätze als auch die Zuordnung eines Platzes
-- zu seiner Etage. Im Code steht keine feste Platzzahl.
--
-- Bewusst eine eigene Migration: Struktur und Inhalt sind zwei verschiedene
-- Dinge und ändern sich unabhängig voneinander. Kommt später eine Etage
-- hinzu, ist das ein V3 - die Strukturmigration bleibt unberührt.
-- ---------------------------------------------------------------------------

INSERT INTO parketage (etage_nr, anzahl_plaetze)
VALUES (0, 20),
       (1, 120),
       (2, 120);
