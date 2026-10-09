package de.yildirim.parkhaus.model.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Gemeinsamer Ereignis-Kanal zwischen Model und View. Alle Modelle melden über
 * dieselbe Instanz, die Views registrieren sich auf die Namen der Ereignisse,
 * die sie interessieren.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class PropertyChangeHandle
{
    private final PropertyChangeSupport support;

    public PropertyChangeHandle(PropertyChangeSupport support)
    {
        this.support = support;
    }

    /**
     * Meldet eine View für ein bestimmtes Ereignis an.
     *
     * @param propName Name des Ereignisses, zum Beispiel "Alarm"
     * @param pcl      die View, die darauf reagieren soll
     */
    public void addPropertyChangeListener(Ereignis propName, PropertyChangeListener pcl)
    {
        support.addPropertyChangeListener(propName.name(), pcl);
    }

    /**
     * Meldet ein Ereignis an alle Views, die sich auf diesen Namen angemeldet haben.
     * Der alte Wert wurde bewusst auf NULL gesetzt, damit immer gefeuert wird.
     *
     * @param propName Name des Ereignisses
     * @param newValue mitgeschickte Daten, oder {@code null}, wenn das Ereignis für
     *                 sich allein spricht
     */
    public void propertyChange(Ereignis propName, Object newValue)
    {
        support.firePropertyChange(propName.name(), null, newValue);
    }

}