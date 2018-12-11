package com.radek.statki;

import java.util.ArrayList;

import com.radek.statki.model.GameModel;
import com.radek.statki.view.ShipController;

import javafx.application.Application;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.*;

/**
 * Klasa main implementuj¹ca glowny program aplikacji.
 * 
 * @author Radoslaw Tuzimek
 * @version 1.0
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
				stage.setTitle("Statki_B");
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
