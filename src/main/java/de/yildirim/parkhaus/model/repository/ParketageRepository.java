package de.yildirim.parkhaus.model.repository;

import de.yildirim.parkhaus.model.entity.Parketage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Datenzugriff auf die Tabelle {@code parketage}, die Etagen mit ihren Kapazitäten.
 *
 * <p>Der Rumpf ist leer, und das ist kein Versehen: die geerbten Methoden aus
 * {@link JpaRepository} reichen aus. Wo die Reihenfolge der Etagen zählt, gibt der
 * Aufrufer sie über {@code findAll(Sort)} mit, statt dafür eine eigene Methode zu
 * deklarieren.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public interface ParketageRepository extends JpaRepository<Parketage, Integer>
{
}
