package de.yildirim.parkhaus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;




/**
 * Ein registriertes Fahrzeug der Tabelle {@code fahrzeug}: Kennzeichen und Typ.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen, weil JPA es verlangt.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Entity
public class Fahrzeug
{
    // Felder der Tabelle in der DB
    @Id
    private String nummernschild;
    private String typ;

    //Konstruktoren
    public Fahrzeug(){} //Default-Konstruktor
    public Fahrzeug(String nummernschild, String typ)
    {
        this.nummernschild = nummernschild;
        this.typ = typ;
    }

    @Override
    public String toString()
    {
        return String.format("Kennzeichen: %s",nummernschild);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getNummernschild(),getTyp());
    }

    @Override
    public boolean equals(Object a)
    {
        return a instanceof Fahrzeug fahrzeug && Objects.equals(getNummernschild(), fahrzeug.getNummernschild()) && Objects.equals(getTyp(), fahrzeug.getTyp());
    }

    // Getter und Setter
    public String getNummernschild()
    {
        return nummernschild;
    }

    public String getTyp()
    {
        return typ;
    }

}
