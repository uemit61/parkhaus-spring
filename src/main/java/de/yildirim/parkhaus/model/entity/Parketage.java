package de.yildirim.parkhaus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Eine Etage der Tabelle {@code parketage}: Etagennummer und ihre Platzzahl.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen, weil JPA es verlangt.
 *
 * @author Ümit Yildirim <hopes61@icloud.com>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Entity
public class Parketage
{
    @Id
    private int etageNr;

    //@Column(name ="anzahl_plaetze")-Annotation nicht nötig, der Namensstrategie-Mechanismus(siehe Hibernate 7.0) übersetzt dies korrekt.
    private int anzahlPlaetze;

    //Konstruktoren
    public Parketage() {}

    public Parketage(int etageNr, int anzahlPlaetze)
    {
        this.etageNr = etageNr;
        this.anzahlPlaetze = anzahlPlaetze;
    }

    @Override
    public String toString()
    {
        return String.format("Parketage: %d", etageNr);
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(getEtageNr());
    }

    @Override
    public boolean equals(Object p)
    {
        return p instanceof Parketage parketage && (getEtageNr() == parketage.getEtageNr());
    }

    public int getEtageNr()
    {
        return etageNr;
    }

    public int getAnzahlPlaetze()
    {
        return anzahlPlaetze;
    }

}
