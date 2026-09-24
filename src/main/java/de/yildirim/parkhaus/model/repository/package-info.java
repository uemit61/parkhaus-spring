/**
 * Der Datenzugriff: je ein Interface pro Entität, ohne eine Zeile Implementierung.
 *
 * <p>Die Implementierungen erzeugt Spring Data beim Start aus den Methodennamen.
 * An die Stelle des handgeschriebenen SQL der DAO-Schicht des Swing-Projekts
 * tritt damit der Name selbst: Ein Unterstrich trennt die Eigenschaften zweier
 * Klassen und erzeugt den JOIN, der vorher als Zeichenkette dastand.
 *
 * <p><b>Was dabei geprüft wird und was nicht:</b> Beim Hochfahren bestätigt
 * Spring Data nur, dass ein Methodenname <em>auflösbar</em> ist — nicht, dass er
 * das Richtige tut, und den Rückgabetyp gar nicht. Ein falsch gedachter Name
 * fällt also erst im Betrieb auf. Genau deshalb liegt zu diesem Paket ein Test
 * gegen echtes SQL.
 *
 * <p>Lackmustest: Ein Aufruf des Ereignis-Kanals wäre hier falsch. Repositories
 * liefern Daten und melden nichts; was aus den Daten folgt, entscheidet
 * {@link de.yildirim.parkhaus.model.service}.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.model.repository;
