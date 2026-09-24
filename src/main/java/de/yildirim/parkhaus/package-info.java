/**
 * Der Einstiegspunkt der Anwendung und zugleich die Wurzel der Komponentensuche.
 *
 * <p>{@code @SpringBootApplication} durchsucht das Paket, in dem die annotierte
 * Klasse liegt, und alles darunter. Deshalb steht hier genau eine Klasse: läge
 * sie tiefer, blieben die Pakete oberhalb unsichtbar, und keine einzige Bean
 * würde gefunden.
 *
 * <p>Wie die Anwendung zusammengesetzt wird, steht nicht hier, sondern in
 * {@link de.yildirim.parkhaus.config}. In der {@code main}-Methode bleibt nur,
 * was feststehen muss, <em>bevor</em> der Container hochfährt.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus;
