package de.yildirim.parkhaus.config;

import de.yildirim.parkhaus.controller.Controller;
import de.yildirim.parkhaus.model.event.PropertyChangeHandle;

import de.yildirim.parkhaus.view.ViewParkhaus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.awt.EventQueue;
import java.beans.PropertyChangeSupport;

/**
 * Der Aufbau der Anwendung — Nachfolger des Composition Root aus
 * {@code MainGarage} im Swing-Projekt.
 *
 * <p>Die Kette aus Repositories, Services und Controller baut jetzt der
 * Container; hier steht nur noch, was er nicht allein wissen kann. Das sind
 * zwei Dinge: ein Objekt aus einer fremden Bibliothek, das niemand annotieren
 * kann, und der Start der Oberfläche.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
@Configuration
public class ParkhausConfig
{

    /**
     * Öffnet das Hauptfenster, sobald der Kontext vollständig steht.
     *
     * <p>Der Runner tritt an die Stelle der {@code main}-Methode aus
     * {@code MainGarage}. Übrig bleibt davon nur der Fensterbau: der
     * {@link Controller} kommt fertig verdrahtet als Parameter herein, weil
     * seine sämtlichen Konstruktorparameter selbst Beans sind.
     *
     * <p>{@code EventQueue.invokeLater} bleibt zwingend: der Runner läuft auf dem
     * main-Thread, Swing-Komponenten dürfen aber nur auf dem Event Dispatch
     * Thread entstehen. Danach hält genau dieser Thread — ein Nicht-Daemon —
     * die Anwendung am Leben, nicht Spring.
     *
     * @param controller Bindeglied zwischen Oberfläche und Fachschicht
     * @return der Startvorgang, den Spring am Ende des Hochfahrens ausführt
     */
    @Profile("!test")
    @Bean
    CommandLineRunner starteOberflaeche(Controller controller)
    {
        return args ->
        {
            EventQueue.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    new ViewParkhaus(controller);
                }
            });

        };
    }

    /**
     * Stellt den Ereignis-Kanal bereit, über den die Services an die Views melden.
     *
     * <p>Warum eine {@code @Bean}-Methode und keine Annotation an der Klasse:
     * {@link PropertyChangeSupport} stammt aus dem JDK und lässt sich nicht
     * annotieren. Es als Parameter zu verlangen ginge ebenfalls schief — jeder
     * Parameter einer {@code @Bean}-Methode ist eine Bestellung beim Container,
     * und diesen Typ kennt er nicht. Also entsteht er hier im Rumpf.
     *
     * <p>Eine einzige Instanz für die ganze Anwendung: Melder und Zuhörer finden
     * sich nur, wenn sie denselben Kanal benutzen.
     *
     * @return der gemeinsame Ereignis-Kanal
     */
    @Bean
    PropertyChangeHandle pch()
    {
        PropertyChangeSupport support =new PropertyChangeSupport(this);
        return new PropertyChangeHandle(support);
    }
}
