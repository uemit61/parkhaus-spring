package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prüft die abgeleiteten Abfragen von {@link FahrzeugRepository} gegen echtes SQL.
 *
 * <p>Geprüft werden ausschließlich die beiden selbst deklarierten Methoden. Alles
 * andere — {@code findById}, {@code existsById}, {@code save} — kommt geerbt aus
 * {@code JpaRepository} und ist fremder Code; dafür hat Spring Data eigene Tests.
 *
 * <p>Beide Methoden tragen dieselbe Fachregel: Seit v4.1 gehört der Fahrzeugtyp
 * zur Identität eines Fahrzeugs. Ein bekanntes Kennzeichen mit abweichendem Typ
 * darf weder als vorhanden gelten noch gelöscht werden. Der Name allein sichert
 * das nicht zu — beim Start bestätigt Spring Data nur, dass er auflösbar ist.
 *
 * <p>Einfacher als {@code GarageRepositoryTest}: Hier genügen Fahrzeug-Zeilen,
 * es braucht weder eine Etage noch eine Belegung.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class FahrzeugRepositoryTest
{
    private final FahrzeugRepository fahrzeugRepository;
    private final TestEntityManager em;

    @Autowired
    public FahrzeugRepositoryTest(FahrzeugRepository fahrzeugRepository, TestEntityManager em)
    {
        this.fahrzeugRepository = fahrzeugRepository;
        this.em = em;
    }

    /**
     * Legt ein Fahrzeug an.
     *
     * <p>Bewusst über den {@link TestEntityManager} und nicht über das Repository:
     * Testdaten sollen nicht mit dem Werkzeug entstehen, das geprüft werden soll —
     * sonst teilen Aufbau und Prüfung dieselben Annahmen, und ein Fehler könnte
     * sich selbst decken.
     *
     * <p>{@code persist} statt {@code save}, weil hier feststeht, dass der Eintrag
     * neu ist. Die Methode sagt das aus und scheitert laut, wenn es nicht stimmt;
     * {@code save} würde stillschweigend ein {@code merge} daraus machen.
     *
     * @param nummernschild Kennzeichen, zugleich der Primärschlüssel
     * @param typ           Fahrzeugtyp, zum Beispiel "Auto"
     */
    private void registrieren(String nummernschild, String typ)
    {
        em.persist(new Fahrzeug(nummernschild,typ));
    }

    @BeforeEach
    void aufbau(){ registrieren("TT-TS 61","Auto");}


    @ParameterizedTest
    @DisplayName("Liefert 'true' falls Kennzeichen und Typ passen, sonst 'false'")
    @CsvSource(
        {
            "TT-TS 61, Auto, true", // Kennzeichen und Typ passen
            "TT-TS 61, Motorrad, false", // Kennzeichen passt, Typ nicht
            "TT-TS 7, Auto, false" // Kennzeichen unbekannt
        }
    )
    void existsByNummernschildAndTyp(String nummernschild, String typ, boolean erwartet)
    {
        assertEquals(erwartet,fahrzeugRepository.existsByNummernschildAndTyp(nummernschild,typ));
    }

    @Test
    @DisplayName("Liefert 1, wenn Fahrzeug gelöscht worden ist , sonst 0")
    void deleteNummernschildAndTyp()
    {
        assertEquals(1,fahrzeugRepository.deleteByNummernschildAndTyp("TT-TS 61","Auto"),"Liefert 1, wenn erfolgreich gelöscht, worden ist");
        em.persist(new Fahrzeug("TT-TT 61","Motorrad"));
        assertEquals(0,fahrzeugRepository.deleteByNummernschildAndTyp("TT-TT 61","Auto"),"Kennzeichen ok, aber Typ falsch, dies lierert 0");
        // Fahrzeug erneut löschen, um sicher zugehen, dass wirklich gelöscht worden ist.
        em.flush();
        em.clear();

        assertEquals(0, fahrzeugRepository.deleteByNummernschildAndTyp("TT-TS 61","Auto"),"Liefert 0, weil das Auto nicht mehr registriert ist.");
    }
}
