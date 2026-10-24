package de.yildirim.parkhaus.view;

import de.yildirim.parkhaus.controller.Controller;
import de.yildirim.parkhaus.model.entity.Fahrzeug;
import de.yildirim.parkhaus.model.event.Ereignis;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import static de.yildirim.parkhaus.model.event.Ereignis.ALARM;


/**
 * @author      Ümit Yildirim <uemit611@outlook.de>
 * @copyright   Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class ViewParkhaus implements PropertyChangeListener
{
	private JFrame frame;
	private JButton btnCheckin;
	private JButton btnPosition;
	private JButton btnCheckout;
	private Controller controller = null;
	private JLabel lblInformation;
	private JLabel lblInformation_2;
	private JLabel lblAnzahFrei;
	private JButton btnAdmin;
	private JButton btnBeenden;
	private AdministrationsGui adminView;
	private JPanel tempPanel;
	private JPanel parkplatzTabelle;
	private JPanel autoTabelle;
	private  JComboBox<Fahrzeug> fahrzeugJComboBox;
	private  JComboBox<String> boxTyp;
	private DefaultComboBoxModel<Fahrzeug> fahrzeugDefaultComboBoxModel;
	private DefaultComboBoxModel<String> typDefaultComboBoxModel;

	private JSeparator separator;


	/**
	 * Create the application.
	 */
	public ViewParkhaus(Controller controller) 
	{
		this.controller = controller;
		initialize();
	}

	public void addPropertys()
	{
		controller.addPropertyListener(ALARM, this);
		controller.addPropertyListener(Ereignis.KENNZEICHEN_NICHT_GEFUNDEN, this);
		controller.addPropertyListener(Ereignis.PARKPLATZ_VERLASSEN,this);
		controller.addPropertyListener(Ereignis.ZEIGE_POSITION, this);
		controller.addPropertyListener(Ereignis.PARKPLATZ_VOLL, this);
		controller.addPropertyListener(Ereignis.FREIE_PLAETZE, this);
		controller.addPropertyListener(Ereignis.ZURUECK_HAUPTPANEL, this);
		controller.addPropertyListener(Ereignis.KENNZEICHEN_UNGUELTIG,this);
		controller.addPropertyListener(Ereignis.PANEL_TABELLE, this);
		controller.addPropertyListener(Ereignis.ZURUECK_ADMIN_PANEL, this);
		controller.addPropertyListener(Ereignis.PANEL_TABELLE_FAHRZEUG,this);
		controller.addPropertyListener(Ereignis.FAHRZEUGE_GELADEN, this);
		controller.addPropertyListener(Ereignis.TYPANGABE_FEHLT,this);
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		addPropertys();
		frame = new JFrame();
		frame.setBounds(300,100,800, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setVisible(true);
		
		JLabel lblUeberschrift = new JLabel("Parkhaus Simulator");
		lblUeberschrift.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblUeberschrift.setBounds(290, 30, 220, 20);
		frame.getContentPane().add(lblUeberschrift);
		
		JLabel lblNumschild = new JLabel("Nummernschild:");
		lblNumschild.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNumschild.setBounds(50, 169, 120, 16);
		frame.getContentPane().add(lblNumschild);


		fahrzeugDefaultComboBoxModel = new DefaultComboBoxModel<>();
		fahrzeugJComboBox = new JComboBox<>(fahrzeugDefaultComboBoxModel);
		fahrzeugJComboBox.setBounds(190, 165, 100, 30);
		fahrzeugJComboBox.setEditable(true);
		fahrzeugJComboBox.addActionListener(e->ViewParkhaus.selectFahrzeug(boxTyp,fahrzeugJComboBox));
		frame.getContentPane().add(fahrzeugJComboBox);

		btnCheckin = new JButton("Checkin");
		btnCheckin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCheckin.addActionListener(this::btnCheckinActionPerformed);
		btnCheckin.setBounds(340, 165, 100, 30);
		frame.getContentPane().add(btnCheckin);
		
		btnCheckout = new JButton("Checkout");
		btnCheckout.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCheckout.addActionListener(this::btnCheckoutActionPerformed);

		btnCheckout.setBounds(460, 165, 105, 30);
		frame.getContentPane().add(btnCheckout);
		
		btnPosition = new JButton("Show Position");
		btnPosition.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnPosition.addActionListener(this::btnPositionActionPerformed);
		btnPosition.setBounds(585, 165, 150, 30);
		frame.getContentPane().add(btnPosition);
		
		lblInformation = new JLabel("");
		lblInformation.setVerticalAlignment(SwingConstants.TOP);
		lblInformation.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblInformation.setBounds(50, 230, 650, 20);
		frame.getContentPane().add(lblInformation);
		
		lblInformation_2 = new JLabel("");
		lblInformation_2.setForeground(new Color(255, 0, 0));
		lblInformation_2.setVerticalAlignment(SwingConstants.TOP);
		lblInformation_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblInformation_2.setBounds(50, 250, 650, 20);
		frame.getContentPane().add(lblInformation_2);
		
		JLabel lblFahrTyp = new JLabel("Fahrzeugtyp:");
		lblFahrTyp.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFahrTyp.setBounds(50, 120, 120, 20);
		frame.getContentPane().add(lblFahrTyp);
		
		boxTyp = new JComboBox<>();
		typDefaultComboBoxModel = new DefaultComboBoxModel<>();
		typDefaultComboBoxModel.addElement("Auto");
		typDefaultComboBoxModel.addElement("Motorrad");
		boxTyp.setFont(new Font("Tahoma", Font.BOLD, 14));
		boxTyp.setModel(typDefaultComboBoxModel);
		boxTyp.setBounds(190, 113, 100, 30);
		frame.getContentPane().add(boxTyp);
		
		btnAdmin = new JButton("Administration");
		btnAdmin.addActionListener(this::btnAdminActionPerformed);
		btnAdmin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAdmin.setBounds(50, 299, 150, 30);
		frame.getContentPane().add(btnAdmin);
		
		btnBeenden = new JButton("Beenden");
		btnBeenden.addActionListener(e->System.exit(0));
		btnBeenden.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnBeenden.setBounds(635, 299, 100, 30);
		frame.getContentPane().add(btnBeenden);
		
		JLabel lblFreiePlatz = new JLabel("Freie Parkplätze:");
		lblFreiePlatz.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblFreiePlatz.setBounds(50, 66, 122, 30);
		frame.getContentPane().add(lblFreiePlatz);
		
		lblAnzahFrei = new JLabel("...");
		lblAnzahFrei.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAnzahFrei.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblAnzahFrei.setBounds(190, 66, 100, 30);
		frame.getContentPane().add(lblAnzahFrei);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		lblNewLabel.setBounds(50, 228, 685, 45);
		frame.getContentPane().add(lblNewLabel);

		separator = new JSeparator();
		separator.setBounds(0, 54, 798, 2);
		frame.getContentPane().add(separator);

		tempPanel = (JPanel) frame.getContentPane(); // ContentPane zwischen speichern

		adminView = new AdministrationsGui(controller,fahrzeugDefaultComboBoxModel,typDefaultComboBoxModel);
		
		parkplatzTabelle = new ParkplatzTabelle(controller);
		
		autoTabelle = new AutoTabelle(controller);

		controller.ermittleFreiePlaetze();
		controller.ladeFahrzeuge();
	}

	//ActionPErformed

	protected static void selectFahrzeug(JComboBox<String> stringJComboBox,JComboBox<Fahrzeug> comboBox)
	{
		Object getSelectedItem = comboBox.getSelectedItem();
		String selectedItem= getSelectedItem instanceof  Fahrzeug f ?  f.getTyp():null;
		stringJComboBox.setSelectedItem(selectedItem);
	}
	private void btnCheckinActionPerformed(ActionEvent e)
	{
		Object getSelectedItem =fahrzeugJComboBox.getSelectedItem();
		String selectedItem= getSelectedItem instanceof  Fahrzeug f ?  f.getNummernschild():(String) getSelectedItem;
		String nummernschild = selectedItem != null ? selectedItem:"";
		controller.befahren(nummernschild,(String) boxTyp.getSelectedItem());
		fahrzeugJComboBox.setSelectedItem(null);
		boxTyp.setSelectedItem(null);
		controller.ladeFahrzeuge();
	}
	
	private void btnCheckoutActionPerformed(ActionEvent e) 
	{
		lblInformation.setText("");
		lblInformation_2.setText("");
		Object getSelectedItem =fahrzeugJComboBox.getSelectedItem();
		String selectedItem= getSelectedItem instanceof  Fahrzeug f ?  f.getNummernschild():(String) getSelectedItem;
		String nummernschild = selectedItem != null ? selectedItem:"";
		controller.verlassen(nummernschild,(String) boxTyp.getSelectedItem());
		fahrzeugJComboBox.setSelectedItem(null);
		boxTyp.setSelectedItem(null);

	}

	private void btnPositionActionPerformed(ActionEvent e)
	{
		Object getSelectedItem =fahrzeugJComboBox.getSelectedItem();
		String selectedItem= getSelectedItem instanceof  Fahrzeug f ?  f.getNummernschild():(String) getSelectedItem;
		String nummernschild = selectedItem != null ? selectedItem:"";
		controller.zeigePos(nummernschild);
		fahrzeugJComboBox.setSelectedItem(null);
		boxTyp.setSelectedItem(null);
	}
	

	private void btnAdminActionPerformed(ActionEvent e) 
	{
		frame.setBounds(300,100,650,410);
		adminView.getLblInfo().setText("");
		fahrzeugJComboBox.setSelectedItem(null);
		boxTyp.setSelectedItem(null);
		frame.setContentPane(adminView);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
        switch (Ereignis.valueOf(evt.getPropertyName()))
        {
            case ALARM ->
            {
                lblInformation.setText("Ein Auto mit dem selben Nummernschild befindet sich im Parkhaus.");
                lblInformation_2.setText("Wegfahrsperre wurde aktiviert. Bitte informieren Sie die Polizei!");
            }
			case PARKPLATZ_VOLL ->
            {
                lblInformation_2.setText("Parkplatz ist VOLL.");
                lblInformation.setText("");
            }
			case KENNZEICHEN_NICHT_GEFUNDEN ->
            {
                String nummernschild = (String) evt.getNewValue();
                lblInformation.setText("");
                lblInformation_2.setText("Das Fahrzeug '" + nummernschild + "' befindet sich nicht im Parkhaus.");

            }
			case PARKPLATZ_VERLASSEN ->
            {
                String nummernschild = (String) evt.getNewValue();
                lblInformation_2.setText("");
                lblInformation.setText("Das Fahrzeug '" + nummernschild + "' hat das Parkhaus verlassen.");

            }
			case ZEIGE_POSITION ->
            {
				@SuppressWarnings("unchecked")
                List<Object> position = (List<Object>) evt.getNewValue();
                lblInformation_2.setText("");
                lblInformation.setText("Der Parkplatz vom " + position.getFirst() + " '" + position.get(1) + "' befindet sich auf der Etage " + position.get(2) + " auf Platz " + position.get(3));
            }
			case FREIE_PLAETZE ->
            {
                int frei = 0;
                frei = (int) evt.getNewValue();
                lblAnzahFrei.setText("" + frei);
            }
			case KENNZEICHEN_UNGUELTIG ->
            {
                String numSchild = (String) evt.getNewValue();
                lblInformation.setText("");
                lblInformation_2.setText("Falsche Eingabe. Das Nummernschild '" + numSchild + "' hat ein falsches Format. ");
            }
			case ZURUECK_HAUPTPANEL ->
            {
                frame.setBounds(300, 100, 800, 400);
                lblInformation_2.setText("");
                lblInformation.setText("");
				fahrzeugJComboBox.setSelectedItem(null);
				boxTyp.setSelectedIndex(0);
                frame.setContentPane(tempPanel); //wechselt tempPanel =getContentPane entspricht der (hauptpanel)
            }
			case PANEL_TABELLE ->
            {
                frame.setBounds(300, 100, 590, 590);
                lblInformation_2.setText("");
                lblInformation.setText("");
                frame.setContentPane(parkplatzTabelle);
            }
			case ZURUECK_ADMIN_PANEL ->
            {
                frame.setBounds(300, 100, 650, 410);
                lblInformation.setText("");
                lblInformation_2.setText("");
				fahrzeugJComboBox.setSelectedItem(null);
				boxTyp.setSelectedIndex(0);
                frame.setContentPane(adminView);
            }
			case PANEL_TABELLE_FAHRZEUG ->
            {
                frame.setBounds(300, 100, 590, 590);
                lblInformation_2.setText("");
                lblInformation.setText("");
                frame.setContentPane(autoTabelle);
            }
			case FAHRZEUGE_GELADEN ->
			{
				List<Fahrzeug> fahrzeugListe = (List<Fahrzeug>) evt.getNewValue();
				fahrzeugDefaultComboBoxModel.removeAllElements();
				fahrzeugDefaultComboBoxModel.addAll(fahrzeugListe);
			}
			case TYPANGABE_FEHLT ->
			{
				Boolean admin = (Boolean) evt.getNewValue();
				String fehler= "Typangabe fehlt!";
				if(admin)
					adminView.setLblInfoText(fehler);
				else
					lblInformation_2.setText(fehler);

			}
        }
	}
}
