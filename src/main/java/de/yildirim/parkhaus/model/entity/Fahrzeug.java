package de.yildirim.parkhaus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


/**
 * Ein registriertes Fahrzeug der Tabelle {@code fahrzeug}: Kennzeichen und Typ.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen, weil JPA es verlangt.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
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

    // Getter und Setter
    public String getNummernschild()
    {
        return nummernschild;
    }

    public void setNummernschild(String nummernschild)
    {
        this.nummernschild = nummernschild;
    }

    public String getTyp()
    {
        return typ;
    }

    public void setTyp(String typ)
    {
        this.typ = typ;
    }


}
