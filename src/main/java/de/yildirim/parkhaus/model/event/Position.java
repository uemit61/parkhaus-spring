package de.yildirim.parkhaus.model.event;

/**
 * Der Standplatz eines Fahrzeugs: wo es steht und worum es sich handelt.
 *
 * <p>Vorher wanderten diese vier Werte als {@code List<Object>} über den Kanal,
 * und der Empfänger griff mit {@code position.get(2)} auf die Etage zu. Dass an
 * Stelle 2 die Etage steht, war nirgends festgehalten - es stand im Sender, in
 * einer anderen Schicht. Ein Dreher dort hätte in der Oberfläche
 * "Etage TT-TS 61" ergeben: übersetzbar, lauffähig, falsch.
 *
 * <p>Die Reihenfolge der Bestandteile weicht bewusst von der alten Liste ab.
 * Sie durfte es, denn mit Namen trägt die Stellung im Konstruktor keine
 * Bedeutung mehr. Genau das war der Zweck der Umstellung.
 *
 * <p>Eine Lücke bleibt: {@code nummernschild} und {@code typ} sind beide
 * {@code String} und stehen nebeneinander. Werden sie beim Aufruf vertauscht,
 * übersetzt das stumm. Dagegen schützt kein Record, nur der Test.
 *
 * <p>Wird als {@link Ereignis#ZEIGE_POSITION} an zwei Stellen gemeldet: nach
 * dem Einparken und auf Nachfrage zu einem bereits parkenden Fahrzeug.
 *
 * @param nummernschild Kennzeichen des Fahrzeugs, normalisiert
 * @param typ           Fahrzeugtyp, etwa {@code "PKW"} oder {@code "LKW"}
 * @param etage         Nummer der Etage, beginnend bei 1
 * @param platz         Nummer des Parkplatzes, fortlaufend über alle Etagen
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public record Position(String nummernschild, String typ, int etage, int platz)
{

}
