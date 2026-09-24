/**
 * Die Tabellen der Datenbank als JPA-Entitäten: je eine Klasse pro Tabelle, mit
 * Feldern, Gettern und Settern und sonst nichts.
 *
 * <p>Klassen dieses Pakets kennen weder die Datenbank noch die View. Die
 * Zuordnung zu den Spalten steht trotzdem nirgends ausgeschrieben: die Felder
 * stehen in camelCase, die Spalten in snake_case, und die Namensstrategie
 * übersetzt zwischen beiden. Deshalb kommt das Paket ohne eine einzige
 * {@code @Column}-Angabe aus — und aus demselben Grund muss die Benennung
 * deutsch bleiben, sobald eine Migration sie festgelegt hat.
 *
 * <p>Fremdschlüssel sind als Beziehungen abgebildet, nicht als {@code int} oder
 * {@code String}. Damit kann ein Konstruktor Etagennummer und Platznummer nicht
 * mehr verwechseln; vorher standen dort zwei {@code int} nebeneinander, und ein
 * Vertauscher wäre erst zur Laufzeit aufgefallen.
 *
 * <p>{@code toString()} liest bewusst nur Schlüsselfelder: Auf einem noch nicht
 * geladenen Proxy würde jedes andere Feld eine Nachlade-Abfrage auslösen.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.model.entity;
