/**
 * Der Ereignis-Kanal zwischen Model und View.
 *
 * <p>Die Services melden über eine gemeinsame Instanz; welche View sich auf
 * welchen Ereignisnamen anmeldet, entscheidet die View selbst. Dadurch kennt die
 * Model-Schicht keine einzige View-Klasse — sie nennt nur Namen.
 *
 * <p>Unverändert aus dem Swing-Projekt übernommen. Der einzige Unterschied liegt
 * außerhalb dieses Pakets: die gemeinsame Instanz entsteht jetzt als
 * {@code @Bean} statt im Composition Root. Dass die Entkopplung den Wechsel des
 * Rahmenwerks ohne Anpassung überstanden hat, spricht für ihren Schnitt.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.model.event;
