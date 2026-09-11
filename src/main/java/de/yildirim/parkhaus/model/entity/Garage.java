package de.yildirim.parkhaus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Ein belegter Parkplatz der Tabelle {@code garage}: Platznummer, Parketage und das
 * Fahrzeug, das dort steht. toString() gibt die ID's zurück.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen, weil JPA es verlangt.
 *
 * @author Ümit Yildirim <hopes61@icloud.com>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Entity
public class Garage
{
    // Felder der DB-Tabelle
    @Id
    private int platzNr;

    @ManyToOne
    private Parketage parketage;

    @ManyToOne
    private Fahrzeug fahrzeug;

    //Konstruktoren
    public Garage()
    {
    } // Default-konstruktor

    public Garage(int platzNr, Parketage parketage, Fahrzeug fahrzeug)
    {
        this.fahrzeug = fahrzeug;
        this.parketage = parketage;
        this.platzNr = platzNr;
    }

    @Override
    public String toString()
    {
        return String.format("Garage[%s,%d,%d]", fahrzeug.getNummernschild(), parketage.getEtageNr(), platzNr);
    }

    public int getPlatzNr()
    {
        return platzNr;
    }

    public void setPlatzNr(int platzNr)
    {
        this.platzNr = platzNr;
    }

    public Parketage getParketage()
    {
        return parketage;
    }

    public void setParketage(Parketage parketage)
    {
        this.parketage = parketage;
    }

    public Fahrzeug getFahrzeug()
    {
        return fahrzeug;
    }

    public void setFahrzeug(Fahrzeug fahrzeug)
    {
        this.fahrzeug = fahrzeug;
    }
}
