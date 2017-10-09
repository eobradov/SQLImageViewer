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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Controller for Connect.fxml
 *  
 * @author Edi Obradovic
 * @version 1.0
 */
public class ConnectController {

	private static Thread connectorThread = null;
	private static Stage currentstage;

	ObservableList<String> driverList = FXCollections.observableArrayList();
	ObservableList<String> databaseList = FXCollections.observableArrayList();

	@FXML
	private ComboBox<String> driverBox;

	@FXML
	private TextField serverField;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private ComboBox<String> databaseBox;

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
	
	/**
	 * Called on GUI initialize
	 */
	@FXML
	private void initialize() {
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		statusLabel.setText("");
		statusProgress.setVisible(false);

		driverList.add(DriverConstants.MICROSOFT_SQL_NAME);
		driverList.add(DriverConstants.MYSQL_NAME);
		databaseList.add(DriverConstants.EMPTY_LIST_ITEM);

		driverBox.setItems(driverList);
		driverBox.setValue(DriverConstants.MICROSOFT_SQL_NAME);
		
		String selectedItem = driverBox.getSelectionModel().getSelectedItem();
		serverField.setText(DriverConstants.getDefaultServer(selectedItem));
		usernameField.setText(DriverConstants.getDefaultUsername(selectedItem));
		passwordField.setText("");
		
		databaseBox.setItems(databaseList);
		databaseBox.setValue(DriverConstants.EMPTY_LIST_ITEM);
		
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
		Node source = (Node) event.getSource(); 
	    currentstage  = (Stage) source.getScene().getWindow();
	    
		if (event.getSource().equals(continueButton)) {
			connectSQL(true);
		} else if (event.getSource().equals(queryButton)) {
			connectSQL(false);
			collectDB();
		} else if (event.getSource().equals(driverBox)) {
			handleDriverBox();
		} else {
			handleUnknown();
		}
		event.consume();
	}
	
	private void handleDriverBox() {
		String selectedItem = driverBox.getSelectionModel().getSelectedItem();
		serverField.setText(DriverConstants.getDefaultServer(selectedItem));
		usernameField.setText(DriverConstants.getDefaultUsername(selectedItem));
		passwordField.setText("");
		
		databaseList.clear();
		databaseList.add(DriverConstants.EMPTY_LIST_ITEM);
		databaseBox.setValue(DriverConstants.EMPTY_LIST_ITEM);
	}
	
	private void handleUnknown() {
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
		statusLabel.setText("Action not defined ...");
	}
	
	/**
	 * Handle key actions on GUI elements
	 * 
	 * @param event
	 */
	@FXML
	private void handleKeyEvent(KeyEvent event) {
		Node source = (Node) event.getSource(); 
	    currentstage  = (Stage) source.getScene().getWindow();
	    
		if (event.getSource().equals(continueButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				connectSQL(true);
			}
		} else if (event.getSource().equals(queryButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				connectSQL(false);
				collectDB();		
			}
		} else if (event.getSource().equals(driverBox)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleDriverBox();
			}
		}
			
		event.consume();
	}

	/**
	 * Get list of databases from SQL server
	 */
	private void collectDB() {

		Task<Void> taskCollectDB = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				while (connectorThread != null && connectorThread.isAlive()) {
					Thread.sleep(250);
				}
				
				if (SQLUtil.conn != null) {
					ArrayList<String> databases = SQLUtil.getDatabases(driverBox.getSelectionModel().getSelectedItem().toString());
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

	/**
	 * Connect to SQL server
	 * 
	 * @param loadNext
	 */
	private void connectSQL(boolean loadNext) {
		
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		disableGUI("Connecting to server ...");

		String driver = DriverConstants.getDriverValue(driverBox.getSelectionModel().getSelectedItem().toString());
		String connectionPrefix = DriverConstants.getConnectionPrefix(driverBox.getSelectionModel().getSelectedItem().toString());
		String server = serverField.getText();
		String username = usernameField.getText();
		String password = passwordField.getText();

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
				
				if (loadNext) {
					String database = databaseBox.getSelectionModel().getSelectedItem().toString();
					if (database != null && !database.trim().equals("") && !database.trim().equals(DriverConstants.EMPTY_LIST_ITEM)) {
						connection = SQLUtil.addDatabaseToConnection(driverBox.getSelectionModel().getSelectedItem().toString(), connection, database);
					}
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
									imageViewer = (BorderPane)FXMLLoader.load(Main.class.getResource("resources/fxml/ImageViewer.fxml"));
									Stage stage = new Stage();
						            stage.setTitle(DriverConstants.PROGRAM_TITLE);
						            stage.setScene(new Scene(imageViewer, 640, 480));
						            stage.setMaximized(true);
						            stage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/images/logo.png")));
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
	
	/**
	 * Enable GUI elements, and display empty message in statusLabel
	 */
	private void enableGUI() {
		enableGUI("");
	}
	
	/**
	 * Enable GUI elements, and display message in statusLabel
	 * 
	 * @param message
	 */
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
			databaseList.add(DriverConstants.EMPTY_LIST_ITEM);
			databaseBox.setValue(databaseList.get(0));
		}
	}
	
	/**
	 * Disable GUI elements, and display message in statusLabel
	 * 
	 * @param message
	 */
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
