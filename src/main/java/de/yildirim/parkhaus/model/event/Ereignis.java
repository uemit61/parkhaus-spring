package de.yildirim.parkhaus.model.event;

/**
 * Das Vokabular des Ereignis-Kanals: Jede Meldung, die über
 * {@link PropertyChangeHandle} laufen kann, steht hier als Konstante.
 *
 * <p>Vorher stand an jeder Melde- und Anmeldestelle eine Zeichenkette. Ein
 * Vertipper war dort kein Übersetzungsfehler, sondern eine Meldung, die
 * niemanden erreicht - besonders tückisch beim <em>Anmelden</em> eines
 * Zuhörers: Dann verstummt eine einzelne Ansicht, ohne dass irgendwo etwas
 * sichtbar schiefgeht. Mit diesem Enum wird daraus ein Fehler beim Übersetzen.
 *
 * <p><b>Die Meldungen laufen in beide Richtungen.</b> Das Model meldet, was
 * fachlich geschehen ist ({@code ALARM}, {@code VOLL}) und dass angeforderte
 * Daten bereitliegen. Die Oberfläche meldet ihrerseits Navigation
 * ({@code ZURUECK_HAUPTPANEL}) - also Ansicht an Ansicht, am Model vorbei.
 *
 * <p><b>Deshalb liegt der Enum hier und nicht in {@code view}.</b> Beide
 * Schichten brauchen ihn, und nur eine Richtung ist erlaubt: Die Oberfläche
 * darf das Model kennen, umgekehrt nicht. Läge er in {@code view}, stünde in
 * jedem Service ein {@code import ...view...} - genau die Abhängigkeit, die
 * die Schichtung dieses Projekts vermeidet.
 *
 * <p><b>Benannt wird nach Tatsachen, nicht nach Befehlen.</b> Es heißt
 * {@code FAHRZEUG_GELOESCHT} und nicht {@code FAHRZEUG_LOESCHEN}, denn die
 * Meldung geht erst hinaus, wenn gelöscht wurde. Das Model berichtet, was
 * geschehen ist; was daraus folgt, entscheidet die Ansicht.
 *
 * <p>Die Typsicherheit endet am Kanal: {@link java.beans.PropertyChangeSupport}
 * kennt nur Zeichenketten, übertragen wird also {@link Enum#name()}. Niemand
 * tippt den Namen mehr von Hand, aber beim Empfänger kommt weiterhin ein
 * {@code String} an. Diese letzte Lücke schließt erst der Umstieg auf Springs
 * Ereignismechanismus mit typisierten Nutzdaten (v1.2).
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public enum Ereignis
{
    // --- Fachereignisse: was im Parkhaus geschehen ist -------------------
    ALARM, PARKPLATZ_VOLL, FREIE_PLAETZE, PARKPLATZ_VERLASSEN, ZEIGE_POSITION, KENNZEICHEN_NICHT_GEFUNDEN, KENNZEICHEN_UNGUELTIG,
    SCHON_REGISTRIERT, REGISTRIERT, FAHRZEUG_GELOESCHT, FAHRZEUG_LOESCHEN_FEHLGESCHLAGEN, LOESCHEN_VERBOTEN,

    // --- Daten für die Tabellen -----------------------------------------
    PARKPLATZ_TABELLE, FAHRZEUG_TABELLE,

    // --- Navigation: welches Fenster die Oberfläche zeigen soll ----------
    PANEL_TABELLE, PANEL_TABELLE_FAHRZEUG, ZURUECK_HAUPTPANEL, ZURUECK_ADMIN_PANEL
//
//    REGIST	REGISTRIERT	Abkürzung, kein Wort
//    TAB_AN	PARKPLATZ_TABELLE	kryptisch; es sind die belegten Plätze
//    AUTO_TAB	FAHRZEUG_TABELLE	dito, und „Auto" ist der falsche Oberbegriff — es gibt auch Motorräder
//    FAIL_CHECK	KENNZEICHEN_UNGUELTIG	sagt, was falsch ist
//    FAIL	KENNZEICHEN_NICHT_GEFUNDEN	„Fail" sagt nur, dass etwas schiefging
//    LOESCHEN_FAIL	LOESCHEN_FEHLGESCHLAGEN	Deutsch/Englisch gemischt
//    VORHANDEN	SCHON_REGISTRIERT	„vorhanden" wo? im Parkhaus oder im Register?
//    ZURUECK umlaute besser weil dann einheitlich mit LOESCHEN
    
}
