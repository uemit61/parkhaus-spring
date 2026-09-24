package de.yildirim.parkhaus.controller;


import de.yildirim.parkhaus.model.event.PropertyChangeHandle;
import de.yildirim.parkhaus.model.service.FahrzeugService;
import de.yildirim.parkhaus.model.service.GarageService;
import de.yildirim.parkhaus.model.service.ParketageService;
import org.springframework.stereotype.Component;

import java.beans.PropertyChangeListener;

/**
 * Bindeglied zwischen der Swing-Oberfläche und der Fachschicht.
 *
 * <p>Nimmt die Wünsche der View entgegen und leitet sie an den zuständigen Service
 * weiter. Fachlogik steht hier keine, Datenzugriff erst recht nicht — die Klasse
 * weiß nur, wer wofür zuständig ist.
 *
 * <p>Sie trägt {@code @Component} und nicht {@code @Controller}: Letzteres bedeutet
 * in Spring „Web-Controller" und wäre eine falsche Aussage. Alle vier
 * Konstruktorparameter sind selbst Beans, der Container baut die Klasse also allein
 * — die Kette, die im Swing-Projekt von Hand in {@code MainGarage} stand.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Component
public class Controller
{
    private final FahrzeugService fahrzeugService;
    private final ParketageService parketageService;
    private final GarageService modelGarage;
    private final PropertyChangeHandle pch;

    //Constructor


    public Controller(FahrzeugService fahrzeugService, ParketageService parketageService, GarageService modelGarage,
            PropertyChangeHandle pch)
    {
        this.fahrzeugService = fahrzeugService;
        this.parketageService = parketageService;
        this.modelGarage = modelGarage;
        this.pch = pch;
    }

    public void addPropertyListener(String propName, PropertyChangeListener view)
    {
        pch.addPropertyChangeListener(propName, view);
    }

    public void propertyChange(String propName)
    {
        pch.propertyChange(propName, null);
    }

    public void freiePlaetze()
    {
        parketageService.freiePlaetze();
    }

    public void befahren(String nummernschild, String typ)
    {
        modelGarage.befahren(nummernschild,typ);
        parketageService.freiePlaetze();
    }

    public void verlassen(String nummernschild,String typ)
    {
        modelGarage.verlassen(nummernschild,typ);
        freiePlaetze();
    }

    public void zeigePos(String nummernschild)
    {
        modelGarage.zeigePosition(nummernschild);
    }

    public void fahrzeugRegistrieren(String nummernschild,String typ)
    {
        //Propertier 'admin' sagt der Methode, dass Sie vom AdminView aufgerufen wurde
        //Check sagt, ob die Existenz des Fahrzeugs geprüft wurde.
        fahrzeugService.fahrzeugRegistrieren(nummernschild,typ,true);
    }

    public void loeschen(String nummernschild, String typ)
    {
        fahrzeugService.loescheFahrzeug(nummernschild, typ);
    }

    public void parkplatzTabelle()
    {
        modelGarage.parkplatzTabelle();
    }

    public void autoTabelle()
    {
        fahrzeugService.autoTabelle();
    }

}
