package de.yildirim.parkhaus.model.event;

/**
 * Kennzeichen und Typ eines Fahrzeugs - unabhängig davon, ob es in der
 * Datenbank steht.
 *
 * <p>Ersetzt ein {@code String[]} mit zwei Plätzen, aus dem der Empfänger mit
 * {@code pos[0]} und {@code pos[1]} las. Welcher Platz welchen Wert trug, stand
 * nur im Sender.
 *
 * <p><b>Warum nicht die Entity {@link de.yildirim.parkhaus.model.entity.Fahrzeug}?</b>
 * Sie hat dieselben zwei Felder, wäre also naheliegend. Sie ist aber eine
 * Aussage über die Datenbank: <em>dies ist eine Zeile der Tabelle
 * {@code fahrzeug}</em>. Von den drei Ereignissen, die diesen Record tragen,
 * trifft das nur auf {@link Ereignis#REGISTRIERT} zu. Bei
 * {@link Ereignis#FAHRZEUG_GELOESCHT} ist die Zeile gerade verschwunden, bei
 * {@link Ereignis#FAHRZEUG_LOESCHEN_FEHLGESCHLAGEN} hat es sie nie gegeben.
 * Eine Entity für eine Zeile zu bauen, die nicht existiert, wäre eine Lüge über
 * die eigene Datenstruktur.
 *
 * <p>Dazu kommt die Blickrichtung: Trüge die Meldung die Entity, wanderte jede
 * später hinzugefügte Spalte - Farbe, Halter, Ablaufdatum - automatisch bis in
 * die Oberfläche. Das Datenbankschema bestimmte dann mit, was die Ansicht zu
 * sehen bekommt.
 *
 * <p>Die beiden Werte stammen ohnehin nicht aus der Datenbank, sondern aus dem
 * Formular: Es sind die Parameter von {@code fahrzeugRegistrieren} und
 * {@code loeschen}, so wie der Benutzer sie angegeben hat.
 *
 * @param nummernschild angegebenes Kennzeichen
 * @param typ           angegebener Fahrzeugtyp
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public record FahrzeugInfo(String nummernschild, String typ)
{
}
