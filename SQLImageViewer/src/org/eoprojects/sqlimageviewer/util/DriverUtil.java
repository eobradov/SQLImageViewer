package org.eoprojects.sqlimageviewer.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Contains methods used between other classes
 * 
 * @author Edi Obradovic
 *
 */
public class DriverUtil {
	
	/**
	 * Show alert information box with Alert header and custom message
	 * 
	 * @param message
	 */
	public static void showAlert(String message) {
	    Platform.runLater(new Runnable() {
	      public void run() {
	          Alert alert = new Alert(Alert.AlertType.INFORMATION);
	          alert.setTitle(DriverConstants.PROGRAM_NAME);
	          alert.setHeaderText("Alert:");
	          alert.setContentText(message);
	          alert.showAndWait();
	      }
	    });
	}
	
	/**
	 * Show alert information box with custom header and custom message
	 * 
	 * @param message
	 */
	public static void showAlert(String header, String message) {
	    Platform.runLater(new Runnable() {
	      public void run() {
	          Alert alert = new Alert(Alert.AlertType.INFORMATION);
	          alert.setTitle(DriverConstants.PROGRAM_NAME);
	          alert.setHeaderText(header);
	          alert.setContentText(message);
	          alert.showAndWait();
	      }
	    });
	}
	
	/**
	 * Show error information box with Error header and custom message
	 * 
	 * @param message
	 */
	public static void showError(String message) {
	    Platform.runLater(new Runnable() {
	      public void run() {
	          Alert alert = new Alert(Alert.AlertType.ERROR);
	          alert.setTitle(DriverConstants.PROGRAM_NAME);
	          alert.setHeaderText("Error:");
	          alert.setContentText(message);
	          alert.showAndWait();
	      }
	    });
	}
	
	/**
	 * Show error information box with custom header and custom message
	 * 
	 * @param message
	 */
	public static void showError(String header, String message) {
	    Platform.runLater(new Runnable() {
	      public void run() {
	          Alert alert = new Alert(Alert.AlertType.ERROR);
	          alert.setTitle(DriverConstants.PROGRAM_NAME);
	          alert.setHeaderText(header);
	          alert.setContentText(message);
	          alert.showAndWait();
	      }
	    });
	}
	
}
