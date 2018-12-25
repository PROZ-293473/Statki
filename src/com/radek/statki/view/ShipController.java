package com.radek.statki.view;

import java.net.URL;

import java.util.ResourceBundle;

import com.radek.statki.atj.ConsumerA;
import com.radek.statki.atj.ProducerA;
import com.radek.statki.model.GameModel;
import com.radek.statki.model.GameModel.State;
import com.radek.statki.model.Ship;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Klasa implementujaca kontroler we wzorcu MVC. Implementuje interfejs
 * uzytkownika, odbiera polecenia, konwertuje i przekazuje do modelu lub widoku.
 *
 * @author Radoslaw Tuzimek
 * @version 2.0
 */

public class ShipController implements Initializable {

	private GameModel model;
	private String queue1;
	private String queue2;
	private ProducerA producer = null;
	private ConsumerA consumer = null;

	// Deklaracja wyswietlaczy
	@FXML
	public TextField tfWysUp;
	@FXML
	public TextField tfWysDown;

	// Deklaracja plansz
	@FXML
	public Canvas canvasOpponent;
	@FXML
	public Canvas canvasGamer;

	@FXML
	public CheckBox ch1;
	@FXML
	public CheckBox ch2;

	/**
	 * Metoda zwraca 6 znakowy naglowek wiadomosci
	 */
	private String header(String message) {
		if (message.length() > 6) {
			return message.substring(0, 6);
		}
		return "";
	}

	/**
	 * Metoda zwraca wiadomosc bez naglowka
	 */
	private String trunc(String message) {
		return message.substring(6);
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
																								// dolna // [ok]
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

	/**
	 * Metody umozliwiajace awaryjny wybor graczas
	 */
	@FXML
	public void click_ch1() {
		ch1.setSelected(true);
		ch2.setSelected(false);
	}

	@FXML
	public void click_ch2() {
		ch2.setSelected(true);
		ch1.setSelected(false);
	}

	/**
	 * Metoda reagujaca na klikniecie zielonego przycisku
	 */
	@FXML
	public void click_buttonGreen() {
		if (model.game == State.start) {
			boolean anySelected = false;
			if (ch2.isSelected()) {
				anySelected = true;
				queue1 = "ATJQueue";
				queue2 = "ATJ2Queue";
			}
			if (ch1.isSelected()) {
				anySelected = true;
				queue1 = "ATJ2Queue";
				queue2 = "ATJQueue";
			}
			if (!anySelected) {
				// obsluzenie niewybrania checkboxa jesli przypisanie graczy nie powiedzie sie
				System.out.println("FAIL");
			}
			model.click();
			repaintGamerBoard();
			setScreans();
		}
	}

	public boolean receiveShot(String message) {
		if (model.game == State.turnP) {

			GraphicsContext gg = canvasGamer.getGraphicsContext2D();
			gg.setLineWidth(1);
			gg.setFill(Color.AQUA);
			gg.setStroke(Color.BLACK);
			gg.fillRect(Character.getNumericValue(message.charAt(0)) * 25,
					Character.getNumericValue(message.charAt(2)) * 25, 25, 25);
			drawGrid(canvasGamer);

			if (model.checkShotOpponent(Character.getNumericValue(message.charAt(0)),
					Character.getNumericValue(message.charAt(2))) == false) {
				model.game = State.turnG;
				return false;
			} else {
				model.game = State.turnP;
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodaodbiera wiadomosci z naglowkiem i czysci wszystkie inne, a jesli nie
	 * odebralo poprawnej to wysyla taka dla drugiego klient i na tej podstawie
	 * dokonuje sie przypisania numeru gracza (1 lub 2) oraz czysci kolejki
	 */

	public void discrimination() {
		ConsumerA tempConsumer1 = null;
		ConsumerA tempConsumer2 = null;
		ProducerA tempProducer1 = null;
		try {
			tempConsumer1 = new ConsumerA("localhost:7676/jms", "ATJQueue", this);
			tempConsumer2 = new ConsumerA("localhost:7676/jms", "ATJ2Queue", this);
			tempProducer1 = new ProducerA("localhost:7676/jms", "ATJQueue");
			String message1 = "";
			String message2 = "";
			while (message2 != null) {
				message2 = tempConsumer2.receiveQueueMessage();
			}
			while (message1 != null) {
				if (header(message1).equals("DISCRI")) {
					message1 = trunc(message1);
					if (Integer.parseInt(message1) == 2) {
						ch2.setSelected(true);
						ch1.setSelected(false);
						ch1.setDisable(true);
						ch2.setDisable(true);
					}
				}
				message1 = tempConsumer1.receiveQueueMessage();
			}
			if (ch1.isSelected() == false && ch2.isSelected() == false) {
				ch1.setSelected(true);
				ch2.setSelected(false);
				ch1.setDisable(true);
				ch2.setDisable(true);
				tempProducer1.sendQueueMessage("DISCRI" + Integer.toString(2));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				tempConsumer1.finalize();
				tempConsumer2.finalize();
				tempProducer1.finalize();
			} catch (Exception ex2) {
			}
		}
	}

	public boolean sendShot(int x, int y) {
		producer.sendQueueMessage(String.valueOf(x) + " " + String.valueOf(y));
		if (!model.checkShotGamer(x, y)) {
			model.game = State.turnP;
			return false;
		}
		return true;
	}

	public void sendGamerBoard() {
		producer.sendQueueMessage("CREATE" + model.gamerBoard.getShip(0).printToString() + " "
				+ model.gamerBoard.getShip(1).printToString() + " " + model.gamerBoard.getShip(2).printToString() + " "
				+ model.gamerBoard.getShip(3).printToString());
	}

	public void onMessageReceived(String message) {
		if (header(message).equals("CREATE")) {
			message = trunc(message);
			model.createOpponentShips(message);
		} else {
			if (model.game == State.turnP) {
				if (receiveShot(message) == false) {
					model.game = State.turnG;
				}
				setScreans();
				repaintGamerBoard();
			}
			if (model.game == State.handshake) {
				if (header(message).equals("HANDSH")) {
					message = trunc(message);
					if (message.compareTo(Double.toString(model.playerRoll)) > 0) {
						model.game = State.turnG;
						model.textUp = "Zaczynasz.";
					} else {
						model.game = State.turnP;
						model.textUp = "Przeciwnik zaczyna...";
					}
					repaintGamerBoard();
					setScreans();
				}
			}
		}
	}

	public void startMessageListener() {
		model.playerRoll = Math.random();
		try {
			consumer = new ConsumerA("localhost:7676/jms", queue1, this);
			Task<Void> task = new Task<Void>() {
				@Override
				public Void call() {
					try {
						consumer.receiveQueueMessageAsync();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			new Thread(task).start();

			producer = new ProducerA("localhost:7676/jms", queue2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fieldClickedOpponent(double mouseX, double mouseY) {
		int boardX = (int) (mouseX / (canvasOpponent.getWidth() / 10));
		int boardY = (int) (mouseY / (canvasOpponent.getHeight() / 10));

		if (model.game == State.turnG) {
			if (sendShot(boardX, boardY)) {
				repaintOpponentBoard();
			} else {
				GraphicsContext gc = canvasOpponent.getGraphicsContext2D();
				gc.setFill(Color.AQUA);
				gc.setStroke(Color.BLACK);
				gc.fillRect(boardX * 25, boardY * 25, 25, 25);
				drawGrid(canvasOpponent);
			}

			repaintGamerBoard();
			setScreans();
		}
	}

	public void fieldClickedGamer(double mouseX, double mouseY) {
		int boardX = (int) (mouseX / (canvasOpponent.getWidth() / 10));
		int boardY = (int) (mouseY / (canvasOpponent.getHeight() / 10));

		if (model.game == State.entry4 || model.game == State.entry3 || model.game == State.entry2
				|| model.game == State.entry1) {
			if (model.addMastToShip(boardX, boardY)) {
				repaintGamerBoard();
			}
			if (model.game == State.handshake) {
				setScreans();
				repaintGamerBoard();

				startMessageListener();
				producer.sendQueueMessage("HANDSH" + Double.toString(model.playerRoll));
				sendGamerBoard();
			}
			setScreans();
		}
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
		drawGrid(canvasGamer);
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
		drawGrid(canvasOpponent);
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

		discrimination();
	}
}
