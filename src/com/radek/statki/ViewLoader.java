package com.radek.statki;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 * Generyczna klasa ViewLoader, ktorej konstruktor pobiera sciezke do pliku
 * FXML. Zawiera dwie metody, ktore zwracaj¹ referencje do ukladu (layout)
 * widoku i kontrolera.
 * 
 * @author Radoslaw Tuzimek
 * @version 2.0
 */

public class ViewLoader<T, U> {

	private T element = null;
	private U fxmlController = null;

	/**
	 * Konstruktor pobiera sciezke do pliku FXML.
	 */
	public ViewLoader(String fxml) {
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));

			element = fxmlLoader.load();

			fxmlController = fxmlLoader.getController();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Metoda zwracajaca referencje do ukladu (layout) widoku.
	 */
	public T getElement() {
		return element;
	}

	/**
	 * Metoda zwracajaca referencje do kontrolera.
	 */
	public U getController() {
		return fxmlController;
	}
}