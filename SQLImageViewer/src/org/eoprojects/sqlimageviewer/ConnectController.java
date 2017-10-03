package org.eoprojects.sqlimageviewer;

import java.io.IOException;
import java.util.ArrayList;

import org.eoprojects.sqlimageviewer.util.DriverConstants;
import org.eoprojects.sqlimageviewer.util.SQLUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ConnectController {

	private static final String EMPTY_LIST_ITEM = "----";

	private static Thread connectorThread = null;
	private static Stage currentstage;

	ObservableList<String> driverList = FXCollections.observableArrayList();
	ObservableList<String> databaseList = FXCollections.observableArrayList();

	@FXML
	private ChoiceBox<String> driverBox;

	@FXML
	private TextField serverField;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private ChoiceBox<String> databaseBox;

	@FXML
	private Button queryButton;

	@FXML
	private Button continueButton;

	@FXML
	private Label statusLabel;
	
	@FXML
	private Label logoLabel;

	@FXML
	private ProgressIndicator statusProgress;

	@FXML
	private void initialize() {
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		statusLabel.setText("");
		statusProgress.setVisible(false);

		driverList.add(DriverConstants.MICROSOFT_SQL_NAME);
		databaseList.add(EMPTY_LIST_ITEM);

		driverBox.setItems(driverList);
		driverBox.setValue(DriverConstants.MICROSOFT_SQL_NAME);

		serverField.setText("10.20.10.171\\SQLEXPRESS:1433");
		usernameField.setText("sa");
		passwordField.setText("W!nd0w5");

		databaseBox.setItems(databaseList);
		databaseBox.setValue(EMPTY_LIST_ITEM);
		
		logoLabel.setText("");
		Image logoImage = new Image(Main.class.getResourceAsStream("resources/logo.png"));
		logoLabel.setGraphic(new ImageView(logoImage));
	}

	@FXML
	private void handleAction(ActionEvent event) {
		Node source = (Node) event.getSource(); 
	    currentstage  = (Stage) source.getScene().getWindow();
	    
		if (event.getSource().equals(continueButton)) {
			connectSQL(true);

		} else if (event.getSource().equals(queryButton)) {
			connectSQL(false);
			collectDB();

		} else {
			statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
			statusLabel.setText(event.toString());
		}
	}

	private void collectDB() {

		Task<Void> taskCollectDB = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				while (connectorThread != null && connectorThread.isAlive()) {
					Thread.sleep(250);
				}
				
				if (SQLUtil.conn != null) {
					ArrayList<String> databases = SQLUtil.getDatabases();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							databaseList.clear();
							for (String database : databases) {
								databaseList.add(database);
							}
							statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
							enableGUI("");
							databaseBox.setValue(databaseList.get(0));
						}
					});
				}
				return null;
			}
		};

		Thread collectDBThread = new Thread(taskCollectDB);
		collectDBThread.setDaemon(true);
		collectDBThread.start();
	}

	private void connectSQL(boolean loadNext) {
		
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		disableGUI("Connecting to server ...");

		String driver = DriverConstants.getDriverValue(driverBox.getSelectionModel().getSelectedItem().toString());
		String connectionPrefix = DriverConstants.getConnectionPrefix(driverBox.getSelectionModel().getSelectedItem().toString());
		String server = serverField.getText();
		String username = usernameField.getText();
		String password = passwordField.getText();
		String database = databaseBox.getSelectionModel().getSelectedItem().toString();

		String missingParameters = "";

		// -----

		if (driver == null || driver.trim().equals("")) {
			if (!missingParameters.equals("")) {
				missingParameters += ", ";
			}
			missingParameters += "driver";
		}

		if (server == null || server.trim().equals("")) {
			if (!missingParameters.equals("")) {
				missingParameters += ", ";
			}
			missingParameters += "server";
		}

		if (username == null || username.trim().equals("")) {
			if (!missingParameters.equals("")) {
				missingParameters += ", ";
			}
			missingParameters += "username";
		}

		if (!missingParameters.equals("")) {
			missingParameters = "Missing parameters: " + missingParameters;
			statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
			enableGUI(missingParameters);
			return;
		}

		// -----

		Task<Void> taskConnect = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				String connection = "";

				if (connectionPrefix == null || connectionPrefix.trim().equals("")) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
							enableGUI("Unable to create connection string ...");
						}
					});
					return null;
				} else {
					connection = connectionPrefix + server.trim();
				}

				if (database != null && !database.trim().equals("") && !database.trim().equals(EMPTY_LIST_ITEM)) {
					connection += ";databaseName=" + database;
				}

				SQLUtil.setDriver(driver);
				SQLUtil.setConnectionString(connection);
				SQLUtil.setUsername(username);
				SQLUtil.setPassword(password);

				try {					
					SQLUtil.disconnect();
					SQLUtil.connect();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
							enableGUI();
							if (loadNext) {
								BorderPane imageViewer;
								try {
									imageViewer = (BorderPane)FXMLLoader.load(getClass().getResource("ImageViewer.fxml"));
									Stage stage = new Stage();
						            stage.setTitle(DriverConstants.PROGRAM_TITLE);
						            stage.setScene(new Scene(imageViewer, 640, 480));
						            stage.setMaximized(true);
						            stage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/logo.png")));
						            stage.show();
						            currentstage.close();
								} catch (IOException e) {
									statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
									enableGUI(e.getMessage());
								}	
							}
						}
					});
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
							enableGUI(e.getMessage());
						}
					});
				}
				return null;
			}
		};

		connectorThread = new Thread(taskConnect);
		connectorThread.setDaemon(true);
		connectorThread.start();
	}
	
	private void enableGUI() {
		enableGUI("");
	}
	
	private void enableGUI(String message) {
		statusProgress.setVisible(false);
		statusLabel.setText(message);
		continueButton.setDisable(false);
		queryButton.setDisable(false);
		driverBox.setDisable(false);
		databaseBox.setDisable(false);
		serverField.setDisable(false);
		usernameField.setDisable(false);
		passwordField.setDisable(false);

		if (databaseList.isEmpty()) {
			databaseList.add(EMPTY_LIST_ITEM);
			databaseBox.setValue(databaseList.get(0));
		}

	}

	private void disableGUI(String message) {
		statusProgress.setVisible(true);
		statusLabel.setText(message);
		continueButton.setDisable(true);
		queryButton.setDisable(true);
		driverBox.setDisable(true);
		databaseBox.setDisable(true);
		serverField.setDisable(true);
		usernameField.setDisable(true);
		passwordField.setDisable(true);
	}

}