/**
 * Das Bindeglied zwischen Oberfläche und Fachschicht.
 *
 * <p>Nimmt entgegen, was der Benutzer auslöst, und leitet es an den zuständigen
 * Service weiter. Fachlogik steht hier keine, Datenzugriff erst recht nicht —
 * das Paket weiß ausschließlich, wer wofür zuständig ist.
 *
 * <p>Die Klassen tragen {@code @Component} und nicht {@code @Controller}:
 * Letzteres bedeutet in Spring „Web-Controller" und wäre eine falsche Aussage,
 * solange die Anwendung keinen Webserver startet.
 *
 * <p>Lackmustest: Eine Bedingung, die über ein fachliches Ergebnis entscheidet,
 * gehört nach {@link de.yildirim.parkhaus.model.service}. Was hier steht, darf
 * nur weiterreichen.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
package de.yildirim.parkhaus.controller;
