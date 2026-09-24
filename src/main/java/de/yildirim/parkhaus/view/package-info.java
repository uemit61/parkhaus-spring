/**
 * Die Swing-Oberfläche: Fenster, Tabellen und die Anzeige der Meldungen.
 *
 * <p>Unverändert aus dem Swing-Projekt übernommen. Dass sie ohne eine einzige
 * Anpassung auf der neuen Datenschicht läuft, ist der eigentliche Nachweis, dass
 * die Schichtung des Vorgängers getragen hat.
 *
 * <p>Klassen dieses Pakets kennen von der Anwendung nur den Controller und die
 * Namen der Ereignisse, auf die sie sich anmelden. Sie entscheiden, <em>wie</em>
 * ein Ergebnis aussieht — ob als Text, als Tabelle oder als Warnung —, aber
 * niemals, <em>was</em> der Fall ist.
 *
 * <p>Die Tabellenmodelle füllen ihre Spalten per Reflection: Aus dem
 * Spaltennamen wird der Name des passenden Getters gebaut. Eine zusätzliche
 * Spalte braucht deshalb nur einen weiteren Eintrag im Namensfeld — der Preis
 * dafür ist, dass ein Tippfehler im Spaltennamen kein Übersetzungsfehler ist,
 * sondern erst beim Zeichnen auffällt.
 *
 * <p>Ab v2.0 tritt eine REST-Schnittstelle <em>neben</em> dieses Paket, nicht an
 * seine Stelle: Beide bedienen denselben Service.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.view;
