package org.eoprojects.sqlimageviewer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import org.eoprojects.sqlimageviewer.util.DriverConstants;
import org.eoprojects.sqlimageviewer.util.SQLUtil;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for ImageViewer.fxml
 * 
 * @author Edi Obradovic
 * @version 1.0
 */
public class ImageViewerController {

	@SuppressWarnings("unused")
	private static Stage currentstage;

	@FXML
	private ImageView imageView;

	@FXML
	private Button executeButton;

	@FXML
	private Button previousButton;

	@FXML
	private Button nextButton;

	@FXML
	private Button firstButton;

	@FXML
	private Button lastButton;

	@FXML
	private Label statusLabel;

	@FXML
	private TextArea sqlQueryArea;

	@FXML
	private ProgressIndicator statusProgress;

	@FXML
	private MenuItem saveAsMenuItem;

	@FXML
	private MenuItem exitMenuItem;

	@FXML
	private MenuItem aboutMenuItem;

	@FXML
	private ScrollPane imageScrollPane;

	/**
	 * Called on GUI initialize
	 */
	@FXML
	private void initialize() {
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		statusLabel.setText("");
		statusProgress.setVisible(false);

		sqlQueryArea.setText("SELECT ImageName, Image FROM VCIMM_Images ORDER BY TIMESTAMP DESC");
		displayInfoImage(Main.class.getResourceAsStream("resources/images/imageIntro.png"));
	}

	/**
	 * Handle actions on GUI elements
	 * 
	 * @param event
	 */
	@FXML
	private void handleAction(ActionEvent event) {
		if (event.getSource().equals(previousButton)) {
			handlePreviousButton();
		} else if (event.getSource().equals(nextButton)) {
			handleNextButton();
		} else if (event.getSource().equals(firstButton)) {
			handleFirstButton();
		} else if (event.getSource().equals(lastButton)) {
			handleLastButton();
		} else if (event.getSource().equals(executeButton)) {
			executeQuery();
		} else if (event.getSource().equals(saveAsMenuItem)) {
			handleSaveAsMenu();
		} else if (event.getSource().equals(exitMenuItem)) {
			System.exit(0);
		} else if (event.getSource().equals(aboutMenuItem)) {
			handleAboutMenu();
		} else {
			handleUnknown();
		}
		event.consume();
	}
	
	private void handlePreviousButton() {
		if (SQLUtil.results != null) {
			try {
				if (SQLUtil.results.previous()) {
					statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
					statusLabel.setText("Row: " + SQLUtil.results.getRow());
					InputStream inputStream = SQLUtil.getBinaryFromRow();
					if (inputStream == null) {
						inputStream = SQLUtil.getBlobFromRow();
					}
					if (inputStream != null) {
						displayCollectedImage(inputStream);	
					} else {
						displayInfoImage(Main.class.getResourceAsStream("resources/images/imageNotFound.png"));
					}
				}
			} catch (Exception e) {
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
				statusLabel.setText(e.getMessage());
				displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
			}
		}
	}
	
	private void handleNextButton() {
		if (SQLUtil.results != null) {
			try {
				if (SQLUtil.results.next()) {
					statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
					statusLabel.setText("Row: " + SQLUtil.results.getRow());
					displayCollectedImage(SQLUtil.getBinaryFromRow());
				}
			} catch (Exception e) {
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
				statusLabel.setText(e.getMessage());
				displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
			}
		}
	}
	
	private void handleFirstButton() {
		if (SQLUtil.results != null) {
			try {
				if (SQLUtil.results.first()) {
					statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
					statusLabel.setText("Row: " + SQLUtil.results.getRow());
					displayCollectedImage(SQLUtil.getBinaryFromRow());
				}
			} catch (Exception e) {
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
				statusLabel.setText(e.getMessage());
				displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
			}
		}
	}
	
	private void handleLastButton() {
		if (SQLUtil.results != null) {
			try {
				if (SQLUtil.results.last()) {
					statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
					statusLabel.setText("Row: " + SQLUtil.results.getRow());
					displayCollectedImage(SQLUtil.getBinaryFromRow());
				}
			} catch (Exception e) {
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
				statusLabel.setText(e.getMessage());
				displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
			}
		}
	}
	
	private void handleSaveAsMenu() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		String fileName = SQLUtil.getStringFromRow();
		if (fileName != null) {
			fileChooser.setInitialFileName(fileName);
		}

		File file = fileChooser.showSaveDialog(null);
		if (file != null) {
			try {
				fileName = file.getName();
				String fileExtension = fileName.substring(fileName.indexOf(".") + 1, file.getName().length());
				if (fileExtension == null || fileExtension.trim().equals("")) {
					fileExtension = "jpg";
				}
				ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), fileExtension, file);
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
				statusLabel.setText("Saved");
			} catch (IOException e) {
				statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
				statusLabel.setText(e.getMessage());
			}
		}
	}
	
	private void handleAboutMenu() {
		BorderPane about;
		try {
			about = (BorderPane) FXMLLoader.load(Main.class.getResource("resources/fxml/About.fxml"));
			Stage stage = new Stage();
			stage.setTitle(DriverConstants.PROGRAM_TITLE);
			stage.setScene(new Scene(about, 450, 450));
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/images/logo.png")));
			stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent keyEvent) -> {
				if (KeyCode.ESCAPE == keyEvent.getCode()) {
					stage.close();
				}
			});
			stage.show();
		} catch (IOException e) {
			statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
			statusLabel.setText(e.getMessage().trim());
		}
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
		
		if (event.getSource().equals(previousButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				handlePreviousButton();
			}
		} else if (event.getSource().equals(nextButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleNextButton();
			}
		} else if (event.getSource().equals(firstButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleFirstButton();
			}
		} else if (event.getSource().equals(lastButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleLastButton();
			}
		} else if (event.getSource().equals(executeButton)) {
			if (event.getCode() == KeyCode.ENTER) {
				executeQuery();
			}
		} else if (event.getSource().equals(saveAsMenuItem)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleSaveAsMenu();
			}
		} else if (event.getSource().equals(exitMenuItem)) {
			if (event.getCode() == KeyCode.ENTER) {
				System.exit(0);
			}
		} else if (event.getSource().equals(aboutMenuItem)) {
			if (event.getCode() == KeyCode.ENTER) {
				handleAboutMenu();
			}
		}
		
		event.consume();
	}

	/**
	 * Configure availability for navigation buttons
	 */
	private void setPreviousNext() {
		if (SQLUtil.results != null) {
			try {
				if (SQLUtil.results.isFirst()) {
					firstButton.setDisable(true);
					previousButton.setDisable(true);
				} else {
					firstButton.setDisable(false);
					previousButton.setDisable(false);
				}
				if (SQLUtil.results.isLast()) {
					lastButton.setDisable(true);
					nextButton.setDisable(true);
				} else {
					lastButton.setDisable(false);
					nextButton.setDisable(false);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			firstButton.setDisable(true);
			previousButton.setDisable(true);
			lastButton.setDisable(true);
			nextButton.setDisable(true);
		}
	}

	/**
	 * Display info message
	 * 
	 * @param inputStream
	 */
	private void displayInfoImage(InputStream inputStream) {
		saveAsMenuItem.setDisable(true);
		displayImage(inputStream);
	}
	
	/**
	 * Display image from SQL
	 * 
	 * @param inputStream
	 */
	private void displayCollectedImage(InputStream inputStream) {
		saveAsMenuItem.setDisable(false);
		displayImage(inputStream);
	}

	/**
	 * Display image
	 * 
	 * @param inputStream
	 */
	private void displayImage(InputStream inputStream) {
		imageView.setImage(null);

		Image image = new Image(inputStream);

		// imageView.preserveRatioProperty().set(true);
		imageView.setFitHeight(0);
		imageView.setFitWidth(0);
		imageView.setImage(image);

		setPreviousNext();
	}

	/**
	 * Execute SQL query
	 */
	private void executeQuery() {
		Task<Void> taskExecuteQuery = new Task<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					SQLUtil.executeQuery(sqlQueryArea.getText());
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								if (SQLUtil.results != null) {
									if (SQLUtil.results.next()) {
										statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
										enableGUI("Row: " + SQLUtil.results.getRow());
										InputStream inputStream = SQLUtil.getBinaryFromRow();
										if (inputStream == null) {
											inputStream = SQLUtil.getBlobFromRow();
										}
										if (inputStream != null) {
											displayCollectedImage(inputStream);	
										} else {
											displayInfoImage(Main.class.getResourceAsStream("resources/images/imageNotFound.png"));
										}
									} else {
										statusLabel.setTextFill(Color.web(DriverConstants.COLOR_SUCCESS));
										enableGUI("No records found ...");
										displayInfoImage(
												Main.class.getResourceAsStream("resources/images/imageNotFound.png"));
									}
								} else {
									statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
									enableGUI("No results returned ...");
									displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
								}
							} catch (Exception e) {
								statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
								enableGUI(e.getMessage());
								displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
							}
						}
					});
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							statusLabel.setTextFill(Color.web(DriverConstants.COLOR_FAIL));
							enableGUI(e.getMessage());
							displayInfoImage(Main.class.getResourceAsStream("resources/images/imageError.png"));
						}
					});
				}
				return null;
			}
		};

		Thread collectDBThread = new Thread(taskExecuteQuery);
		collectDBThread.setDaemon(true);
		displayInfoImage(Main.class.getResourceAsStream("resources/images/imageSearch.png"));
		statusLabel.setTextFill(Color.web(DriverConstants.COLOR_NEUTRAL));
		disableGUI("Executing Query ...");
		collectDBThread.start();
	}

	/**
	 * Enable GUI elements, and display empty message in statusLabel
	 */
	private void enableGUI(String message) {
		statusProgress.setVisible(false);
		executeButton.setDisable(false);
		nextButton.setDisable(false);
		previousButton.setDisable(false);
		firstButton.setDisable(false);
		lastButton.setDisable(false);
		statusLabel.setText(message);
	}

	/**
	 * Disable GUI elements, and display empty message in statusLabel
	 */
	private void disableGUI(String message) {
		statusProgress.setVisible(true);
		executeButton.setDisable(true);
		nextButton.setDisable(true);
		previousButton.setDisable(true);
		firstButton.setDisable(true);
		lastButton.setDisable(true);
		statusLabel.setText(message);
	}

}
