package de.yildirim.parkhaus.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Ein belegter Parkplatz der Tabelle {@code garage}: Platznummer, Parketage und das
 * Fahrzeug, das dort steht.
 *
 * <p>Reine Datenklasse ohne Verhalten — sie kennt weder die Datenbank noch die
 * View. Der leere Konstruktor steht bewusst neben dem vollen, weil JPA es verlangt.
 *
 * <p>Die beiden Fremdschlüssel sind als {@code @ManyToOne}-Beziehungen abgebildet,
 * nicht als {@code int}/{@code String}. Ein {@code @JoinColumn} braucht es dabei
 * nirgends: die Spaltennamen der Migration folgen bereits der JPA-Konvention
 * <em>Feldname + "_" + Schlüsselspalte</em>.
 *
 * <p>{@code toString()} nennt nur Schlüsselfelder. Andere Felder würden auf einem
 * noch nicht geladenen Proxy eine Nachlade-Abfrage auslösen.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
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

    @Override
    public int hashCode()
    {
        return Integer.hashCode(getPlatzNr());
    }

    @Override
    public boolean equals(Object g)
    {
        return g instanceof Garage garage && (getPlatzNr() == garage.getPlatzNr());
    }

    public int getPlatzNr()
    {
        return platzNr;
    }

    public Parketage getParketage()
    {
        return parketage;
    }

    /**
     * Liefert die Etagennummer als flachen Wert.
     *
     * <p>Der ungewöhnliche Name ist Absicht: Das per Reflection arbeitende
     * TableModel sucht seine Getter über die Spaltennamen. Ein Setter dazu wäre
     * gefährlich — er würde den Primärschlüssel einer fremden Entität ändern.
     *
     * @return die Nummer der Etage, auf der dieser Platz liegt
     */
    public int getParketage_etageNr()
    {
        return getParketage().getEtageNr();
    }


    public Fahrzeug getFahrzeug()
    {
        return fahrzeug;
    }

    public String getFahrzeug_nummernschild()
    {
        return getFahrzeug().getNummernschild();
    }


}
