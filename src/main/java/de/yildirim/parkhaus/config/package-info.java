/**
 * Der Aufbau der Anwendung: alles, was der Container nicht von allein wissen kann.
 *
 * <p>Nachfolger des Composition Root aus {@code MainGarage} im Swing-Projekt.
 * Die Kette aus Repositories, Services und Controller baut der Container selbst,
 * sobald die Klassen annotiert sind — von Hand verdrahtet wird nichts mehr.
 * Übrig bleiben zwei Dinge: ein Objekt aus dem JDK, das sich nicht annotieren
 * lässt, und der Start der Oberfläche, sobald der Kontext vollständig steht.
 *
 * <p>Lackmustest: Steht in diesem Paket eine fachliche Entscheidung, ist sie am
 * falschen Ort. Beschrieben wird hier, <em>wie</em> die Teile zusammenkommen,
 * nicht <em>was</em> sie tun.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.config;
