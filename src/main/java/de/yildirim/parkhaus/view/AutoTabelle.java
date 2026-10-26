package de.yildirim.parkhaus.view;

import de.yildirim.parkhaus.controller.Controller;
import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.event.Ereignis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


/**
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class AutoTabelle extends JPanel
{

	@Serial
    private static final long serialVersionUID = 1L;
	private Controller controller = null;
    private final FahrzeugTableModel fahrzeugTableModel;

	public void addProperty()
	{
		controller.addPropertyListener(Ereignis.FAHRZEUG_TABELLE, this::zeigeFahrzeuge);
	}

	@SuppressWarnings("unchecked")
	private void zeigeFahrzeuge(PropertyChangeEvent evt)
	{
		List<Fahrzeug> fahrzeugListe = (List<Fahrzeug>) evt.getNewValue();
		fahrzeugTableModel.updateTable(fahrzeugListe);
	}

	/**
	 * Create the panel.
	 */
	public AutoTabelle(Controller controller) 
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
		fahrzeugTableModel = new FahrzeugTableModel(new ArrayList<>());
		table.setModel(fahrzeugTableModel);
		scrollPane.setViewportView(table);
	}

	private void btnZuruckActionPerformed(ActionEvent e)
	{
		controller.propertyChange(Ereignis.ZURUECK_ADMIN_PANEL);
	}
}
