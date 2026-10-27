package de.yildirim.parkhaus.view;

import de.yildirim.parkhaus.controller.Controller;
import de.yildirim.parkhaus.model.event.Ereignis;
import de.yildirim.parkhaus.model.event.GarageListe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.Serial;
import java.util.ArrayList;


/**
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class ParkplatzTabelle extends JPanel
{

    @Serial
    private static final long serialVersionUID = 1L;
    private Controller controller = null;
    private final GarageTableModel garageTableModel;

    public void addProperty()
    {
        controller.addPropertyListener(Ereignis.PARKPLATZ_TABELLE, this::zeigeParkplaetze);
    }

    /**
     * Create the panel.
     */
    public ParkplatzTabelle(Controller controller)
    {

        this.controller = controller;
        addProperty();
        setLayout(null);

        JButton btnZuruck = new JButton("Zurück");
        btnZuruck.addActionListener(this::btnZuruckActionPerformed);
        btnZuruck.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnZuruck.setBounds(60, 50, 85, 30);
        add(btnZuruck);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(60, 114, 420, 391);
        add(scrollPane);

        JTable table = new JTable();
        garageTableModel = new GarageTableModel(new ArrayList<>());
        table.setModel(garageTableModel);
        scrollPane.setViewportView(table);
    }

    private void btnZuruckActionPerformed(ActionEvent e)
    {

        controller.propertyChange(Ereignis.ZURUECK_ADMIN_PANEL);
    }


    public void zeigeParkplaetze(PropertyChangeEvent evt)
    {
        GarageListe garageListe = (GarageListe) evt.getNewValue();
        garageTableModel.updateTable(garageListe.garageListe());
    }
}
