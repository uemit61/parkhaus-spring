//Das ist der Punkt, der verwirrt: src/main/java und src/test/java
// sind getrennte Verzeichnisse aber das Paket bestimmt die package-Zeile,
// nicht der Ordner. Maven übersetzt beide Bäume getrennt und
// legt beim Testen beide auf den Klassenpfad. Die JVM
// sieht dann einen einzigen Namensraum, und in de.yildirim.parkhaus.model.service
// liegen FahrzeugService (aus target/classes) und FahrzeugServiceTest (aus target/test-classes)
// einträchtig nebeneinander.
package de.yildirim.parkhaus.model.service;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.event.Ereignis;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.repository.FahrzeugRepository;
import de.yildirim.parkhaus.model.repository.GarageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import static de.yildirim.parkhaus.model.event.Ereignis.*;
import static de.yildirim.parkhaus.model.service.FahrzeugService.istGueltigesKennzeichen;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Prüft {@link FahrzeugService} auf zwei Ebenen, die unterschiedlich viel Umgebung brauchen.
 *
 * <p>Die <b>äußeren Testmethoden</b> prüfen das Verhalten des Service: welche Meldung bei
 * welcher Ausgangslage hinausgeht und welche Repositories dabei berührt werden. Die beiden
 * Repositories sind deshalb Mocks — sie wären sonst die Datenbank. Der Prüfling selbst ist
 * kein Mock, sondern eine echte Instanz, die {@code aufbau()} aus diesen Mocks zusammensetzt.
 *
 * <p>Die verschachtelte Klasse {@code KennzeichenPruefungTest} prüft dagegen nur den
 * statischen Formatprüfer. Der braucht weder Mocks noch ein Service-Objekt — und genau
 * deshalb steht das {@code @BeforeEach} <b>nicht</b> auf oberster Ebene: Ein Aufbau der
 * äußeren Klasse liefe auch vor jedem verschachtelten Test mit. Die Trennung hält ihn dort,
 * wo er gebraucht wird.
 *
 * <p><b>Warum der Zuhörer namenlos angemeldet wird:</b> {@code propList} sammelt über
 * {@code addPropertyChangeListener(listener)}, also ohne Ereignisnamen. Dadurch sieht er
 * <em>alle</em> Meldungen in der Reihenfolge, in der sie gefeuert wurden. Erst das macht
 * {@code propList.size()} zu einer sinnvollen Zusicherung: Ein benannter Zuhörer bekäme die
 * übrigen Meldungen gar nicht zu sehen und könnte eine zweite, ungewollte nicht entdecken.
 *
 * <p><b>Was gemockt wird und was nicht:</b> Repositories sind Mitarbeiter mit Außenkontakt
 * und werden ersetzt. {@code istGueltigesKennzeichen} ist reine Logik ohne Nebenwirkung und
 * gehört zur geprüften Einheit — wer sie stubbt, prüft am Ende seinen Stub.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FahrzeugServiceTest")
class FahrzeugServiceTest
{

    @Mock
    private FahrzeugRepository fahrzeugRepository;
    @Mock
    private GarageRepository garageRepository;

    private FahrzeugService fahrzeugService;

    public final List<PropertyChangeEvent> propList = new ArrayList<>();

    @BeforeEach
    void aufbau()
    {
        PropertyChangeSupport support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(propList::add);

        fahrzeugService = new FahrzeugService(fahrzeugRepository,garageRepository,new PropertyChangeHandle(support));
    }

    private PropertyChangeEvent getProp()
    {
        assertFalse(propList.isEmpty(), "es wurde gar keine Property gemeldet");
        return propList.getLast();
    }

    /**
     * Fehlende Typangabe wird als eigener Fall gemeldet, nicht als Formatfehler.
     *
     * <p>{@code null} entsteht in der Oberfläche, wenn in der Typ-Auswahlliste nichts
     * ausgewählt ist. Die Prüfung steht deshalb ganz vorn in der Methode — vor der
     * Formatprüfung und vor jedem Datenbankzugriff. Das Kennzeichen ist hier bewusst
     * gültig: Sonst bliebe offen, welche der beiden Prüfungen zuerst greift.
     */
    @Test
    @DisplayName("Registration Typ Fehlt")
    void regTypFehlt()
    {
        fahrzeugService.fahrzeugRegistrieren("TT-TS 61",null,true);
        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(TYPANGABE_FEHLT.name(),getProp().getPropertyName(), "Typangabe fehlt fehler wurde angezeigt.");
        verifyNoInteractions(garageRepository);
        verifyNoInteractions(fahrzeugRepository);
    }

    /**
     * Ein bereits registriertes Kennzeichen wird im Adminbereich gemeldet, nicht überschrieben.
     *
     * <p>Das {@code admin}-Kennzeichen steht auf {@code true}, weil die Rückmeldung nur dort
     * erwartet wird: Kommt die Registrierung aus dem Hauptfenster, läuft sie still durch.
     *
     * <p>{@code verifyNoInteractions(garageRepository)} hält fest, dass die Registrierung mit
     * dem Parkhaus selbst nichts zu tun hat — hier wird ausschließlich das Fahrzeugregister
     * berührt.
     */
    @Test
    @DisplayName("Registration vorhanden")
    void regVorhanden()
    {
        String nummernschild = "TT-TS 61";
        String typ = "Auto";

        when(fahrzeugRepository.existsById(nummernschild)).thenReturn(true);

        fahrzeugService.fahrzeugRegistrieren(nummernschild,typ,true);

        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(SCHON_REGISTRIERT.name(),getProp().getPropertyName(), "Es wird angezeigt, dass das Fahrzeug bereits" +
                " registriert ist");

        verifyNoInteractions(garageRepository);
    }

    /**
     * Ein unbekanntes Kennzeichen wird aufgenommen und gemeldet.
     *
     * <p>Für {@code existsById} steht hier <b>kein</b> Stub: Mockito liefert bei einer
     * {@code boolean}-Methode von sich aus {@code false}, und genau das ist die gewünschte
     * Ausgangslage. Ein Stub mit {@code thenReturn(false)} würde dasselbe bewirken und wäre
     * nur Rauschen.
     */
    @Test
    @DisplayName("Registration durchgeführt")
    void registration()
    {
//        when(fahrzeugRepository.existsById("TT-TS 61")).thenReturn(false);
//        überflüssig, weil default ist false
        fahrzeugService.fahrzeugRegistrieren("TT-TS 61","Auto",true);
        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(REGISTRIERT.name(),getProp().getPropertyName(),"Es wird angezeigt, dass das" +
                " Fahrzeug registriert wurde.");
        verify(fahrzeugRepository).save(new Fahrzeug("TT-TS 61", "Auto"));
        verifyNoInteractions(garageRepository);
    }

    /**
     * Ein nicht geparktes Fahrzeug wird aus dem Register entfernt.
     *
     * <p>Entschieden wird allein an der Zahl, die {@code deleteBy...} zurückgibt: Eine
     * getroffene Zeile bedeutet gelöscht. Deshalb ist das der einzige Stub, den dieser Test
     * braucht — dass das Fahrzeug nicht parkt, ist bereits Mockitos Voreinstellung
     * {@code false}.
     */
    @Test
    @DisplayName("Löschen erfolgreich.")
    void loeschenErfolg()
    {
        String nummernschild = "TT-TS 61";
        String typ = "Auto";
//      s.O  when(garageRepository.existsByFahrzeug_Nummernschild(nummernschild)).thenReturn(false);
        when(fahrzeugRepository.deleteByNummernschildAndTyp(nummernschild, typ)).thenReturn(1);

        fahrzeugService.loescheFahrzeug(nummernschild,typ);
        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(FAHRZEUG_GELOESCHT.name(),getProp().getPropertyName(),"Bestätigung der erfolgreichen Löschung");

    }

    /**
     * Trifft das Löschen keine Zeile, wird das als Misserfolg gemeldet.
     *
     * <p>Dieser Zweig deckt <b>zwei</b> Ursachen ab: Das Kennzeichen ist gar nicht
     * registriert, oder es ist registriert, aber mit einem anderen Typ. Seit beide dieselbe
     * Meldung erzeugen, unterscheidet der Service sie nicht mehr — die frühere Abfrage mit
     * {@code existsById} war damit überflüssig geworden und ist entfallen.
     *
     * <p>Der Test kommt ohne jeden Stub aus: Beide Voreinstellungen ({@code false} fürs
     * Parken, {@code 0} für die gelöschten Zeilen) ergeben genau diese Ausgangslage.
     */
    @Test
    @DisplayName("Löschen fehlgeschlagen")
    void loeschenFehler()
    {
        String nummernschild = "TT-TS 61";
        String typ = "Auto";

        fahrzeugService.loescheFahrzeug(nummernschild,typ);
        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(FAHRZEUG_LOESCHEN_FEHLGESCHLAGEN.name(),getProp().getPropertyName(),"Benachrichtigung, dass das Löschen" +
                "fehlgeschlagen ist.");

    }

    /**
     * Ein Fahrzeug, das im Parkhaus steht, darf nicht aus dem Register verschwinden.
     *
     * <p>Sonst stünde ein Auto auf einem Platz, zu dem es keinen Eintrag mehr gäbe. Die
     * Abfrage im Parkhaus steht deshalb vor dem Löschversuch, und der Benutzer bekommt den
     * Hinweis, das Fahrzeug zuerst auszuchecken.
     */
    @Test
    @DisplayName("Löschen verboten")
    void loeschenVerboten()
    {
        String nummernschild = "TT-TS 61";

        when(garageRepository.existsByFahrzeug_Nummernschild(nummernschild)).thenReturn(true);

        fahrzeugService.loescheFahrzeug(nummernschild,"Auto");
        assertEquals(1,propList.size(),"Event wurde gefeuert");
        assertEquals(LOESCHEN_VERBOTEN.name(),getProp().getPropertyName(),"Hinweis, dass das Fahrzeug" +
                "nicht ausgechekt wurde, und daher nicht gelöscht werden kann.");
    }

    /**
     * Prüft ausschließlich {@code istGueltigesKennzeichen}, den statischen Formatprüfer.
     *
     * <p>Diese Gruppe braucht weder Mocks noch ein Service-Objekt: Die Methode ist static
     * und nebenwirkungsfrei, sie meldet nichts an die Oberfläche, sondern gibt ein Ergebnis
     * zurück. Genau deshalb steht sie in einer eigenen verschachtelten Klasse — so läuft der
     * Aufbau der äußeren Klasse nicht vor jedem dieser Fälle mit.
     *
     * <p><b>Auswahl der Fälle:</b> nicht jede Kombination, sondern je Regel des regulären
     * Ausdrucks ein Vertreter plus die beiden Ränder. Das vollständige Kreuzprodukt wären 24
     * Fälle, die alle dieselben drei Quantifier prüfen — die Teile des Ausdrucks sind
     * voneinander unabhängig, eine Kombination bewiese also nichts Zusätzliches. Ein
     * Zählfehler um eins zeigt sich ausschließlich am Rand; in der Mitte verhalten sich alle
     * Fassungen gleich.
     */
    @Nested
    @DisplayName("Kennzeichenprüfung")
    class KennzeichenPruefungTest
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
            assertTrue(istGueltigesKennzeichen(nummernschild),
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
            assertFalse(istGueltigesKennzeichen(nummernschild),
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
            assertTrue(istGueltigesKennzeichen("hh-aa 123"));
        }

    }

}
