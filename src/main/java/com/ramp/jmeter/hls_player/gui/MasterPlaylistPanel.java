package com.ramp.jmeter.hls_player.gui;

import javax.swing.*;
import java.awt.event.ItemEvent;

class MasterPlaylistPanel extends JPanel {

    private final JTextField customDurationTextField;
    void setCustomDurationTextField(String text){
        customDurationTextField.setText(text);
    }
    String getCustomDurationTextField(){
        return customDurationTextField.getText();
    }

    private final JTextField urlTextField;
    void setUrlTextField(String text){
        urlTextField.setText(text);
    }
    String getUrlTextField(){
        return urlTextField.getText();
    }
    
    private final JTextField urlEzdrmPostTextField;
    void setUrlEzdrmPostTextField(String text){
        urlEzdrmPostTextField.setText(text);
    }
    String getUrlEzdrmPostTextField(){
        return urlEzdrmPostTextField.getText();
    }
    
    private final JTextField urlLicenseTextField;
    void setUrlLicenseTextField(String text){
        urlLicenseTextField.setText(text);
    }
    String getUrlLicenseTextField(){
        return urlLicenseTextField.getText();
    }

    private final ButtonGroup durationButtons;
    private final JRadioButton customDurationRButton;
    private final JRadioButton wholeDurationRButton;

    MasterPlaylistPanel() {
        //Buttons
        durationButtons = new ButtonGroup();
        wholeDurationRButton = new JRadioButton("Whole Duration", true);
        durationButtons.add(wholeDurationRButton);
        customDurationRButton = new JRadioButton("Custom Duration", false);
        durationButtons.add(customDurationRButton);

        // Labels
        JLabel urlTextFieldLabel = new JLabel("URL:");
        
        JLabel urlEzdrmPostTextFieldLabel = new JLabel("URL Post EZDRM:");
        
        JLabel urlLicenseTextFieldLabel = new JLabel("URL License:");

        // Text Fields
        customDurationTextField = new JTextField();
        urlTextField = new JTextField();
        
        urlEzdrmPostTextField = new JTextField();
        urlLicenseTextField = new JTextField();
        

        //Actions
        customDurationRButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                customDurationTextField.setEnabled(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                customDurationTextField.setEnabled(false);
            }
        });

        //Layout
        GroupLayout masterPlaylistPanelLayout = new GroupLayout(this);
        masterPlaylistPanelLayout.setAutoCreateGaps(true);
        masterPlaylistPanelLayout.setAutoCreateContainerGaps(true);
        this.setBorder(BorderFactory.createTitledBorder("Master Playlist"));
        this.setLayout(masterPlaylistPanelLayout);
        //Master Playlist Panel
        masterPlaylistPanelLayout.setHorizontalGroup(
                masterPlaylistPanelLayout.createSequentialGroup()
                        .addComponent(urlEzdrmPostTextFieldLabel)
                        .addComponent(urlEzdrmPostTextField)
                        .addComponent(urlLicenseTextFieldLabel)
                        .addComponent(urlLicenseTextField)
                        .addComponent(urlTextFieldLabel)
                        .addComponent(urlTextField)
                        .addComponent(wholeDurationRButton)
                        .addComponent(customDurationRButton)
                        .addComponent(customDurationTextField)
        );
        masterPlaylistPanelLayout.setVerticalGroup(
                masterPlaylistPanelLayout.createParallelGroup()
                        .addComponent(urlEzdrmPostTextFieldLabel)
                        .addComponent(urlEzdrmPostTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(urlLicenseTextFieldLabel)
                        .addComponent(urlLicenseTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        
                        .addComponent(urlTextFieldLabel)
                        .addComponent(urlTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(wholeDurationRButton)
                        .addComponent(customDurationRButton)
                        .addComponent(customDurationTextField,
                                javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }

    void setIsCustomDuration(boolean isCustomDuration) {
        customDurationRButton.setSelected(isCustomDuration);
    }

    boolean isCustomDuration() {
        return customDurationRButton.isSelected();
    }
}
