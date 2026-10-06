package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Garage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Datenzugriff auf die Tabelle {@code garage}, die belegten Parkplätze.
 *
 * <p>Implementierung gibt es keine — Spring Data erzeugt sie beim Start aus den
 * Methodennamen. Der Unterstrich trennt dabei die Eigenschaften zweier Klassen:
 * links {@code Garage.fahrzeug}, rechts {@code Fahrzeug.nummernschild}. Der JOIN,
 * der in der Swing-Fassung noch als SQL-Text dastand, entsteht daraus von selbst.
 *
 * <p>Geprüft wird beim Start nur der <em>Name</em>, nicht der Rückgabetyp. Ein
 * unpassender Typ fällt erst beim Aufruf auf.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public interface GarageRepository extends JpaRepository<Garage, Integer>
{
    /**
     * @param nummernschild Kennzeichen des gesuchten Fahrzeugs
     * @return {@code true}, wenn dieses Fahrzeug gerade auf einem Platz steht
     */
    boolean existsByFahrzeug_Nummernschild(String nummernschild);

    /**
     * Gibt den Platz frei, auf dem ein bestimmtes Fahrzeug steht.
     *
     * <p>Abgeleitete Löschmethoden liefern {@code void}, die Trefferzahl oder die
     * gelöschten Objekte — mehr gibt Spring Data nicht her. Die Zahl ist hier der
     * einzige Rückkanal, deshalb {@code int}.
     *
     * @param nummernschild Kennzeichen des ausfahrenden Fahrzeugs
     * @param typ           registrierter Fahrzeugtyp; beide müssen zusammenpassen
     * @return Anzahl der gelöschten Zeilen, im Erfolgsfall genau eine
     */
    int deleteByFahrzeug_NummernschildAndFahrzeug_Typ(String nummernschild, String typ);

    /**
     * @param nummernschild Kennzeichen des gesuchten Fahrzeugs
     * @return die Belegung dieses Fahrzeugs; eine leere Liste, wenn es nicht parkt.
     *         Mehr als ein Eintrag ist ausgeschlossen — die Migration legt einen
     *         UNIQUE-Index auf {@code fahrzeug_nummernschild}
     */
    List<Garage> findByFahrzeug_Nummernschild(String nummernschild);
}
