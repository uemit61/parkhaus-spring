package de.yildirim.parkhaus.view;




import de.yildirim.parkhaus.controller.Controller;
import de.yildirim.parkhaus.model.entity.Fahrzeug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class AutoTabelle extends JPanel implements PropertyChangeListener
{

	private static final long serialVersionUID = 1L;
	private Controller controller = null;
	private JButton btnZuruck;
	private JTable table;
	private FahrzeugTableModel fahrzeugTableModel;

	public void addProperty()
	{
		controller.addPropertyListener("AutoTab", this);
	}
	/**
	 * Create the panel.
	 */
	public AutoTabelle(Controller controller) 
	{
		
		this.controller = controller;
		addProperty();
		
		setLayout(null);
		
		btnZuruck = new JButton("Zurück");
		btnZuruck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnZuruckActionPerformed(e);
			}
		});
		btnZuruck.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnZuruck.setBounds(60, 50, 85, 30);
		add(btnZuruck);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(60, 114, 420, 391);
		add(scrollPane);
		
		table = new JTable();
		fahrzeugTableModel = new FahrzeugTableModel(new ArrayList<>());
		table.setModel(fahrzeugTableModel);
		scrollPane.setViewportView(table);
	}

	private void btnZuruckActionPerformed(ActionEvent e) 
	{
		controller.propertyChange("ZuruckAdmin");
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		List<Fahrzeug> fahrzeugListe = (List<Fahrzeug>) evt.getNewValue();
		fahrzeugTableModel.updateTable(fahrzeugListe);
	}

	
}
