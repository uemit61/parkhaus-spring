package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import org.springframework.data.jpa.repository.JpaRepository;




/**
 * Datenzugriff auf die Tabelle {@code fahrzeug}, die registrierten Fahrzeuge.
 *
 * <p>Der Schlüsseltyp ist {@code String}: das Kennzeichen ist der Primärschlüssel.
 * Deshalb genügt für die Suche nach einem Fahrzeug das geerbte
 * {@code findById}/{@code existsById} — eine eigene Methode {@code ...ById} ließe
 * sich nicht ableiten, weil kein Feld wörtlich {@code id} heißt.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public interface FahrzeugRepository extends JpaRepository<Fahrzeug, String>
{
    /**
     * Löscht ein Fahrzeug, wenn Kennzeichen und Typ zusammenpassen.
     *
     * <p>Das geerbte {@code deleteById} wäre hier zu grob: Es kennt den Typ nicht
     * und gibt ausserdem {@code void} zurück — ob etwas gelöscht wurde, bliebe
     * unbekannt. Es ist bei einer fehlenden Zeile stillschweigend wirkungslos.
     *
     * @param nummernschild Kennzeichen des zu löschenden Fahrzeugs
     * @param typ           registrierter Fahrzeugtyp
     * @return Anzahl der gelöschten Zeilen; {@code 0}, wenn die Kombination so
     *         nicht vorhanden war
     */
    int deleteByNummernschildAndTyp(String nummernschild, String typ);

    /**
     * Prüft, ob genau dieses Fahrzeug mit genau diesem Typ registriert ist.
     *
     * <p>Beide Bedingungen gehören zusammen: Seit v4.1 gehört der Typ zur Identität
     * eines Fahrzeugs. Eine Prüfung allein auf den Typ würde die Regel aushebeln —
     * sie wäre erfüllt, sobald irgendein Fahrzeug dieser Art existiert.
     *
     * @param nummernschild Kennzeichen des Fahrzeugs
     * @param typ           erwarteter Fahrzeugtyp
     * @return {@code true}, wenn Kennzeichen und Typ zum selben Eintrag gehören
     */
    boolean existsByNummernschildAndTyp(String nummernschild, String typ);
}
