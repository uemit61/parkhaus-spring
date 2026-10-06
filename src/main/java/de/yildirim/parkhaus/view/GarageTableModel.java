package de.yildirim.parkhaus.view;



import de.yildirim.parkhaus.model.entity.Garage;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Füllt die Parkplatztabelle direkt aus einer Liste von {@link Garage}-Objekten,
 * ohne den Umweg über Object-Arrays wie beim DefaultTableModel.
 *
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class GarageTableModel extends AbstractTableModel
{
    private List<Garage> garageList;
    private final String[] columnNames ={"parketage_etageNr","platzNr", "fahrzeug_nummernschild"};

    public GarageTableModel(List<Garage> garageList)
    {
        this.garageList = garageList;
    }

    /**
     * Tauscht die angezeigte Liste aus und meldet der Tabelle, dass sie sich neu
     * zeichnen soll.
     *
     * @param neueListe die belegten Parkplätze, die ab jetzt angezeigt werden
     */
    public void updateTable(List<Garage> neueListe)
    {
        this.garageList = neueListe;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount()
    {
        return garageList.size();
    }

    @Override
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Liefert den Wert einer Zelle.
     *
     * <p>Statt die Spalten einzeln abzufragen, wird aus dem Spaltennamen der Name
     * des passenden Getters gebaut — aus "platzNr" wird "getPlatzNr" — und dieser per
     * Reflection aufgerufen. Eine zusätzliche Spalte braucht deshalb nur einen
     * weiteren Eintrag in {@code columnNames}.
     *
     * @param rowIndex    Zeile, entspricht der Position in der Liste
     * @param columnIndex Spalte, entspricht der Position in {@code columnNames}
     * @return Wert der Zelle, oder {@code null}, wenn zum Spaltennamen kein Getter
     *         existiert
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Object retVal =null;
        String propName = columnNames[columnIndex];
        try
        {
            String methodeName ="get"+propName.substring(0,1).toUpperCase()+propName.substring(1);
            Garage garage = garageList.get(rowIndex);
            Method getValue =garage.getClass().getMethod(methodeName);
            retVal = getValue.invoke(garage);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return retVal;
    }

    @Override
    public String getColumnName(int colum)
    {
        return columnNames[colum];
    }
}
