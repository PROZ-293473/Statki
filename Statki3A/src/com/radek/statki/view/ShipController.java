package com.radek.statki.view;

import java.net.URL;

import java.util.ResourceBundle;

import javax.jms.JMSException;

import com.radek.statki.Main;
import com.radek.statki.atj.ConsumerA;
import com.radek.statki.atj.ProducerA;
import com.radek.statki.model.GameModel;
import com.radek.statki.model.GameModel.State;
import com.radek.statki.model.Ship;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
//import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
//import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Klasa implementujaca kontroler we wzorcu MVC. Implementuje interfejs
 * uzytkownika, odbiera polecenia, konwertuje i przekazuje do modelu lub widoku.
 *
 * @author Radoslaw Tuzimek
 * @version 1.0
 */

public class ShipController implements Initializable {

	private GameModel model;

	// Deklaracja wyswietlaczy

	@FXML
	public TextField tfWysUp;
	@FXML
	public TextField tfWysDown;
	@FXML
	public Canvas canvasOpponent;
	@FXML
	public Canvas canvasGamer;

	/**
	 * Metoda reagujaca na klikniecie zielonego przycisku
	 */

	@FXML
	public void click_buttonGreen() {

		if (model.game == State.upgradeData) {
			tfWysUp.setText("wysylanie danych...");
		}

		if (model.game == State.turnP) {
			if (model.receiveShot() == false) {
				String info = model.getLastMessage();
				GraphicsContext gc = canvasGamer.getGraphicsContext2D();
				gc.setFill(Color.AQUA);
				gc.setStroke(Color.BLACK);
				gc.fillRect(Character.getNumericValue(info.charAt(0)) * 25,
						Character.getNumericValue(info.charAt(2)) * 25, 25, 25);

				drawGrid(canvasOpponent);
			}
		} else {
			model.click();

		}
		repaintGamerBoard();
		drawGrid(canvasGamer);
		setScreans();
		model.setTurnName();
	}

	private void drawGrid(Canvas canvas) {

		GraphicsContext gc = canvas.getGraphicsContext2D();

		// vertical lines
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1);

		for (int i = 0; i < 10; i++) {
			double currentX = i * canvas.getWidth() / 10;
			double currentX1 = (i + 1) * canvas.getWidth() / 10;
			double currentY = i * canvas.getHeight() / 10;
			double currentY1 = (i + 1) * canvas.getHeight() / 10;
			if (i != 0) {
				gc.strokeLine(currentX, 0, currentX, canvas.getWidth());
				gc.strokeLine(0, currentY, canvas.getHeight(), currentY);
			}
			if (i != 9) {
				gc.strokeLine(currentX1, 0, currentX1, canvas.getWidth());
				gc.strokeLine(0, currentY1, canvas.getHeight(), currentY1);
			}
		}
		gc.setLineWidth(2);
		gc.strokeLine(0 + 1, 0, 0 + 1, canvas.getHeight()); // pionowa lewa [ok]
		gc.strokeLine(0, canvas.getHeight() - 1, canvas.getWidth(), canvas.getHeight() - 1); // pozioma
																								// dolna
																								// [ok]
		gc.strokeLine(0, 0 + 1, canvas.getWidth(), 0 + 1); // pozioma gorna [ok]
		gc.strokeLine(canvas.getWidth() - 1, 0, canvas.getWidth() - 1, canvas.getHeight()); // pionowa
																							// prawa
																							// [ok]

	}

	private void paintBackground(Canvas canvas) {

		GraphicsContext gc = canvas.getGraphicsContext2D();

		gc.setFill(Color.BLUE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

	}

	public void fieldClickedOpponent(double mouseX, double mouseY) {
		int boardX = (int) (mouseX / (canvasOpponent.getWidth() / 10));
		int boardY = (int) (mouseY / (canvasOpponent.getHeight() / 10));

		if (model.game == State.turnG) {
			if (model.checkShotGamer(boardX, boardY)) {
				repaintOpponentBoard();
				drawGrid(canvasOpponent);
			} else {
				GraphicsContext gc = canvasOpponent.getGraphicsContext2D();
				gc.setFill(Color.AQUA);
				gc.setStroke(Color.BLACK);
				gc.fillRect(boardX * 25, boardY * 25, 25, 25);
				drawGrid(canvasOpponent);
			}

			model.sendShot(boardX, boardY);

			repaintGamerBoard();
			drawGrid(canvasGamer);
			setScreans();

//		if (model.game == State.turnP) {
//			try {
//				System.out.println("wait");
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			model.receiveShot();
//			repaintGamerBoard();
//			drawGrid(canvasGamer);
//			setScreans();
//			System.out.println("czekam na dane od przeciwnika!");
//		}
		}
	}

	public void fieldClickedGamer(double mouseX, double mouseY) {
		int boardX = (int) (mouseX / (canvasOpponent.getWidth() / 10));
		int boardY = (int) (mouseY / (canvasOpponent.getHeight() / 10));

		if (model.game == State.entry4 || model.game == State.entry3 || model.game == State.entry2
				|| model.game == State.entry1) {
			if (model.addMastToShip(boardX, boardY)) {
				repaintGamerBoard();
				drawGrid(canvasGamer);
			}
		}

		setScreans();
	}

	public void repaintGamerBoard() {
		GraphicsContext gc = canvasGamer.getGraphicsContext2D();
		for (Ship ship : model.gamerBoard.getShips()) {
			for (int i = 0; i < ship.currentMasts; i++) {
				gc.setLineWidth(1);
				if (ship.shots[i] == 0) {
					gc.setFill(Color.GRAY);
					gc.setStroke(Color.BLACK);
				}
				if (ship.shots[i] == 1) {
					gc.setFill(Color.ORANGE);
					gc.setStroke(Color.BLACK);
				}
				if (ship.shots[i] == 2) {
					gc.setFill(Color.RED);
					gc.setStroke(Color.BLACK);
				}
				gc.fillRect(ship.coordinates[i][0] * 25, ship.coordinates[i][1] * 25, 25, 25);
			}
		}
	}

	public void repaintOpponentBoard() {
		GraphicsContext oc = canvasOpponent.getGraphicsContext2D();
		for (Ship ship : model.opponentBoard.getShips()) {
			for (int i = 0; i < ship.currentMasts; i++) {
				oc.setLineWidth(1);
				if (ship.shots[i] == 1) {
					oc.setFill(Color.ORANGE);
					oc.setStroke(Color.BLACK);
					oc.fillRect(ship.coordinates[i][0] * 25, ship.coordinates[i][1] * 25, 25, 25);
				}
				if (ship.shots[i] == 2) {
					oc.setFill(Color.RED);
					oc.setStroke(Color.BLACK);
					oc.fillRect(ship.coordinates[i][0] * 25, ship.coordinates[i][1] * 25, 25, 25);
				}

			}
		}
	}

	public void setScreans() {
		tfWysUp.setText(model.getTextUp());
		tfWysDown.setText(model.getTextDown());
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.model = new GameModel();

		canvasOpponent.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				fieldClickedOpponent(event.getX(), event.getY());
			}
		});

		canvasGamer.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				fieldClickedGamer(event.getX(), event.getY());
			}
		});

		paintBackground(canvasOpponent);
		paintBackground(canvasGamer);

		drawGrid(canvasOpponent);
		drawGrid(canvasGamer);
	}
}
