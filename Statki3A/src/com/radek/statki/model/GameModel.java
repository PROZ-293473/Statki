package com.radek.statki.model;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import com.radek.statki.Main;
import com.radek.statki.atj.ConsumerA;
import com.radek.statki.atj.ProducerA;
import com.radek.statki.model.GameModel.State;
import com.radek.statki.view.ShipController;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

/**
 * Klasa implementujaca model we wzorcu MVC. Zarzadza danymi i odpowiada za
 * wykonywanie roznych zadan w grze statki.
 * 
 * @author Radoslaw Tuzimek
 * @version 1.0
 */

public class GameModel {

	public enum State {
		start, entry4, entry3, entry2, entry1, upgradeData, turnG, turnP, end
	};

	private String textUp;
	private String textDown;
	private int shotedOpponentShips;
	private int shotedGamerShips;
	public State game;
	private String lastMessage;
	public Board gamerBoard = new Board();
	public Board opponentBoard = new Board();

	public String test;

	/**
	 * Konstruktor bezparametrowy. Ustawia wszystkie zmienne i zmienne pomocnicze na
	 * domyslne poczatkowe wartosci.
	 */

	public GameModel() {

		game = State.start;
		textUp = "Witaj w grze statki, aby rozpocz¹æ naciœnij zielony przycisk.";
		textDown = "";
		shotedOpponentShips = 0;
		shotedGamerShips = 0;
		lastMessage = "";
	}

	public String process(String information) {

		lastMessage = information;

		return information;
	}

	public String getTextUp() {
		return textUp;
	}

	public String getTextDown() {
		return textDown;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void click() {

		if (game == State.start) {

			textUp = "Na planszy gracza umieœæ statek o d³ugoœci 4";

			gamerBoard.addShip(new Ship(4));
			gamerBoard.addShip(new Ship(3));
			gamerBoard.addShip(new Ship(2));
			gamerBoard.addShip(new Ship(1));
			game = State.entry4;

		} else if (game == State.upgradeData) {
			// wyslij swoje dane i odbierz dane przeciwnika
			sendGamerBoard();
			receiveOpponentBoard();
			game = State.turnG;
			textUp = "Twoja tura.";
		}

	}

	public boolean checkVicinity(int index, int x, int y) {
		for (int i = 0; i < gamerBoard.getShips().size(); i++) {
			if (i != index) {
				if (!gamerBoard.getShip(i).validateVicinity(x, y))
					return false;
			}
		}
		return true;
	}

	public boolean addMastToShip(int x, int y) {

		boolean result = false;
		if (game == State.entry4) {

			result = gamerBoard.getShip(0).addMast(x, y);
			if (gamerBoard.getShip(0).isComplete()) {
				game = State.entry3;
				textUp = "Na planszy gracza umieœæ statek o d³ugoœci 3";
			}
		} else if (game == State.entry3) {

			if (checkVicinity(1, x, y)) {
				result = gamerBoard.getShip(1).addMast(x, y);
				if (gamerBoard.getShip(1).isComplete()) {
					game = State.entry2;
					textUp = "Na planszy gracza umieœæ statek o d³ugoœci 2";
				}
			}

		} else if (game == State.entry2) {

			if (checkVicinity(2, x, y)) {
				result = gamerBoard.getShip(2).addMast(x, y);
				if (gamerBoard.getShip(2).isComplete()) {
					game = State.entry1;
					textUp = "Na planszy gracza umieœæ statek o d³ugoœci 1, a nastêpnie naciœnij zielony przycisk.";
				}
			}

		} else if (game == State.entry1) {

			if (checkVicinity(3, x, y)) {
				result = gamerBoard.getShip(3).addMast(x, y);
				if (gamerBoard.getShip(3).isComplete()) {
					game = State.upgradeData;
				}
			}

		}

		return result;

	}

	public void createOpponentShips(String info) {

		opponentBoard.addShip(new Ship(4));
		opponentBoard.addShip(new Ship(3));
		opponentBoard.addShip(new Ship(2));
		opponentBoard.addShip(new Ship(1));

		opponentBoard.getShip(0).addMast(Character.getNumericValue(info.charAt(0)),
				Character.getNumericValue(info.charAt(1)));
		opponentBoard.getShip(0).addMast(Character.getNumericValue(info.charAt(3)),
				Character.getNumericValue(info.charAt(4)));
		opponentBoard.getShip(0).addMast(Character.getNumericValue(info.charAt(6)),
				Character.getNumericValue(info.charAt(7)));
		opponentBoard.getShip(0).addMast(Character.getNumericValue(info.charAt(9)),
				Character.getNumericValue(info.charAt(10)));

		opponentBoard.getShip(1).addMast(Character.getNumericValue(info.charAt(13)),
				Character.getNumericValue(info.charAt(14)));
		opponentBoard.getShip(1).addMast(Character.getNumericValue(info.charAt(16)),
				Character.getNumericValue(info.charAt(17)));
		opponentBoard.getShip(1).addMast(Character.getNumericValue(info.charAt(19)),
				Character.getNumericValue(info.charAt(20)));

		opponentBoard.getShip(2).addMast(Character.getNumericValue(info.charAt(23)),
				Character.getNumericValue(info.charAt(24)));
		opponentBoard.getShip(2).addMast(Character.getNumericValue(info.charAt(26)),
				Character.getNumericValue(info.charAt(27)));

		opponentBoard.getShip(3).addMast(Character.getNumericValue(info.charAt(30)),
				Character.getNumericValue(info.charAt(31)));

	}

	public boolean checkShotGamer(int x, int y) {

		for (int i = 0; i < 4; i++) {
			if (opponentBoard.getShip(i).shoot(x, y) == 1 || opponentBoard.getShip(i).shoot(x, y) == -1) {
				textUp = "Trafi³eœ strzel ponownie.";
				textDown = "TRAFIONY!";
				if (opponentBoard.getShip(i).isDead()) {
					shotedOpponentShips++;
					textDown = "ZATOPIONY!";
				}
				if (shotedOpponentShips == 4) {
					finishGame();
				}
				return true;
			}
		}

		game = State.turnP;
		textUp = "Tura przecwnika.";
		textDown = "PUD£O!";
		return false;
	}

	public boolean checkShotOpponent(int x, int y) {

		for (int i = 0; i < 4; i++) {
			if (gamerBoard.getShip(i).shoot(x, y) == 1 || gamerBoard.getShip(i).shoot(x, y) == -1) {
				textUp = "Przeciwnik trafi³ strzela ponownie.";
				textDown = "TRAFIONY!";
				if (gamerBoard.getShip(i).isDead()) {
					shotedGamerShips++;
					textDown = "ZATOPIONY!";
				}
				if (shotedGamerShips == 4) {
					finishGame();
				}
				return true;
			}
		}

		game = State.turnG;
		textUp = "Tura gracza.";
		textDown = "PUD£O!";
		return false;
	}

	public void sendGamerBoard() {
		ProducerA producer = null;
		try {
			producer = new ProducerA("localhost:7676/jms", "ATJQueue");
			producer.sendQueueMessage(
					gamerBoard.getShip(0).printToString() + " " + gamerBoard.getShip(1).printToString() + " "
							+ gamerBoard.getShip(2).printToString() + " " + gamerBoard.getShip(3).printToString());
			System.out.println("A wyslalem staki");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				producer.finalize();
			} catch (Exception ex) {
			}
		}
	}

	public void sendShot(int x, int y) {
		ProducerA producer = null;
		try {
			producer = new ProducerA("localhost:7676/jms", "ATJQueue");
			producer.sendQueueMessage(String.valueOf(x) + " " + String.valueOf(y));
			System.out.println("A wyslalem strzal" + String.valueOf(x) + " " + String.valueOf(y));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				producer.finalize();
			} catch (Exception ex) {
			}
		}
	}

	public void receiveOpponentBoard() {
		ConsumerA consumer = null;
		try {

			consumer = new ConsumerA("localhost:7676/jms", "ATJ2Queue", this);
			consumer.receiveQueueMessageAsync();
			createOpponentShips(lastMessage);
			System.out.println("A odebra³em:" + lastMessage);
			lastMessage = "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				consumer.finalize();
			} catch (Exception ex) {
			}
		}

	}

	public boolean receiveShot() {
		ConsumerA consumer = null;
		try {
			consumer = new ConsumerA("localhost:7676/jms", "ATJ2Queue", this);
			consumer.receiveQueueMessageAsync();
			if (game == State.turnP) {
				if (checkShotOpponent(Character.getNumericValue(lastMessage.charAt(0)),
						Character.getNumericValue(lastMessage.charAt(2))) == false) {
					game = State.turnG;
					System.out.println("A:dosta³em nie poprawny cios" + lastMessage.charAt(0) + lastMessage.charAt(2));
					System.out.println(lastMessage);
					return false;
				} else {
					game = State.turnP;
					System.out.println("A:dosta³em poprawny cios" + lastMessage.charAt(0) + lastMessage.charAt(2));
					System.out.println(lastMessage);
					return true;
				}
			}
			lastMessage = "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				consumer.finalize();
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public void setTurnName() {
		if (game == State.turnG) {
			textUp = "Tura gracza.";
		} else {
			textUp = "Tura przeciwnika.";
		}
		textDown = "";
	}

	private void finishGame() {
		if (game == State.turnP) {
			textUp = "PRZEGRA£EŒ. :(";
			textDown = "";
		} else {
			textUp = "WYGRA£EŒ! :)";
			textDown = "";
		}
		game = State.end;
	}
}