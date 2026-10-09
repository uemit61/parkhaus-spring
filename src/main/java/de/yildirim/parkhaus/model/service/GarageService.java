package de.yildirim.parkhaus.model.service;

import de.yildirim.parkhaus.model.entity.Garage;
import de.yildirim.parkhaus.model.entity.Parketage;
import de.yildirim.parkhaus.model.event.Ereignis;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.repository.FahrzeugRepository;
import de.yildirim.parkhaus.model.repository.GarageRepository;
import de.yildirim.parkhaus.model.repository.ParketageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Fachlogik rund um die Belegung der Parkplätze: Einfahrt, Ausfahrt, Position
 * eines Fahrzeugs und die Tabelle aller belegten Plätze.
 *
 * <p>Die Klasse entscheidet, <em>was</em> der Fall ist, und meldet das Ergebnis
 * über den Ereignis-Kanal. <em>Wie</em> es dem Benutzer gezeigt wird, entscheidet
 * die View. SQL steht hier keines — dafür sind die Repositories da.
 *
 * <p><b>Normalisierung:</b> Jede öffentliche Methode setzt das Kennzeichen als
 * Erstes auf Großbuchstaben, aus demselben Grund wie in {@link FahrzeugService}:
 * es ist der Primärschlüssel. Fehlt die Zeile auch nur an einer Stelle, bleibt
 * das auf MySQL unbemerkt und fällt erst auf einer Datenbank auf, die beim
 * Vergleich zwischen Groß- und Kleinschreibung unterscheidet.
 *
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Service
public class GarageService
{
    private final GarageRepository garageRepository;
    private final ParketageRepository parketageRepository;
    private final FahrzeugRepository fahrzeugRepository;
    private final PropertyChangeHandle pch;
    private final FahrzeugService fahrzeugService;

    public GarageService(GarageRepository garageRepository, ParketageRepository parketageRepository,
            FahrzeugRepository fahrzeugRepository, PropertyChangeHandle pch, FahrzeugService fahrzeugService)
    {
        this.garageRepository = garageRepository;
        this.parketageRepository = parketageRepository;
        this.fahrzeugRepository = fahrzeugRepository;
        this.pch = pch;
        this.fahrzeugService = fahrzeugService;
    }

    /**
     * Lässt ein Fahrzeug einfahren und weist ihm den kleinsten freien Platz zu.
     *
     * <p>Alarm gibt es in zwei Fällen: wenn ein bereits geparktes Fahrzeug noch
     * einmal einfahren will, und wenn ein bekanntes Kennzeichen mit einem anderen
     * Fahrzeugtyp erscheint als dem registrierten — dann ist eines von beiden
     * gefälscht. Ein noch unbekanntes Kennzeichen wird still registriert und fährt
     * regulär ein.
     *
     * <p>Achtung beim Lesen der Alarmbedingung: {@code fahrzeugRegistrieren} steht
     * mitten in einem {@code ||}-Ausdruck und <em>schreibt in die Datenbank</em>.
     * Ob das geschieht, hängt an der Kurzschluss-Auswertung — parkt das Fahrzeug
     * schon, wird der rechte Teil nie ausgewertet und nichts registriert. Genau so
     * ist es gewollt.
     *
     * @param nummernschild Kennzeichen des einfahrenden Fahrzeugs
     * @param typ           Fahrzeugtyp aus der Auswahlliste der Oberfläche
     */
    @Transactional
    public void befahren(String nummernschild, String typ)
    {
        nummernschild = nummernschild.toUpperCase();
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            if (garageRepository.existsByFahrzeug_Nummernschild(nummernschild) ||
                    (!fahrzeugService.fahrzeugRegistrieren(nummernschild, typ, false) &&
                            !fahrzeugRepository.existsByNummernschildAndTyp(nummernschild, typ)))
            {
                pch.propertyChange(Ereignis.ALARM, null);
            }
            else
            {
                // Beide Suchen setzen aufsteigende Sortierung zwingend voraus - deshalb
                // das Sort.by(). findAll() ohne Sortierung sichert KEINE Reihenfolge zu;
                // dass MySQL meist nach Primärschlüssel liefert, ist Zufall, kein Versprechen.
                List<Integer> platzNrListe = garageRepository.findAll(Sort.by("platzNr")).stream().map(Garage::getPlatzNr).toList();

                // Gestreamt werden die Indizes, nicht die Platznummern: gesucht ist der erste
                // Index, dessen Platznummer nicht um eins größer ist als er selbst. Bei
                // [1,2,4,5,...] ist das Index 2 - Platz 3 ist frei. Wurde keine Nummer
                // übersprungen, liefert orElse die nächste Nummer hinten.
                //
                // "idx+1 != platzNrListe.get(idx)" vergleicht Zahlen und nicht Referenzen -
                // aber nur, weil idx aus dem IntStream ein int ist: dadurch wird die
                // Integer-Platznummer ausgepackt. Stünden dort zwei Integer, ginge der
                // Vergleich bis 127 gut, weil der Integer-Cache für kleine Werte dieselbe
                // Instanz liefert. Ab Platz 128 wären es zwei Objekte, die gleich sind, aber
                // nicht dasselbe - bei 260 Plätzen also mitten im Betrieb. Kompiliert sauber,
                // tut etwas anderes.
                int i = IntStream.range(0,platzNrListe.size())
                        .filter(idx -> idx+1!=platzNrListe.get(idx))
                        .map(idx -> idx+1)
                        .findFirst()
                        .orElse(platzNrListe.size()+1);


                List<Parketage> parketageList = parketageRepository.findAll(Sort.by("etageNr"));

                // Die Etage steht nicht im Code: die Kapazitäten werden der Reihe nach
                // aufsummiert (j), bis die gesuchte Platznummer hineinfällt - die erste
                // Etage, in die i noch passt, ist die gesuchte. Bleibt gefundeneEtage
                // null, hat keine Etage mehr Platz: Das Parkhaus ist voll. Ändert sich
                // eine Kapazität, genügt ein UPDATE an den Stammdaten.
                int j=0;
                Parketage gefundeneEtage = null;
                for (Parketage parketage : parketageList)
                {
                    j = j + parketage.getAnzahlPlaetze();
                    if (i <= j)
                    {
                        gefundeneEtage = parketage;
                        break;
                    }
                }

                //Alternativ mit Streams, läuft aber kostet: O(n²);

                //                  List<Integer> helpList = new ArrayList<>();
                //
                //                    int etageNr = IntStream.range(0,parketageList.size())
                //                            .filter(idx ->
                //                            {
                //                                helpList.add(parketageList.get(idx).getAnzahlPlaetze());
                //                                return i <=helpList.stream().mapToInt(b->b).sum();
                //                            })
                //                           .findFirst().orElse(-1);
                //                    Parketage gefundeneEtage =  etageNr == -1 ? null : parketageList.get(etageNr);

                if (gefundeneEtage==null)
                {
                    pch.propertyChange(Ereignis.PARKPLATZ_VOLL, null);
                }
                else
                {
                    // getReferenceById statt findById: Für das INSERT wird vom Fahrzeug
                    // nur der Fremdschlüssel gebraucht. Der Platzhalter trägt ihn bereits
                    // in sich, eine SELECT-Abfrage erübrigt sich. Die Etage muss gar nicht
                    // erst nachgeschlagen werden - das Objekt liegt schon vor.
                    garageRepository.save(new Garage(i, gefundeneEtage, fahrzeugRepository.getReferenceById(nummernschild)));

                    List<Object> viewInfo = new ArrayList<>(Arrays.asList(typ, nummernschild, gefundeneEtage.getEtageNr(),i));
                    pch.propertyChange(Ereignis.ZEIGE_POSITION, viewInfo);
                }
            }
        }
        else
        {
            pch.propertyChange(Ereignis.KENNZEICHEN_UNGUELTIG, nummernschild);
        }
    }

    /**
     * Trägt ein Fahrzeug wieder aus, wenn Kennzeichen und Fahrzeugtyp zusammenpassen.
     *
     * <p>Der Typ steht bewusst mit in der Bedingung: seit v4.1 gehört er zur Identität
     * eines Fahrzeugs. Ein bekanntes Kennzeichen, das mit einem anderen Typ erscheint,
     * darf nicht ausfahren.
     *
     * <p>Die abgeleitete Löschmethode liefert die Anzahl der betroffenen Zeilen — der
     * einzige Rückkanal, den Spring Data hier anbietet. Genau eine gelöschte Zeile ist
     * der Erfolgsfall; null heißt, dass es die Kombination aus Kennzeichen und Typ so
     * nicht gab.
     *
     * @param nummernschild Kennzeichen des ausfahrenden Fahrzeugs
     * @param typ           Fahrzeugtyp aus der Auswahlliste der Oberfläche
     */
    @Transactional
    public void verlassen(String nummernschild, String typ)
    {
        nummernschild = nummernschild.toUpperCase();
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {
            if (garageRepository.deleteByFahrzeug_NummernschildAndFahrzeug_Typ(nummernschild, typ) == 1)
                pch.propertyChange(Ereignis.PARKPLATZ_VERLASSEN, nummernschild);
            else
                pch.propertyChange(Ereignis.KENNZEICHEN_NICHT_GEFUNDEN, nummernschild);
        }
        else
        {
            pch.propertyChange(Ereignis.KENNZEICHEN_UNGUELTIG, nummernschild);
        }
    }

    /**
     * Sucht Etage und Platz zu einem Kennzeichen und meldet beides über "ZeigePos" an
     * die View.
     *
     * <p>Der JOIN über {@code garage} und {@code fahrzeug}, den die Swing-Fassung noch
     * als SQL-Text enthielt, steckt jetzt in der {@code @ManyToOne}-Beziehung: aus einer
     * {@link Garage} sind Fahrzeug und Etage unmittelbar erreichbar.
     *
     * @param nummernschild Kennzeichen des gesuchten Fahrzeugs
     */
    @Transactional(readOnly = true)
    public void zeigePosition(String nummernschild)
    {
        nummernschild = nummernschild.toUpperCase();
        if (FahrzeugService.istGueltigesKennzeichen(nummernschild))
        {

            List<Garage> garageList = garageRepository.findByFahrzeug_Nummernschild(nummernschild);

            if (!garageList.isEmpty())
            {
                Garage garage = garageList.getFirst();
                List<Object> position = new ArrayList<>(Arrays.asList(garage.getFahrzeug().getTyp(), nummernschild, garage.getParketage().getEtageNr(), garage.getPlatzNr()));
                pch.propertyChange(Ereignis.ZEIGE_POSITION, position);
            }
            else
                pch.propertyChange(Ereignis.KENNZEICHEN_NICHT_GEFUNDEN, nummernschild);
        }
        else
        {
            pch.propertyChange(Ereignis.KENNZEICHEN_UNGUELTIG, nummernschild);
        }
    }

    /**
     * Liest alle belegten Plätze und schickt sie an die View, die daraus ihre Tabelle
     * füllt.
     *
     * <p>Zwei Meldungen, weil zwei verschiedene Fenster zuhören: "TabAn" liefert die
     * Daten an {@code ParkplatzTabelle}, "PanelTabelle" lässt {@code ViewParkhaus} die
     * Ansicht umschalten. Der Service weiß von beiden nichts — er nennt nur die Namen.
     */
    @Transactional(readOnly = true)
    public void parkplatzTabelle()
    {
        List<Garage> garageListe = garageRepository.findAll();

        pch.propertyChange(Ereignis.PARKPLATZ_TABELLE, garageListe);
        pch.propertyChange(Ereignis.PANEL_TABELLE, null);
    }
}
