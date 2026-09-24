package de.yildirim.parkhaus.view;

import de.yildirim.parkhaus.controller.Controller;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


/**
 * @author      Ümit Yildirim <hopes61@icloud.com>
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
	private JComboBox boxTyp;
	private AdministrationsGui adminView;
	private JPanel tempPanel;
	private JPanel parkplatzTabelle;
	private JPanel autoTabelle;
	private JTextField textnNummerschild;
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
		controller.addPropertyListener("Alarm", this);
		controller.addPropertyListener("Fail", this);
		controller.addPropertyListener("Verlassen",this);
		controller.addPropertyListener("ZeigePos", this);
		controller.addPropertyListener("Voll", this);
		controller.addPropertyListener("Frei", this);
		controller.addPropertyListener("Zuruck", this);
		controller.addPropertyListener("FailCheck",this);
		controller.addPropertyListener("PanelTabelle", this);
		controller.addPropertyListener("ZuruckAdmin", this);
		controller.addPropertyListener("PanelTabelleAuto",this);
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
		
		textnNummerschild = new JTextField();
		textnNummerschild.setBounds(190, 165, 100, 30);
		frame.getContentPane().add(textnNummerschild);
		textnNummerschild.setColumns(10);
		
		btnCheckin = new JButton("Checkin");
		btnCheckin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCheckin.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				btnCheckinActionPerformed(e);
			}
		});
		btnCheckin.setBounds(340, 165, 100, 30);
		frame.getContentPane().add(btnCheckin);
		
		btnCheckout = new JButton("Checkout");
		btnCheckout.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnCheckout.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				btnCheckoutActionPerformed(e);
			}
		});
		btnCheckout.setBounds(460, 165, 105, 30);
		frame.getContentPane().add(btnCheckout);
		
		btnPosition = new JButton("Show Position");
		btnPosition.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnPosition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnPositionActionPerformed(e);
			}
		});
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
		
		boxTyp = new JComboBox();
		boxTyp.setFont(new Font("Tahoma", Font.BOLD, 14));
		boxTyp.setModel(new DefaultComboBoxModel(new String[] {"Auto", "Motorrad"}));
		boxTyp.setBounds(190, 113, 100, 30);
		frame.getContentPane().add(boxTyp);
		
		btnAdmin = new JButton("Administration");
		btnAdmin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				btnAdminActionPerformed(e);
			}
		});
		btnAdmin.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnAdmin.setBounds(50, 299, 150, 30);
		frame.getContentPane().add(btnAdmin);
		
		btnBeenden = new JButton("Beenden");
		btnBeenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				System.exit(0);
			}
		});
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

		tempPanel = (JPanel) frame.getContentPane(); // ContentPane zwischen speichern


		adminView = new AdministrationsGui(controller);
		
		parkplatzTabelle = new ParkplatzTabelle(controller);
		
		autoTabelle = new AutoTabelle(controller);
		
		
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBorder(new LineBorder(new Color(0, 0, 0), 3));
		lblNewLabel.setBounds(50, 228, 685, 45);
		frame.getContentPane().add(lblNewLabel);
		
		separator = new JSeparator();
		separator.setBounds(0, 54, 798, 2);
		frame.getContentPane().add(separator);
		
		controller.freiePlaetze();
	}

	//ActionPErformed
	private void btnCheckinActionPerformed(ActionEvent e)
	{
		controller.befahren(textnNummerschild.getText(),(String) boxTyp.getSelectedItem());
		textnNummerschild.setText("");
		boxTyp.setSelectedIndex(0);
	}
	
	private void btnCheckoutActionPerformed(ActionEvent e) 
	{
		lblInformation.setText("");
		lblInformation_2.setText("");				
		controller.verlassen(textnNummerschild.getText(),(String) boxTyp.getSelectedItem());
		textnNummerschild.setText("");
		boxTyp.setSelectedIndex(0);
	}

	private void btnPositionActionPerformed(ActionEvent e) 
	{
		controller.zeigePos(textnNummerschild.getText());
		textnNummerschild.setText("");
		boxTyp.setSelectedIndex(0);
	}
	

	private void btnAdminActionPerformed(ActionEvent e) 
	{
		frame.setBounds(300,100,650,410);
		adminView.getLblInfo().setText("");
		frame.setContentPane(adminView);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
        switch (evt.getPropertyName())
        {
            case "Alarm" ->
            {
                lblInformation.setText("Ein Auto mit dem selben Nummernschild befindet sich im Parkhaus.");
                lblInformation_2.setText("Wegfahrsperre wurde aktiviert. Bitte informieren Sie die Polizei!");
            }
            case "Voll" ->
            {
                lblInformation_2.setText("Parkplatz ist VOLL.");
                lblInformation.setText("");
            }
            case "Fail" ->
            {
                String numschild = (String) evt.getNewValue();
                lblInformation.setText("");
                lblInformation_2.setText("Das Fahrzeug '" + numschild + "' befindet sich nicht im Parkhaus.");

            }
            case "Verlassen" ->
            {
                String numschild = (String) evt.getNewValue();
                lblInformation_2.setText("");
                lblInformation.setText("Das Fahrzeug '" + numschild + "' hat das Parkhaus verlassen.");

            }
            case "ZeigePos" ->
            {
				@SuppressWarnings("unchecked")
                List<Object> position = (List<Object>) evt.getNewValue();
                lblInformation_2.setText("");
                lblInformation.setText("Der Parkplatz vom " + position.getFirst() + " '" + position.get(1) + "' befindet sich auf der Etage " + position.get(2) + " auf Platz " + position.get(3));
            }
            case "Frei" ->
            {
                int frei = 0;
                frei = (int) evt.getNewValue();
                lblAnzahFrei.setText("" + frei);
            }
            case "FailCheck" ->
            {
                String numSchild = (String) evt.getNewValue();
                lblInformation.setText("");
                lblInformation_2.setText("Falsche Eingabe. Das Nummernschild '" + numSchild + "' hat ein falsches Format. ");
            }
            case "Zuruck" ->
            {
                frame.setBounds(300, 100, 800, 400);
                lblInformation_2.setText("");
                lblInformation.setText("");
                frame.setContentPane(tempPanel); //wechselt tempPanel =getContentPane entspricht der (hauptpanel)
            }
            case "PanelTabelle" ->
            {
                frame.setBounds(300, 100, 590, 590);
                lblInformation_2.setText("");
                lblInformation.setText("");
                frame.setContentPane(parkplatzTabelle);
            }
            case "ZuruckAdmin" ->
            {
                frame.setBounds(300, 100, 650, 410);
                lblInformation.setText("");
                lblInformation_2.setText("");
                frame.setContentPane(adminView);
            }
            case "PanelTabelleAuto" ->
            {
                frame.setBounds(300, 100, 590, 590);
                lblInformation_2.setText("");
                lblInformation.setText("");
                frame.setContentPane(autoTabelle);
            }
        }
	}
}
