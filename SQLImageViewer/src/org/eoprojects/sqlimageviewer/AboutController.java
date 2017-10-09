package org.eoprojects.sqlimageviewer;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.eoprojects.sqlimageviewer.util.DriverConstants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller for About.fxml
 * 
 * @author Edi Obradovic
 * @version 1.0
 */
public class AboutController {

	@FXML
	private Button okButton;

	@FXML
	private TextArea licenseText;

	@FXML
	private Label authorLabel;
	
	@FXML
	private Hyperlink emailHyperlink;

	@FXML
	private Label productNameLabel;

	@FXML
	private Label logoLabel;

	/**
	 * Called on GUI initialize
	 */
	@FXML
	private void initialize() {
		productNameLabel.setText(DriverConstants.PROGRAM_NAME + " V" + DriverConstants.PROGRAM_VERSION);
		authorLabel.setText(DriverConstants.PROGRAM_AUTHOR);
		emailHyperlink.setText(DriverConstants.PROGRAM_EMAIL);
		
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
					e.printStackTrace();
				}
			}
		}

		licenseText.setEditable(false);
		licenseText.selectHome();
		licenseText.deselect();

		logoLabel.setText("");
		Image logoImage = new Image(Main.class.getResourceAsStream("resources/images/logo.png"));
		logoLabel.setGraphic(new ImageView(logoImage));
	}

	/**
	 * Handle actions on GUI elements
	 * 
	 * @param event
	 */
	@FXML
	private void handleAction(ActionEvent event) {
		if (event.getSource().equals(okButton)) {
			Node source = (Node) event.getSource();
			Stage currentstage = (Stage) source.getScene().getWindow();
			currentstage.close();
		} else if (event.getSource().equals(emailHyperlink)) {
			String mailString = ("mailto:" + emailHyperlink.getText() + "?subject=SQL%20Image%20Viewer");
			System.out.println(mailString);
			try {
				Desktop.getDesktop().browse(new URI(mailString));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		event.consume();
	}
}