package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.entity.Garage;
import de.yildirim.parkhaus.model.entity.Parketage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Sort;

import org.springframework.test.context.ActiveProfiles;



import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Prüft die abgeleiteten Abfragen von {@link GarageRepository} gegen echtes SQL.
 *
 * <p>Scheibentest: {@code @DataJpaTest} lädt nur die JPA-Beans — keine
 * {@code @Service}, keine {@code @Configuration}. Der CommandLineRunner entsteht
 * hier also gar nicht, und zwar nicht durch einen Riegel, sondern weil er nicht
 * zur Scheibe gehört.
 *
 * <p>Geprüft wird, was beim Start NICHT geprüft wird: Spring Data bestätigt beim
 * Hochfahren nur, dass die Methodennamen auflösbar sind — nicht, dass sie das
 * Richtige tun.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GarageRepositoryTest
{
    private final GarageRepository garageRepository;
    private final TestEntityManager em;

    // Merkregel: Im Produktionscode braucht ein einziger Konstruktor kein @Autowired —
    // Spring nimmt ihn von selbst. Im Test schon, weil die Voreinstellung umgekehrt ist.
    @Autowired
    GarageRepositoryTest(GarageRepository garageRepository, TestEntityManager em)
    {
        this.garageRepository = garageRepository;
        this.em = em;
    }

    /**
     * Legt ein Fahrzeug an und stellt es auf einen Platz.
     *
     * <p>Die Parketagen kommen aus der Flyway-Migration V2 und sind schon da —
     * Flyway hat sie beim Aufbau des Kontexts festgeschrieben, außerhalb der
     * Testtransaktion. Nur das Fahrzeug und die Belegung entstehen hier.
     */
    private void parke(String nummernschild, String typ, int platzNr, int etageNr)
    {
        // Testdaten nicht mit dem Werkzeug anlegen, das geprüft werden soll.(findBy...)
        // findById(id)	em.find(Klasse, id), save(objekt)	em.persist(…)
        // Mit TestEntityManager sind Aufbau und Prüfung unabhängig
        Fahrzeug fahrzeug = em.persist(new Fahrzeug(nummernschild, typ));
        Parketage etage = em.find(Parketage.class, etageNr);
        em.persist(new Garage(platzNr, etage, fahrzeug));
    }

    // Kein GoTO, sondern eher ein ComeTo: es springt vor jedem Test hier hoch und parkt das Auto,
    // daher kein wiederholtes Parken in den Tests.
    @BeforeEach
    void aufbau()
    {
        parke("HH-AB 123","Auto",3,1);
    }

    @Test
    @DisplayName("findByFahrzeug_Nummernschild liefert eine leere Liste, wenn das Fahrzeug nicht parkt")
    void findByKennzeichenLiefertLeereListe()
    {
        List<Garage> gefunden = garageRepository.findByFahrzeug_Nummernschild("HH-AA 123");

        assertNotNull(gefunden, "abgeleitete Abfragen liefern nie null");
        assertTrue(gefunden.isEmpty());
    }

    @Test
    @DisplayName("existsByFahrzeug_Nummernschild liefert true, wenn das Fahrzeug parkt, sonst false")
    void existsByFahrzeug_NummernschildLiefertTrueOderFalse()
    {
        assertTrue(garageRepository.existsByFahrzeug_Nummernschild("HH-AB 123"), "Liefert true wenn das Fahrzeug parkt ");
        assertFalse(garageRepository.existsByFahrzeug_Nummernschild("OB-TS 61"), "Liefert false wenn das Fahrzeug nicht parkt.");
    }

    @Test
    @DisplayName("Prüft Etage und Typ über Garageobjekt, welches mit findByFahrzeug_Nummernschild gefunden wird.")
    void pruefeEtageUndTypUeberFindByFahrzeug_Nummernschild()
    {
        // Auto von oben Nummernschild: HH-AB 123 Etage: 1, Platz: 3
        Garage gefunden = garageRepository.findByFahrzeug_Nummernschild("HH-AB 123").getFirst();

        assertEquals("Auto",gefunden.getFahrzeug().getTyp(),"Liefert True, weil as Auto geparkt");
        assertEquals(1,gefunden.getParketage().getEtageNr(),"Liefert True, weil auf Etage 1");
    }

    @Test
    @DisplayName("Liefert false, wenn falscher Typ, nichts gelöscht, und true bei erfolgreicher Löschung")
    void deleteByFahrzeug_NummernschildAndFahrzeug_TypFalseOderTrue()
    {
        assertEquals(0,garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ("HH-AB 123", "Motorrad") ,
                        "Richtiges Kennzeichen aber falscher Typ, dies liefert 0, weil nichts gelöscht wurde");
        assertEquals(1, garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ("HH-AB 123", "Auto"),
                        "Das Löschen ist erfolgreich,dies  liefert 1, weil das Auto parkt (s.O @BeforeEach)");
        // Das Fahrzeug wird erneut mit dem selben Nummernschild und Typ gelöscht, um nachzuweisen,
        // dass der Datensatz wirklich weg ist. Dafür flush() und clear(), sonst könnte die Methode 1 melden
        em.flush();
        em.clear();
        assertEquals(0, garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ("HH-AB 123", "Auto"),
                "Abfrage liefert 0, der obige Löschvorgang war erfolgreich.");
    }

    @Test
    @DisplayName("Liefert true bei erfolgreicher Sortierung")
    void findAllSortedLiefertGeordneteListe()
    {
        // Parken von zwei weiteren Autos, somit ist die Reihenfolge 3,1,2
        // Das erste auto wird automatisch mit @BeforeEach geparkt
        parke("HH-AC 123","Motorrad",1,1);
        parke("HH-AD 123","Auto",2,1);

        assertEquals(List.of(1,2,3), garageRepository.findAll(Sort.by("platzNr")).stream().map(Garage::getPlatzNr).toList());
    }
}
