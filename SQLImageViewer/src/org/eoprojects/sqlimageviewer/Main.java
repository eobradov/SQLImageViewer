package org.eoprojects.sqlimageviewer;

import org.eoprojects.sqlimageviewer.util.DriverConstants;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/**
 * SQL Image Viewer main class
 * <BR>Images and icons taken from: <a href="https://www.flaticon.com/">www.flaticon.com</a>
 * 
 * @author Edi Obradovic
 * @version 1.0
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("Connect.fxml"));
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle(DriverConstants.PROGRAM_TITLE);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/images/logo.png")));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
