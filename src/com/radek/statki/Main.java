package com.radek.statki;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Klasa main implementuj¹ca glowny program aplikacji.
 * 
 * @author Radoslaw Tuzimek
 * @version 2.0
 */

public class Main extends Application {

	/**
	 * Metoda start otwiera glowne okno aplikacji.
	 */
	@Override
	public void start(Stage stage) {
		try {
			ViewLoader<Parent, Object> viewLoader = new ViewLoader<Parent, Object>("view/Ship.fxml");
			Parent root = viewLoader.getElement();

			if (root != null) {
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setTitle("STATKI");
				stage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
					public void handle(WindowEvent we) {
						System.exit(0);
					}
				});
			}

			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metoda main, rozpoczyna program.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
