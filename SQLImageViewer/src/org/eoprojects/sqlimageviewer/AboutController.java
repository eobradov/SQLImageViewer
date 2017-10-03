package org.eoprojects.sqlimageviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eoprojects.sqlimageviewer.util.DriverConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AboutController {
	
	@FXML
	private Button okButton;
	
	@FXML
	private TextArea licenseText;
	
	@FXML
	private Label authorLabel;
	
	@FXML
	private Label productNameLabel;
	
	@FXML
	private Label logoLabel;
	
	@FXML
	private void initialize() {
		productNameLabel.setText(DriverConstants.PROGRAM_NAME + " V" + DriverConstants.PROGRAM_VERSION);
		authorLabel.setText(DriverConstants.PROGRAM_AUTHOR);
		
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("resources/gpl-3.0.txt")));
			while ((line = br.readLine()) != null) {
				licenseText.appendText(line + "\n");
			}
		} catch (IOException e) {
			licenseText.setText(e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					//
				}	
			}	
		}
		
		licenseText.setEditable(false);
		licenseText.selectHome();
		licenseText.deselect();
		
		logoLabel.setText("");
		Image logoImage = new Image(Main.class.getResourceAsStream("resources/logo.png"));
		logoLabel.setGraphic(new ImageView(logoImage));
	}
	
	@FXML
	private void handleAction(ActionEvent event) {
		if (event.getSource().equals(okButton)) {
			Node source = (Node) event.getSource(); 
		    Stage currentstage = (Stage) source.getScene().getWindow();
			currentstage.close();
		}
	}
	
}
