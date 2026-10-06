//Das ist der Punkt, der verwirrt: src/main/java und src/test/java
// sind getrennte Verzeichnisse aber das Paket bestimmt die package-Zeile,
// nicht der Ordner.Maven übersetzt beide Bäume getrennt und
// legt beim Testen beide auf den Klassenpfad. Die JVM
// sieht dann einen einzigen Namensraum, und in de.yildirim.parkhaus.model.service
// liegen FahrzeugService (aus target/classes) und FahrzeugServiceTest (aus target/test-classes)
// einträchtig nebeneinander.
package de.yildirim.parkhaus.model.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prüft die Kennzeichenprüfung aus {@link FahrzeugService}.
 *
 * <p>Reiner Unit-Test: kein Spring, keine Datenbank, keine Oberfläche. Möglich ist
 * das nur, weil {@code istGueltigesKennzeichen} static und nebenwirkungsfrei ist —
 * sie meldet nichts an die View, sondern gibt ein Ergebnis zurück. Deshalb braucht
 * dieser Test nicht einmal ein FahrzeugService-Objekt.
 *
 * <p><b>Auswahl der Fälle:</b> nicht jede Kombination, sondern je Regel des
 * regulären Ausdrucks ein Vertreter plus die beiden Ränder. Das vollständige
 * Kreuzprodukt wären 24 Fälle, die alle dieselben drei Quantifier prüfen — die
 * Teile des Ausdrucks sind voneinander unabhängig, eine Kombination bewiese also
 * nichts Zusätzliches. Ein Zählfehler um eins zeigt sich ausschließlich am Rand;
 * in der Mitte verhalten sich alle Fassungen gleich.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
class FahrzeugServiceTest
{
    @DisplayName("gültige Kennzeichen werden angenommen")
    @ParameterizedTest()
    @ValueSource(strings = {
            "B-X 12",           // Untergrenze Buchstaben: einer vorn, einer hinten
            "ABC-X 12",         // Obergrenze vorn: drei Buchstaben
            "HH-AB 12",         // Obergrenze hinten: zwei Buchstaben
            "LÖ-AB 12",         // Umlaut im Unterscheidungszeichen (Lörrach)
            "HH-AA 1",          // Untergrenze Ziffern: eine
            "HH-AA 1234"        // Obergrenze Ziffern: vier
    })
    void erkenntGueltigeKennzeichen(String nummernschild)
    {
        assertTrue(FahrzeugService.istGueltigesKennzeichen(nummernschild),
                () -> "\"" + nummernschild + "\" sollte gültig sein");
    }

    @DisplayName("ungültige Kennzeichen werden abgelehnt")
    @ParameterizedTest()
    @ValueSource(strings = {
            "-X 12",            // kein Unterscheidungszeichen
            "ABCD-X 12",        // vier Buchstaben vorn, erlaubt sind drei
            "L1-AB 12",         // Ziffer im Buchstabenteil
            "HHAA 12",          // Bindestrich fehlt
            "HH- 12",           // keine Erkennungsbuchstaben
            "HH-ABC 12",        // drei Buchstaben hinten, erlaubt sind zwei
            "HH-AA12",          // Leerzeichen fehlt
            "HH-AA  12",        // zwei Leerzeichen; \s steht für genau eines
            "HH-AA 012",        // führende Null
            "HH-AA 12345",      // fünf Ziffern, erlaubt sind vier
            "XHH-AA 12X",       // Text drumherum; matches() prüft die ganze Eingabe
            ""                  // leere Eingabe
    })
    void erkenntUngueltigeKennzeichen(String nummernschild)
    {
        assertFalse(FahrzeugService.istGueltigesKennzeichen(nummernschild),
                () -> "\"" + nummernschild + "\" sollte abgelehnt werden");
    }

    /**
     * Kleinschreibung wird angenommen, weil die Methode selbst normalisiert.
     *
     * <p>Bewusst ein einzelner Fall und nicht jede Zeile der Tabelle doppelt:
     * geprüft wird hier {@code toUpperCase()}, also eine einzige Zeile Code. Sie
     * 24-mal zu prüfen macht sie nicht richtiger.
     */
    @Test
    @DisplayName("Kleinschreibung wird normalisiert")
    void nimmtKleinschreibungAn()
    {
        assertTrue(FahrzeugService.istGueltigesKennzeichen("hh-aa 123"));
    }

    /**
     * Hält den heutigen Stand fest: bei {@code null} fliegt eine
     * {@link NullPointerException}, weil {@code toUpperCase()} vor jeder Prüfung
     * steht.
     *
     * <p>Das ist eine <em>Aufzeichnung</em>, keine Empfehlung. Die Entscheidung,
     * ob {@code null} statt dessen als ungültig gelten soll, ist offen — dafür
     * müsste die Prüfung vor den ersten Methodenaufruf auf dem Parameter wandern.
     * Ändert sich das Verhalten, muss dieser Test mitgeändert werden; genau
     * deshalb steht er hier.
     */
    @Test
    @DisplayName("null führt derzeit zu einer NullPointerException")
    void wirftBeiNullEineAusnahme()
    {
        assertThrows(NullPointerException.class,
                () -> FahrzeugService.istGueltigesKennzeichen(null));
    }
}
