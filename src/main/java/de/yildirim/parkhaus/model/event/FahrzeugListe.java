package de.yildirim.parkhaus.model.event;

import de.yildirim.parkhaus.model.entity.Fahrzeug;

import java.util.List;

/**
 * Alle registrierten Fahrzeuge, auf dem Weg zur Oberfläche.
 *
 * <p>Eine Hülle um eine Liste wirkt überflüssig - sie hat einen handfesten
 * Grund. Der Kanal überträgt {@link Object}, beim Empfänger stand deshalb
 * {@code (List<Fahrzeug>) evt.getNewValue()}. Wegen der Typlöschung weiß die
 * Laufzeit nichts mehr vom Elementtyp und kann diesen Cast nicht prüfen; der
 * Übersetzer warnte, und jede betroffene Methode trug ein
 * {@code @SuppressWarnings("unchecked")}. Gegen {@code FahrzeugListe} ist der
 * Cast vollständig prüfbar, die Unterdrückung entfällt.
 *
 * <p>Bedient zwei Ereignisse: {@link Ereignis#FAHRZEUG_TABELLE} füllt die
 * Fahrzeugtabelle, {@link Ereignis#FAHRZEUGE_GELADEN} die Auswahlliste im
 * Adminbereich. Beide brauchen dieselben Daten an anderer Stelle. Dass ein
 * Record mehrere Ereignisse bedienen darf, gilt nur, solange {@link Ereignis}
 * die Meldung benennt - beim Umstieg auf Springs Ereignismechanismus wird der
 * Typ selbst zum Verteilungsschlüssel, dann trennen sie sich.
 *
 * <p>Die Liste trägt Entities, und das ist hier richtig: Die Tabelle zeigt
 * Zeilen, die Entity ist der Gegenstand. Für Meldungen über einzelne Vorgänge
 * gilt das nicht - dafür steht {@link FahrzeugInfo} daneben.
 *
 * @param fahrzeugListe die Fahrzeuge, wie die Datenbank sie liefert
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public record FahrzeugListe(List<Fahrzeug> fahrzeugListe)
{
}
