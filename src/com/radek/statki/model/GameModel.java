package com.radek.statki.model;

/**
 * Klasa implementujaca model we wzorcu MVC. Zarzadza danymi i odpowiada za
 * wykonywanie roznych zadan w grze statki.
 * 
 * @author Radoslaw Tuzimek
 * @version 2.0
 */

public class GameModel {

	public enum State {
		start, entry4, entry3, entry2, entry1, handshake, turnG, turnP, end
	};

	public String modelId;
	public String textUp;
	public String textDown;
	private int sunkenOpponentShips;
	private int sunkenGamerShips;
	public State game;
	public Board gamerBoard = new Board();
	public Board opponentBoard = new Board();

	public double playerRoll;

	public String test;

	/**
	 * Konstruktor bezparametrowy. Ustawia wszystkie zmienne i zmienne pomocnicze na
	 * domyslne poczatkowe wartosci.
	 */

	public GameModel() {
		game = State.start;
		textUp = "Witaj w grze statki, aby rozpocz¹æ naciœnij zielony przycisk.";
		textDown = "";
		sunkenOpponentShips = 0;
		sunkenGamerShips = 0;
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

	public String getTextUp() {
		return textUp;
	}

	public String getTextDown() {
		return textDown;
	}

	public void click() {

		if (game == State.start) {

			textUp = "Na planszy gracza umieœæ statek o d³ugoœci 4";

			gamerBoard.addShip(new Ship(4));
			gamerBoard.addShip(new Ship(3));
			gamerBoard.addShip(new Ship(2));
			gamerBoard.addShip(new Ship(1));
			game = State.entry4;

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
					textUp = "Na planszy gracza umieœæ statek o d³ugoœci 1";
				}
			}

		} else if (game == State.entry1) {

			if (checkVicinity(3, x, y)) {
				result = gamerBoard.getShip(3).addMast(x, y);
				if (gamerBoard.getShip(3).isComplete()) {
					textUp = "Zaczekaj a¿ przeciwnik zakoñczy ustawianie staków...";
					game = State.handshake;
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
					sunkenOpponentShips++;
					textDown = "ZATOPIONY!";
				}
				if (sunkenOpponentShips == 4) {
					finishGame();
				}
				return true;
			}
		}

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
					sunkenGamerShips++;
					textDown = "ZATOPIONY!";
				}
				if (sunkenGamerShips == 4) {
					finishGame();
				}
				return true;
			}
		}

		textUp = "Tura gracza.";
		textDown = "PUD£O!";
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

}