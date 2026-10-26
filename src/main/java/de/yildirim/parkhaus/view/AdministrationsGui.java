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
import java.io.Serial;


/**
 * @author Ümit Yildirim <uemit611@outlook.de>
 * @copyright Copyright (c) 2024-2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */
public class AdministrationsGui extends JPanel
{

    @Serial
    private static final long serialVersionUID = 1L;
    private Controller controller = null;
    private final JLabel lblInfo;
    private JComboBox<String> boxTyp;
    private final DefaultComboBoxModel<String> typDefaultComboBoxModel;
    private final JComboBox<Fahrzeug> fahrzeugJComboBox;
    private final DefaultComboBoxModel<Fahrzeug> fahrzeugDefaultComboBoxModel;

    public void addProperty()
    {
        controller.addPropertyListener(Ereignis.KENNZEICHEN_UNGUELTIG, evt -> zeigeFormatFehler((String) evt.getNewValue()));
        controller.addPropertyListener(Ereignis.REGISTRIERT, evt -> zeigeRegistriert((String[]) evt.getNewValue()));
        controller.addPropertyListener(Ereignis.FAHRZEUG_GELOESCHT, evt -> zeigeGeloescht((String[]) evt.getNewValue()));
        controller.addPropertyListener(Ereignis.FAHRZEUG_LOESCHEN_FEHLGESCHLAGEN, evt -> zeigeLoeschenFehlgeschlagen((String[]) evt.getNewValue()));
        controller.addPropertyListener(Ereignis.LOESCHEN_VERBOTEN, evt -> zeigeLoeschenVerboten((String) evt.getNewValue()));
        controller.addPropertyListener(Ereignis.SCHON_REGISTRIERT, evt -> zeigeSchonRegistriert((String) evt.getNewValue()));
    }


    /**
     * Create the panel.
     */
    public AdministrationsGui(Controller controller, DefaultComboBoxModel<Fahrzeug> fahrzeugDefaultComboBoxModel,
            DefaultComboBoxModel<String> typDefaultComboBoxModel)
    {
        this.fahrzeugDefaultComboBoxModel = fahrzeugDefaultComboBoxModel;
        this.typDefaultComboBoxModel = typDefaultComboBoxModel;
        this.controller = controller;
        addProperty();
        setLayout(null);

        JLabel lblNewLabel = new JLabel("Adminbereich");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblNewLabel.setBounds(252, 30, 136, 45);
        add(lblNewLabel); // ins JPanel

        JLabel lblNumSch = new JLabel("Nummernschild:");
        lblNumSch.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNumSch.setBounds(60, 172, 120, 20);
        add(lblNumSch);


        fahrzeugJComboBox = new JComboBox<>(fahrzeugDefaultComboBoxModel);
        fahrzeugJComboBox.setBounds(190, 170, 100, 30);
        fahrzeugJComboBox.addActionListener(e -> ViewParkhaus.selectFahrzeug(boxTyp, fahrzeugJComboBox));
        fahrzeugJComboBox.setEditable(true);
        add(fahrzeugJComboBox);


        JButton btnLoeschen = new JButton("Löschen");
        btnLoeschen.addActionListener(this::btnLoeschenActionPerformed);
        btnLoeschen.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnLoeschen.setBounds(490, 168, 100, 30);
        add(btnLoeschen);

        JButton btnEinfuegen = new JButton("Einfügen");
        btnEinfuegen.addActionListener(this::btnEinfuegenActionPerformed);
        btnEinfuegen.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnEinfuegen.setBounds(337, 168, 100, 30);
        add(btnEinfuegen);

        JButton btnZurueck = new JButton("Zurück");
        btnZurueck.addActionListener(this::btnZuruekActionPerformed);
        btnZurueck.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnZurueck.setBounds(60, 299, 100, 30);
        add(btnZurueck);

        JButton btnAutoTabelle = new JButton("Autotabelle");
        btnAutoTabelle.addActionListener(this::btnAutoTabelleActionPerformed);
        btnAutoTabelle.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnAutoTabelle.setBounds(475, 299, 115, 30);
        add(btnAutoTabelle);

        lblInfo = new JLabel("");
        lblInfo.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblInfo.setBorder(new LineBorder(new Color(0, 0, 0), 3));
        lblInfo.setBounds(61, 230, 529, 30);
        add(lblInfo);


        boxTyp = new JComboBox<>(typDefaultComboBoxModel);
        boxTyp.setFont(new Font("Tahoma", Font.BOLD, 14));
        boxTyp.setBounds(190, 120, 100, 30);
        add(boxTyp);


        JLabel lblNewLabel_1 = new JLabel("Fahrzeugtyp:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel_1.setBounds(60, 125, 100, 20);
        add(lblNewLabel_1);

        JButton btnParkplatzliste = new JButton("Parkplatztabelle");
        btnParkplatzliste.addActionListener(this::btnParkplaetzeActionPerformed);
        btnParkplatzliste.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnParkplatzliste.setBounds(247, 299, 136, 30);
        add(btnParkplatzliste);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 85, 639, 2);
        add(separator);

    }

    private void btnAutoTabelleActionPerformed(ActionEvent e)
    {
        controller.autoTabelle();
        lblInfo.setText("");
    }

    private void btnParkplaetzeActionPerformed(ActionEvent e)
    {
        controller.parkplatzTabelle();
        lblInfo.setText("");
    }

    private void btnEinfuegenActionPerformed(ActionEvent e)
    {
        lblInfo.setText("");
        Object getSelectedItem = fahrzeugJComboBox.getSelectedItem();
        String selectedItem = getSelectedItem instanceof Fahrzeug f ? f.getNummernschild() : (String) getSelectedItem;
        String nummernschild = selectedItem != null ? selectedItem : "";
        controller.fahrzeugRegistrieren(nummernschild, (String) boxTyp.getSelectedItem());
        fahrzeugJComboBox.setSelectedItem(null);
        controller.ladeFahrzeuge();
        boxTyp.setSelectedItem(null);
    }

    private void btnZuruekActionPerformed(ActionEvent e)
    {
        controller.propertyChange(Ereignis.ZURUECK_HAUPTPANEL);
        lblInfo.setText("");
    }

    private void btnLoeschenActionPerformed(ActionEvent e)
    {
        Object getSelectedItem = fahrzeugJComboBox.getSelectedItem();
        String selectedItem = getSelectedItem instanceof Fahrzeug f ? f.getNummernschild() : (String) getSelectedItem;
        String nummernschild = selectedItem != null ? selectedItem : "";
        String typ = boxTyp.getSelectedItem() != null ? (String) boxTyp.getSelectedItem() : "Fahrzeug";
        controller.loeschen(nummernschild, typ);
        boxTyp.setSelectedItem(null);
        fahrzeugJComboBox.setSelectedItem(null);
        controller.ladeFahrzeuge();
    }


    protected JLabel getLblInfo()
    {
        return lblInfo;
    }

    protected void setLblInfoText(String text)
    {
        lblInfo.setText(text);
    }

    private void zeigeFormatFehler(String nummernschild)
    {
        setLblInfoText("Das Nummernschild '" + nummernschild + "' hat ein falsches Format.");
    }

    private void zeigeRegistriert(String[] pos)
    {
        setLblInfoText("Das " + pos[1] + " '" + pos[0] + "' wurde registriert.");
    }

    private void zeigeGeloescht(String[] pos)
    {
        setLblInfoText("Das " + pos[1] + " '" + pos[0] + "' wurde gelöscht");
    }

    private void zeigeLoeschenFehlgeschlagen(String[] pos)
    {
        setLblInfoText("Das " + pos[1] + " '" + pos[0] + "' ist nicht registriert.");
    }

    private void zeigeLoeschenVerboten(String nummernschild)
    {
        setLblInfoText("Verboten! Das Fahrzeug " + nummernschild + " muss erst ausgecheckt werden.");
    }

    private void zeigeSchonRegistriert(String nummernschild)
    {
        setLblInfoText("Ein Fahrzeug mit dem selben Kennzeichen '" + nummernschild + "' ist bereits registriert.");
    }

}
