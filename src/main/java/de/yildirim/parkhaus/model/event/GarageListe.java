package de.yildirim.parkhaus.model.event;

import de.yildirim.parkhaus.model.entity.Garage;

import java.util.List;

/**
 * Alle Parkplätze mit ihrer Belegung, auf dem Weg zur Parkplatztabelle.
 *
 * <p>Das Gegenstück zu {@link FahrzeugListe} und aus demselben Grund
 * vorhanden: Der Cast beim Empfänger lautete {@code (List<Garage>)} und war
 * wegen der Typlöschung nicht prüfbar. Gegen {@code GarageListe} ist er es.
 *
 * <p>Wird als {@link Ereignis#PARKPLATZ_TABELLE} gemeldet, unmittelbar gefolgt
 * von {@link Ereignis#PANEL_TABELLE} - erst die Daten, dann der Wechsel der
 * Ansicht. Die Reihenfolge ist Absicht: Die Tabelle ist gefüllt, bevor sie
 * sichtbar wird.
 *
 * @param garageListe die Parkplätze, wie die Datenbank sie liefert
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public record GarageListe(List<Garage> garageListe)
{
}
