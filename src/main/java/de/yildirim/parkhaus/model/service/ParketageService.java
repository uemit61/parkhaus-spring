package de.yildirim.parkhaus.model.service;

import de.yildirim.parkhaus.model.entity.Parketage;
import de.yildirim.parkhaus.model.event.Ereignis;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.repository.GarageRepository;
import de.yildirim.parkhaus.model.repository.ParketageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Fachlogik rund um die Etagen des Parkhauses.
 *
 * <p>Holt die Daten über die Repositories und meldet Ergebnisse über den
 * Ereignis-Kanal an die View; SQL steht hier keines.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Service
public class ParketageService
{
    private final ParketageRepository parketageRepository;
    private final GarageRepository garageRepository;
    private final PropertyChangeHandle pch;

    public ParketageService(ParketageRepository parketageRepository, GarageRepository garageRepository,
            PropertyChangeHandle pch)
    {
        this.parketageRepository = parketageRepository;
        this.garageRepository = garageRepository;
        this.pch = pch;
    }

    /**
     * Ermittelt die Anzahl der freien Plätze und meldet sie über "Frei" an die View.
     *
     * <p>Die Gesamtkapazität steht nicht im Code, sondern ergibt sich aus den
     * Stammdaten: Summe aller Etagen-Kapazitäten abzüglich der belegten Plätze.
     * Kommt eine Etage hinzu, genügt eine Migration.
     *
     * <p>Anders als bei der Etagenzuordnung in {@link GarageService} spielt die
     * Reihenfolge hier keine Rolle — summiert wird, nicht durchlaufen. Deshalb
     * braucht dieses {@code findAll()} kein {@code Sort}.
     */
    @Transactional(readOnly = true)
    public void freiePlaetze()
    {
        long freiePlaetze =0;
        long gesamtkapazitaet =0;
        long belegtPlaetze =garageRepository.count();


        List<Parketage> parketageList = parketageRepository.findAll();

        gesamtkapazitaet =parketageList.stream().mapToInt(Parketage::getAnzahlPlaetze).sum();

        freiePlaetze = gesamtkapazitaet-belegtPlaetze;

        // count() und sum() rechnen in long; die View erwartet ein int. Bei einer
        // Platzzahl in dieser Größenordnung ist die Verengung gefahrlos.
        pch.propertyChange(Ereignis.FREIE_PLAETZE, (int) freiePlaetze);

    }
}
