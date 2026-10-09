package de.yildirim.parkhaus.model.service;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.event.Ereignis;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.repository.FahrzeugRepository;
import de.yildirim.parkhaus.model.repository.GarageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fachlogik rund um die registrierten Fahrzeuge: Anmelden, Löschen und die
 * Prüfung des Kennzeichens.
 *
 * <p>Die Klasse entscheidet, <em>was</em> der Fall ist, und meldet das Ergebnis
 * über den Ereignis-Kanal. SQL steht hier keines — dafür sind die Repositories da.
 *
 * <p><b>Normalisierung:</b> Jede öffentliche Methode setzt das Kennzeichen als
 * Erstes auf Großbuchstaben. Das ist keine Kosmetik — das Kennzeichen ist der
 * Primärschlüssel, und "hh-aa 123" wäre sonst ein anderes Fahrzeug als
 * "HH-AA 123". Dass es auch ohne diese Zeile zu funktionieren scheint, liegt
 * allein an MySQLs Vorgabe-Kollation {@code utf8mb4_0900_ai_ci}, die Groß- und
 * Kleinschreibung beim Vergleich ignoriert. Auf einer Datenbank, die das nicht
 * tut, fiele die Anwendung sofort auseinander.
 *
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Service
public class FahrzeugService
{
    // Deutsches Kennzeichen: 1-3 Buchstaben, Bindestrich, 1-2 Buchstaben,
    // Leerzeichen, 1-4 Ziffern ohne führende Null.
    // Einmal beim Laden der Klasse übersetzt, nicht bei jedem Aufruf - deshalb static final.
    private static final Pattern KENNZEICHEN = Pattern.compile("[A-ZÄÖÜ]{1,3}-[A-Z]{1,2}\\s[1-9][0-9]{0,3}");
    private final FahrzeugRepository fahrzeugRepository;
    private final GarageRepository garageRepository;
    private final PropertyChangeHandle pch;

    public FahrzeugService(FahrzeugRepository fahrzeugRepository, GarageRepository garageRepository,
            PropertyChangeHandle pch)
    {
        this.fahrzeugRepository = fahrzeugRepository;
        this.garageRepository = garageRepository;
        this.pch = pch;
    }

    /**
     * Trägt ein Fahrzeug in die Datenbank ein, sofern das Kennzeichen noch nicht
     * vergeben ist.
     *
     * <p>Die Rückgabe unterscheidet die beiden Fälle, die von außen gleich
     * aussehen: neu eingetragen oder schon bekannt. Die Einfahrt braucht genau
     * das — ein bekanntes Fahrzeug muss anschließend noch gegen den registrierten
     * Fahrzeugtyp geprüft werden, ein neues nicht.
     *
     * @param nummernschild das Kennzeichen; wird in Großbuchstaben gespeichert
     * @param typ           Fahrzeugtyp, zum Beispiel "Auto"
     * @param admin         {@code true} für den Aufruf aus dem Adminbereich, wo der
     *                      Benutzer eine Bestätigung erwartet; bei der Einfahrt
     *                      wird still registriert
     * @return {@code true}, wenn das Fahrzeug neu eingetragen wurde; {@code false},
     * wenn es bereits registriert war, das Kennzeichen nicht dem Format
     * entspricht.
     */
    @Transactional
    public boolean fahrzeugRegistrieren(String nummernschild, String typ, boolean admin)
    {
        nummernschild = nummernschild.toUpperCase(Locale.ROOT);
        String[] props = new String[2];
        props[0] = nummernschild;
        props[1] = typ;
        boolean retVal = false;


        if (istGueltigesKennzeichen(nummernschild))
        {

            //Wenn die Abfrage von der Administration kommt, wird geprüft, ob es schon registriert ist.
            if (fahrzeugRepository.existsById(nummernschild))
            {
                if (admin)
                    pch.propertyChange(Ereignis.SCHON_REGISTRIERT, nummernschild);
            }
            else
            {
                fahrzeugRepository.save(new Fahrzeug(nummernschild, typ));
                if (admin)
                    pch.propertyChange(Ereignis.REGISTRIERT, props);

                retVal = true;
            }
        }
        else
            pch.propertyChange(Ereignis.KENNZEICHEN_UNGUELTIG, nummernschild);

        return retVal;
    }

    /**
     * Liest alle registrierten Fahrzeuge und schickt sie als Liste an die View, die
     * daraus ihre Tabelle füllt.
     */
    public void autoTabelle()
    {
        //Datenbank
        List<Fahrzeug> liste_Fahrzeug = fahrzeugRepository.findAll();

        //View
        pch.propertyChange(Ereignis.FAHRZEUG_TABELLE, liste_Fahrzeug);
        pch.propertyChange(Ereignis.PANEL_TABELLE_FAHRZEUG, null);
    }

    /**
     * Löscht ein Fahrzeug aus der Tabelle, sofern es nicht gerade im Parkhaus steht.
     *
     * <p>Ein geparktes Fahrzeug wird bewusst nicht gelöscht — sein Parkplatz würde
     * mitverschwinden — und meldet stattdessen "Verboten" an die View. Die
     * Datenbank verhindert denselben Fall über {@code ON DELETE RESTRICT}.
     *
     * @param nummernschild Kennzeichen des zu löschenden Fahrzeugs
     * @param typ           Fahrzeugtyp; steht mit in der WHERE-Bedingung, gelöscht
     *                      wird also nur, wenn Kennzeichen und Typ zusammenpassen
     */
    @Transactional
    public void loescheFahrzeug(String nummernschild, String typ)
    {
        nummernschild = nummernschild.toUpperCase(Locale.ROOT);
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            String[] props = new String[2];
            props[0] = nummernschild;
            props[1] = typ;

            if (!garageRepository.existsByFahrzeug_Nummernschild(nummernschild))
            {
                if(fahrzeugRepository.existsById(nummernschild))
                {
                    if (fahrzeugRepository.deleteByNummernschildAndTyp(nummernschild, typ) != 0)
                        pch.propertyChange(Ereignis.FAHRZEUG_GELOESCHT, props);
                    else
                        pch.propertyChange(Ereignis.ALARM,null);
                }
                else
                    pch.propertyChange(Ereignis.FAHRZEUG_LOESCHEN_FEHLGESCHLAGEN, props);
            }
            else
                pch.propertyChange(Ereignis.LOESCHEN_VERBOTEN, nummernschild);
        }
        else
        {
            pch.propertyChange(Ereignis.KENNZEICHEN_UNGUELTIG, nummernschild);
        }

    }

    /**
     * Prüft das Kennzeichen gegen das deutsche Format: ein bis drei Buchstaben,
     * Bindestrich, ein bis zwei Buchstaben, Leerzeichen, ein bis vier Ziffern ohne
     * führende Null (siehe {@code KENNZEICHEN}).
     *
     * <p>Die Methode prüft nur und meldet nichts — "FailCheck" schickt der
     * Aufrufer, der auch weiß, in welchem Zusammenhang die Eingabe kam. Dadurch
     * hat sie keine Abhängigkeiten und lässt sich ohne Datenbank und ohne
     * Oberfläche testen.
     *
     * @param nummernschild die Eingabe des Benutzers; Groß- und Kleinschreibung
     *                      spielt keine Rolle
     * @return {@code true}, wenn das Format stimmt
     */
    public static boolean istGueltigesKennzeichen(String nummernschild)
    {
        boolean retVal = false;

        Matcher m = KENNZEICHEN.matcher(nummernschild.toUpperCase(Locale.ROOT));

        if (m.matches())
            retVal = true;

        return retVal;
    }

}
