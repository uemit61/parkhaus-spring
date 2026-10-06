package de.yildirim.parkhaus.model.service;

import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.entity.Garage;
import de.yildirim.parkhaus.model.entity.Parketage;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.repository.FahrzeugRepository;
import de.yildirim.parkhaus.model.repository.GarageRepository;
import de.yildirim.parkhaus.model.repository.ParketageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Prüft die Fachlogik von {@link GarageService}: Einfahrt, Ausfahrt, Positionsabfrage
 * und die Tabelle der belegten Plätze.
 *
 * <p><b>Unit-Ebene.</b> Kein Spring, kein Kontext, keine Datenbank — der Service wird mit
 * {@code new} erzeugt und bekommt seine Mitspieler von Hand hereingereicht. Dass das
 * genügt, ist ein Qualitätsmerkmal: Ließe sich die Fachlogik nur über den Container
 * prüfen, wäre sie zu eng ans Rahmenwerk gebunden.
 *
 * <p><b>Was hier NICHT geprüft wird.</b> Der reguläre Ausdruck des Kennzeichens liegt in
 * {@link FahrzeugServiceTest}, die abgeleiteten Abfragen liegen in den Repository-Tests.
 * Hier geht es ausschließlich um die Frage: <em>Welche Meldung geht bei welcher Lage an
 * die View, und welcher Zugriff findet dabei statt?</em>
 *
 * <p><b>Vier Mocks, ein echtes Objekt.</b> Die drei Repositories und {@link FahrzeugService}
 * sind Platzhalter — sie antworten auf Zuruf, statt Daten zu durchsuchen. Der Ereignis-Kanal
 * {@link PropertyChangeHandle} ist dagegen <em>echt</em>: Seine Verteilung nach Ereignisnamen
 * ist Produktionslogik, die mitlaufen soll. Ein Mock an dieser Stelle würde genau das
 * überspringen, was die Meldung beim richtigen Empfänger ankommen lässt.
 *
 * <p>Deshalb auch kein {@code @InjectMocks} — das würde den Kanal mitmocken, und der
 * mithörende Listener bekäme nie etwas zu sehen.
 *
 * <p><b>Der Listener wird namenlos angemeldet.</b> {@code PropertyChangeHandle} reicht nur
 * die benannte Überladung nach außen; über sie müsste man sich für jeden der Ereignisnamen
 * einzeln anmelden. Darum entsteht der {@link PropertyChangeSupport} hier im Test, der
 * sammelnde Listener hängt sich direkt an ihn, und erst danach wird der Kanal darum gebaut.
 * {@code propList} zeichnet damit <em>jedes</em> Ereignis auf, in der Reihenfolge seines
 * Eintreffens.
 *
 * <p><b>Stubs stehen im Test, nicht im Aufbau.</b> {@code MockitoExtension} arbeitet mit
 * {@code STRICT_STUBS} und meldet nach jeder Testmethode, wenn eine Stubbing-Anweisung
 * ungenutzt blieb. Das ist kein Hindernis, sondern eine Aussage: Dass in
 * {@code befahren()} je nach Lage nur ein Teil der Bedingung ausgewertet wird, macht die
 * Strenge sichtbar. Ein gemeinsamer Stub im Aufbau wäre hier ohnehin nicht möglich — die
 * Fälle unterscheiden sich gerade in dem Wert, den er liefern müsste.
 *
 * <p><b>Nebenbefund:</b> {@code istGueltigesKennzeichen} ist {@code static} und wird von
 * Mockito nicht ersetzt. Die Formatprüfung läuft also mit der echten Implementierung, auch
 * wenn {@code fahrzeugService} ein Mock ist — ein ungültiges Kennzeichen braucht deshalb
 * keinerlei Vorbereitung.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@ExtendWith(MockitoExtension.class)
public class GarageServiceTest
{
    @Mock
    private GarageRepository garageRepository;
    @Mock
    private ParketageRepository parketageRepository;
    @Mock
    private FahrzeugRepository fahrzeugRepository;
    @Mock
    private FahrzeugService fahrzeugService;

    private GarageService garageService;

    public final List<PropertyChangeEvent> propList = new ArrayList<>();


    @BeforeEach
    void aufbau()
    {
        PropertyChangeSupport support = new PropertyChangeSupport(this);
        support.addPropertyChangeListener(propList::add);

        garageService = new GarageService(garageRepository, parketageRepository, fahrzeugRepository, new PropertyChangeHandle(support), fahrzeugService);


    }

    /**
     * Das zuletzt aufgezeichnete Ereignis — spart die Wiederholung in jedem Test.
     *
     * <p>Die Zusicherung davor ist Absicht: Ist nichts gemeldet worden, soll der Test mit
     * einem lesbaren Satz scheitern und nicht mit einer {@code NoSuchElementException}.
     *
     * <p>Reicht nur für Methoden, die <em>eine</em> Meldung feuern. Wo zwei kommen, greifen
     * die Tests direkt auf {@code propList} zu — nur so lässt sich auch ihre Reihenfolge
     * festnageln.
     *
     * @return das letzte Ereignis aus {@code propList}
     */
    private PropertyChangeEvent getProp()
    {
        assertFalse(propList.isEmpty(), "es wurde gar keine Property gemeldet");
        return propList.getLast();
    }

    // ---------------------------------------------------------------------------
    // verlassen() - drei Zweige, drei Testmethoden.
    //
    // Entschieden wird allein an der Zahl, die deleteBy... zurueckgibt: 1 bedeutet
    // ausgetragen, alles andere nicht gefunden. Die drei Faelle unterscheiden sich
    // deshalb GENAU in diesem Wert - und lassen sich darum nicht ueber einen
    // gemeinsamen Stub im Aufbau abbilden.
    // ---------------------------------------------------------------------------
    /**
     * Ungültiges Kennzeichen: Es wird gemeldet, und die Datenbank bleibt unberührt.
     *
     * <p>Kein Stub nötig — die Methode kehrt vor dem Repository-Zugriff zurück. Ein Stub
     * bliebe ungenutzt und ließe den Test an {@code STRICT_STUBS} scheitern.
     *
     * <p>{@code verifyNoInteractions} am Ende ist hier der eigentliche Inhalt: Die Zusicherung
     * lautet nicht nur „es kommt FailCheck", sondern „die Prüfung steht <em>vor</em> dem
     * Zugriff". Ohne sie bliebe der Test grün, wenn jemand das Löschen vor die Formatprüfung
     * zöge. Die Zeile muss dafür hinter dem Aufruf stehen — davor wäre sie immer wahr.
     */
    @Test
    @DisplayName("Fahrzeug verlässt Parkhaus, ein" + " ungültiges Kennzeichen wird eingegeben")
    void verlassenFailCheck()
    {

        // Falsches Kennzeichen
        String nummernschild = "AB-CDEF 123456";
        String typ = "Auto";

        garageService.verlassen(nummernschild, typ);
        // Zunächst wird getestet, ob eine Meldung vorliegt
        assertEquals(1, propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("FailCheck", getProp().getPropertyName(), "\"" + nummernschild + "\"- ein ungültiges sollte die gewünschte Meldung" + "senden");
        verifyNoInteractions(garageRepository);
    }


    /**
     * Erfolgreiche Ausfahrt — und zugleich der Nachweis der Normalisierung.
     *
     * <p><b>Die Kleinschreibung in der Eingabe ist Absicht, bitte nicht „aufräumen".</b>
     * Der Stub steht bewusst auf dem großgeschriebenen Kennzeichen: Nur wenn
     * {@code verlassen()} vorher {@code toUpperCase()} anwendet, greift er und liefert die 1.
     * Entfiele die Normalisierung, träfe der Stub nicht mehr, der Mock lieferte seinen
     * Vorgabewert 0, und der Test meldete „Fail" statt „Verlassen".
     *
     * <p>Der Fall gehört hierher und nicht in {@code verlassenFail}: Dort führen beide Wege —
     * mit und ohne Normalisierung — zur selben 0, der Test würde die Regression nicht bemerken.
     *
     * <p>Die letzte Zusicherung prüft dieselbe Regel an der zweiten Stelle, an der sie zählt:
     * im Wert, den die View zu sehen bekommt.
     */
    @Test
    @DisplayName("Fahrzeug verlässt Parkhaus" + " mit einem gültigen Kennzeichen, kleinschreibung wird normalisiert.")
    void verlassenVerlassen()
    {
        when(garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ("TT-TS 61", "Auto")).thenReturn(1);
        garageService.verlassen("tt-ts 61", "Auto");
        assertEquals(1, propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Verlassen", getProp().getPropertyName(), "Sollte 'Verlassen' melden");
        assertEquals("TT-TS 61", getProp().getNewValue(), "Meldung trägt das normalisierte Kennzeichen");
    }

    /**
     * Gültiges Kennzeichen, aber keine passende Belegung.
     *
     * <p>Die 0 ist zugleich Mockitos Vorgabewert für {@code int} — der Stub wäre also
     * entbehrlich. Er steht trotzdem da, weil er die <em>Voraussetzung</em> des Falls
     * ausspricht („das Fahrzeug parkt nicht"), statt sie dem Vorgabewert zu überlassen.
     * Ein stiller Test hinge daran, dass ein künftiger Leser Mockitos Vorgabewerte kennt.
     */
    @Test
    @DisplayName("Nicht parkendes Fahrzeug mit einem gültigen " + "Kennzeichen, soll ausgecheckt werden")
    void verlassenFail()
    {
        when(garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ("TT-TS 34", "Auto")).thenReturn(0);
        garageService.verlassen("TT-TS 34", "Auto");
        assertEquals(1, propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Fail", getProp().getPropertyName(), "\"TT-TS 34\"-Sollte eine Fehlermeldung anzeigen");
    }



    /**
     * Ungültiges Kennzeichen: gemeldet, und es wird nichts nachgeschlagen.
     *
     * <p>Dieselbe Prüfung wie in {@code verlassen()} — aber ein eigener Test, weil es eine
     * eigene {@code if}-Abfrage ist. Die drei Methoden wissen nichts voneinander; wird die
     * Prüfung aus einer entfernt, merken es die Tests der anderen beiden nicht.
     *
     * <p>Geschützt wird hier ein <em>Lesezugriff</em>, nicht wie bei {@code verlassen()} ein
     * Löschvorgang. Der Schaden wäre geringer, die Regel ist dieselbe: erst prüfen, dann
     * zugreifen.
     *
     * <p>Die Nutzlast wird mitgeprüft — „FailCheck" trägt das Kennzeichen zurück an die View,
     * die es in ihrer Meldung anzeigt.
     */
    @Test
    @DisplayName("Die Position des falschen Kennzeichens führt zum Fehler")
    void zeigePositionFailCheck()
    {

        // Falsches Kennzeichen
        String nummernschild = "AB-CDEF 123456";


        garageService.zeigePosition(nummernschild);
        // Zunächst wird getestet, ob eine Meldung vorliegt
        assertEquals(1, propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("FailCheck", getProp().getPropertyName(), "\"" + nummernschild + "\"- ein ungültiges sollte die gewünschte Meldung" + "senden");
        assertEquals(nummernschild,getProp().getNewValue(), "Sollte das falsche Kennzeichen weiterleiten");
        verifyNoInteractions(garageRepository);
    }

    /**
     * Das Fahrzeug parkt: Typ, Kennzeichen, Etage und Platz gehen an die View.
     *
     * <p>Hier ist die <em>Nutzlast</em> der Prüfgegenstand, nicht der Ereignisname. Die
     * Methode berechnet nichts, aber sie stellt vier Werte in einer festen Reihenfolge
     * zusammen — und genau die kann man vertauschen, ohne dass etwas abstürzt.
     *
     * <p>Deshalb sind die Testdaten so gewählt, dass sich die Zahlen nicht verwechseln lassen:
     * Etage 61 gegen Platz 1. Stünden dort zweimal ähnliche Werte, bliebe ein vertauschtes
     * Paar unentdeckt.
     *
     * <p><b>Grenze dieses Tests:</b> Die Erwartung wird aus demselben {@code Garage}-Objekt
     * gebaut, das der Mock zurückgibt. Nachgewiesen ist damit die <em>Auswahl und Reihenfolge</em>
     * der vier Felder — nicht, dass die Werte aus der richtigen Quelle stammen.
     */
    @Test
    @DisplayName("Es wird die Position eines parkenden Fahrzeugs gemeldet")
    void zeigePositionEinesParkendenFahrzeugs()
    {
        String nummernschild = "TT-TS 61";
        List <Garage> garageList = new ArrayList<>();
        garageList.add(new Garage(1,new Parketage(61,611),new Fahrzeug("TT-TS 61","Auto")));
        when(garageRepository.findByFahrzeug_Nummernschild(nummernschild)).thenReturn(garageList);


        garageService.zeigePosition(nummernschild);
        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");

        Garage garage = garageList.getFirst();
        List<Object> position = new ArrayList<>(Arrays.asList(garage.getFahrzeug().getTyp(),nummernschild , garage.getParketage().getEtageNr(), garage.getPlatzNr()));

        assertEquals("ZeigePos",getProp().getPropertyName(),"Sollte 'zeigePosition' melden");
        assertEquals(position,getProp().getNewValue(), "Sollte das entsprechende Objekt position feuern");

    }

    /**
     * Gültiges Kennzeichen, aber das Fahrzeug steht nicht im Parkhaus.
     *
     * <p>Die leere Liste ist zugleich Mockitos Vorgabewert für {@code List} — abgeleitete
     * Abfragen liefern nie {@code null}, und der Mock hält sich daran. Der Stub wäre also
     * entbehrlich und steht trotzdem da, weil er die Voraussetzung des Falls ausspricht:
     * <em>es gibt keine Belegung zu diesem Kennzeichen</em>.
     *
     * <p>Kein {@code verifyNoInteractions} hier — anders als im FailCheck-Fall wird das
     * Repository in diesem Zweig sehr wohl gefragt. Das ist der Unterschied zwischen
     * „durfte nicht nachsehen" und „hat nachgesehen und nichts gefunden".
     */
    @Test
    @DisplayName("Nichtparkende Kennzeichens meldet Fehler")
    void zeigePositionNichtparkendenFehler()
    {
        String nummernschild = "TT-TS 61";
        List  <Garage> garageList = new ArrayList<>();
        when(garageRepository.findByFahrzeug_Nummernschild("TT-TS 61")).thenReturn(garageList);

        garageService.zeigePosition("TT-TS 61");
        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Fail",getProp().getPropertyName(),"Sollte Fail melden");
        assertEquals(nummernschild,getProp().getNewValue(), "Sollte das falsche Kennzeichen weiterleiten");
    }

    //Methode befahren() testen

    /**
     * Ungültiges Kennzeichen bei der Einfahrt: gemeldet, ohne jeden Zugriff.
     *
     * <p>Die dritte Stelle mit derselben Prüfung — und die mit dem höchsten Einsatz. In der
     * Alarmbedingung von {@code befahren()} steht {@code fahrzeugRegistrieren}, und das
     * <em>schreibt</em>. Wanderte die Formatprüfung dahinter, landete jede Fehleingabe als
     * Fahrzeug in der Datenbank.
     *
     * <p>Genau deshalb kommt die Methode hier gar nicht erst bis zur Bedingung: Sie meldet
     * und kehrt zurück, bevor irgendein Mitspieler gefragt wird.
     */
    @Test
    @DisplayName("Falsches Kennzeichen führt zum Fehler")
    void befahrenFalschesKennzeichenFehler()
    {
        // Falsches Kennzeichen
        String nummernschild = "AB-CDEF 123456";
        String typ = "Auto";
        garageService.befahren(nummernschild, typ);
        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("FailCheck", getProp().getPropertyName(), "Sollte 'FailCheck' melden");
        assertEquals(nummernschild,getProp().getNewValue(),"Sollte das ungültige Kennzeichen melden");
        verifyNoInteractions(garageRepository);
        verifyNoInteractions(fahrzeugService);
    }

    /**
     * Das Fahrzeug parkt bereits: Alarm, erster von zwei Wegen dorthin.
     *
     * <p>Nur ein einziger Stub, und das ist kein Versehen. Die Alarmbedingung lautet
     * {@code A || (!B && !C)}; ist A wahr, wird der rechte Teil durch den Kurzschluss nie
     * ausgewertet. Stubs für B und C blieben ungenutzt — {@code STRICT_STUBS} macht genau
     * das sichtbar.
     *
     * <p>Fachlich steckt darin mehr als eine Spitzfindigkeit: B ist
     * {@code fahrzeugRegistrieren}, und das <em>schreibt</em>. Dass ein bereits geparktes
     * Fahrzeug nicht noch einmal eingetragen wird, hängt allein an dieser Auswertungsreihenfolge.
     */
    @Test
    @DisplayName("Parkendes Kennzeichen, will die Garage befahren, " + "löst ein Alarm aus")
    void befahrenParkendenKennzeichen()
    {
        String nummernschild = "TT-TS 61";
        String typ = "Auto";
        when(garageRepository.existsByFahrzeug_Nummernschild(nummernschild)).thenReturn(true);

        garageService.befahren(nummernschild,typ);

        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Alarm",getProp().getPropertyName(),"Sollte 'Alarm' melden");

    }

    /**
     * Bekanntes Kennzeichen mit abweichendem Fahrzeugtyp: Alarm, zweiter Weg dorthin.
     *
     * <p>Das ist die Fachregel aus v4.1 — der Fahrzeugtyp gehört zur Identität eines
     * Fahrzeugs. Ein bekanntes Kennzeichen, das mit einem anderen Typ erscheint, bekommt
     * keinen Platz, sondern löst Alarm aus.
     *
     * <p>Kein einziger Stub: Alle drei Bedingungen liefern {@code false}, und {@code false}
     * ist Mockitos Vorgabewert für {@code boolean}. Aus {@code A || (!B && !C)} wird damit
     * {@code false || (true && true)} — Alarm.
     *
     * <p><b>Und hier steht bewusst kein {@code verifyNoInteractions}</b>, anders als in den
     * FailCheck-Tests: In diesem Fall <em>wird</em> das Repository gefragt, A ist der erste
     * Operand der Bedingung. „Keine Interaktion" wäre schlicht unwahr.
     */
    @Test
    @DisplayName("Registriertes Fahrzeug mit falschem Typ befährt das Parkhaus")
    void befahrenParkendenFahrzeugFalschenTyp()
    {
        String nummernschild = "TT-TS 61";
        String typ = "Auto";
        garageService.befahren(nummernschild,typ);
        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Alarm",getProp().getPropertyName(),"Sollte 'Alarm' melden");
        
    }


    /**
     * Alle Plätze belegt: „Voll" statt einer Platzvergabe.
     *
     * <p>Die Testdaten sind bewusst winzig — zwei Etagen mit je einem Platz, beide belegt:
     * <pre>
     *   belegte Plätze : 1, 2        → die Suche nach dem kleinsten freien ergibt 3
     *   Kapazitäten    : 1 + 1 = 2   → 3 passt in keine Etage
     * </pre>
     * Damit bleibt {@code gefundeneEtage} null, und genau das ist der Voll-Fall. Mit den
     * echten Stammdaten (20 + 120 + 120 Plätze) bräuchte derselbe Test 260 Einträge.
     *
     * <p>Die {@code Sort}-Argumente werden mitgestubbt und damit mitgeprüft: {@code Sort}
     * hat ein sinnvolles {@code equals}, ein anderes Sortierkriterium ließe die Stubs ins
     * Leere laufen. Die Sortierung ist keine Kosmetik — beide Schleifen setzen aufsteigende
     * Reihenfolge voraus.
     */
    @Test
    @DisplayName("Fahrzeug will das volle Parkhaus befahren")
    void befahrenParkhausVoll()
    {
        //Zwei Etagen, pro Etage ein Parkplatz. Zwei Fahrzeuge die parken.
        Fahrzeug fahrzeugOne = new Fahrzeug("TT-TS 61","Auto");
        Parketage parketageOne =    new Parketage(1,1);
        Garage garageOne = new Garage(1,parketageOne,fahrzeugOne);

        Fahrzeug fahrzeugTwo = new Fahrzeug("TT-TS 7","Motorrad");
        Parketage parketageTwo =    new Parketage(2,1);
        Garage garageTwo = new Garage(2,parketageTwo,fahrzeugTwo);

        List<Garage> garageList = new ArrayList<>(Arrays.asList(garageOne,garageTwo));
        List<Parketage> parketageList = new ArrayList<>(Arrays.asList(parketageOne,parketageTwo));


        when(garageRepository.findAll(Sort.by("platzNr"))).thenReturn(garageList);
        when(parketageRepository.findAll(Sort.by("etageNr"))).thenReturn(parketageList);

        //Ein Fahrzeug will das volle Parkhaus befahren.
        String nummernschild = "TT-TS 6161";
        String typ = "Auto";
        when(fahrzeugService.fahrzeugRegistrieren(nummernschild,typ,false)).thenReturn(true);
        garageService.befahren(nummernschild, typ);

        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("Voll",getProp().getPropertyName(),"Sollte 'Voll' melden");
    }

    /**
     * Erfolgreiche Einfahrt: kleinster freier Platz und die dazu passende Etage.
     *
     * <p>Der wichtigste Test der Klasse, weil hier die einzige echte Rechnung steckt —
     * alles andere ist Weiterleitung:
     * <pre>
     *   belegt      : Platz 1                         → kleinster freier Platz = 2
     *   Kapazitäten : Etage 1 → 1 Platz, Etage 2 → 1 Platz
     *   aufsummiert : nach Etage 1 sind es 1, nach Etage 2 sind es 2
     *   Platz 2     : passt erstmals in Etage 2
     * </pre>
     *
     * <p>Die Etage steht also nirgends im Code, sie ergibt sich aus den Kapazitäten. Ändert
     * sich eine Platzzahl, genügt eine Migration — und dieser Test ist die Stelle, an der
     * sich diese Rechnung nachprüfen lässt.
     *
     * <p>Geprüft wird die vollständige Nutzlast, nicht nur der Ereignisname: Ein vertauschtes
     * Paar aus Etage und Platz käme sonst durch, und die View schickte den Benutzer in die
     * falsche Etage.
     */
    @Test
    @DisplayName("Fahrzeug mit gültigem Kennzeichen befährt das frei Parkhaus.")
    void befahrenZeigePos()
    {
        Fahrzeug fahrzeugOne = new Fahrzeug("TT-TS 61","Auto");
        Parketage parketageOne =    new Parketage(1,1);
        Garage garageOne = new Garage(1,parketageOne,fahrzeugOne);

        Parketage parketageTwo =    new Parketage(2,1);


        List<Garage> garageList = new ArrayList<>(Arrays.asList(garageOne));
        List<Parketage> parketageList = new ArrayList<>(Arrays.asList(parketageOne,parketageTwo));

        when(garageRepository.findAll(Sort.by("platzNr"))).thenReturn(garageList);
        when(parketageRepository.findAll(Sort.by("etageNr"))).thenReturn(parketageList);


        String nummernschild = "TT-TS 6161";
        String typ = "Auto";
        when(fahrzeugService.fahrzeugRegistrieren(nummernschild,typ,false)).thenReturn(true);
        garageService.befahren(nummernschild, typ);

        //Da 2 Etagen je ein Platz sollte dem zweiten Fahrzeug  PlatzNr: 2 und Etage: 2 zugewiesen werden
        List<Object> viewInfo = new ArrayList<>(Arrays.asList(typ, nummernschild, 2,2));

        assertEquals(1,propList.size(), "genau eine Meldung wird erwartet");
        assertEquals("ZeigePos",getProp().getPropertyName(),"Sollte 'ZeigePos' melden");
        assertEquals(viewInfo,getProp().getNewValue(),"Sollte viewInfo feuern");
    }

    /**
     * Die einzige Methode, die <em>zwei</em> Meldungen feuert.
     *
     * <p>Deshalb greift dieser Test über {@code getFirst()} und {@code getLast()} direkt auf
     * {@code propList} zu statt über den Helfer {@code getProp()}: Zusammen mit der
     * Größenprüfung nagelt das beide Namen <em>und ihre Reihenfolge</em> fest. Die ist
     * fachlich bedeutsam — erst kommen die Daten („TabAn"), dann schaltet die Oberfläche um
     * („PanelTabelle"). Andersherum zeigte sie kurz die alte Tabelle.
     *
     * <p>Die Liste wird mit {@code assertEquals} geprüft und nicht mit {@code assertSame}:
     * Zugesichert ist der <em>Inhalt</em>, den die View bekommt, nicht die Objektidentität.
     * Ein späteres {@code List.copyOf(...)} wäre eine sinnvolle Änderung und dürfte diesen
     * Test nicht brechen.
     *
     * <p>Hier steht {@code findAll()} ohne {@code Sort} — anders als in {@code befahren()}.
     * Die Tabelle wird angezeigt, nicht durchlaufen; eine Reihenfolge wird nicht vorausgesetzt.
     */
    @Test
    @DisplayName("Die Garage wird als List an die View gefeuert")
    void parkplatzTabelle()
    {
        Fahrzeug fahrzeugOne = new Fahrzeug("TT-TS 61","Auto");
        Parketage parketageOne =    new Parketage(1,1);
        Garage garageOne = new Garage(1,parketageOne,fahrzeugOne);

        Fahrzeug fahrzeugTwo = new Fahrzeug("TT-TS 7","Motorrad");
        Parketage parketageTwo =    new Parketage(2,1);
        Garage garageTwo = new Garage(2,parketageTwo,fahrzeugTwo);

        List<Garage> garageList = new ArrayList<>(Arrays.asList(garageOne,garageTwo));

        when(garageRepository.findAll()).thenReturn(garageList);

        garageService.parkplatzTabelle();

        assertEquals(2,propList.size(), "genau zwei Meldung werden erwartet");
        assertEquals("TabAn",propList.getFirst().getPropertyName(),"Sollte 'TabAn' melden");
        assertEquals(garageList, propList.getFirst().getNewValue(),"Sollte garageList feuern");
        assertEquals("PanelTabelle",propList.getLast().getPropertyName(),"Sollte 'PanelTabelle' melden");

    }

}
