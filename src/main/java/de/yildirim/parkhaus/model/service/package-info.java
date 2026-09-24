/**
 * Die Fachlogik: was beim Einfahren, Ausfahren, Registrieren und Löschen
 * geschehen soll, und was davon der View gemeldet wird.
 *
 * <p>Klassen dieses Pakets stellen die Regeln auf — welcher Platz der nächste
 * freie ist, wann ein Fahrzeug nicht gelöscht werden darf — und holen sich die
 * Daten über {@link de.yildirim.parkhaus.model.repository}. Sie entscheiden,
 * <em>was</em> der Fall ist; <em>wie</em> es dem Benutzer gesagt wird,
 * entscheidet die View. Der Controller kennt von der Model-Schicht nur dieses
 * Paket.
 *
 * <p>Hier liegt zugleich die Grenze der Transaktion. Ein Repository ist bereits
 * je Methode transaktional; die sinnvolle Einheit ist aber der fachliche Vorgang
 * („ein Fahrzeug fährt ein") und nicht die einzelne geschriebene Zeile — deshalb
 * steht {@code @Transactional} an den Methoden dieses Pakets.
 *
 * <p><b>Normalisierung:</b> Jede öffentliche Methode setzt das Kennzeichen als
 * Erstes auf Großbuchstaben. Es ist der Primärschlüssel; dass MySQL ohne
 * Rücksicht auf Groß- und Kleinschreibung vergleicht, ist eine Eigenheit der
 * Kollation und kein Verlass.
 *
 * <p>Lackmustest: ein {@code import jakarta.persistence} oder {@code java.sql}
 * in diesem Paket ist ein Zeichen dafür, dass etwas an der falschen Stelle
 * gelandet ist.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.model.service;
