package com.radek.statki.model;

public class Ship {
	public int masts; // private
	public int currentMasts = 0;// private
	public int[] shots;// private
	public int[][] coordinates;

	public Ship(int masts) {
		this.masts = masts;
		shots = new int[masts];
		coordinates = new int[masts][2];
	}

	public boolean isComplete() {
		return currentMasts == masts;
	}

	public boolean isDead() {

		if (isComplete()) {
			int pom = 0;
			for (int i = 0; i < masts; i++) {
				pom += shots[i];
			}

			if (pom >= masts) {

				for (int i = 0; i < masts; i++) {
					if (shots[i] == 1) {
						shots[i] = 2;
					}
				}
				return true;
			}
		}

		return false;
	}

	public boolean addMast(int x, int y) {
		if (!isComplete()) {
			if ((validateInternally(x, y) && validateExist(x, y)) || currentMasts == 0) {
				coordinates[currentMasts][0] = x;
				coordinates[currentMasts][1] = y;
				currentMasts++;
				return true;
			}
		}
		return false;
	}

	public boolean validateInternally(int x, int y) {
		// Sprawdza, czy nowy segment da siê przykleiæ do starych.
		int[][] variants = { { -1, 0 }, { 1, 0 }, { 0, 1 }, { 0, -1 } };
		if (x >= 0 && x < 10 && y >= 0 && y < 10) {
			for (int i = 0; i < 4; i++) {
				if (!validateExist(x + variants[i][0], y + variants[i][1]))
					return true;
			}
		}
		return false;
	}

	public boolean validateExist(int x, int y) {
		// Sprawdza, czy taki segment ju¿ istnieje. Jak istnieje to false.
		if (x >= 0 && x < 10 && y >= 0 && y < 10) {
			for (int i = 0; i < currentMasts; i++) {
				if (coordinates[i][0] == x && coordinates[i][1] == y) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean validateVicinity(int x, int y) {
		// Sprawdza otoczenie wskazanego punktu na wypadek wyst¹pienia elementu tego
		// statku.s
		int[][] variants = { { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 0 }, { 1, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 } };
		System.out.println("Sprawdzam " + masts + " masztowiec.");

		if (x >= 0 && x < 10 && y >= 0 && y < 10) {

			System.out.println("CurrentMasts: " + currentMasts);
			for (int j = 0; j < currentMasts; j++) {
				for (int i = 0; i < 8; i++) {
					if (!validateExist(x + variants[i][0], y + variants[i][1]))
						return false;
				}
			}

		}

		return true;
	}

	public int shoot(int x, int y) {
		// Zwraca 1 trafiony, 0 pud³o, -1 ju¿ by³ trafiony.
		for (int i = 0; i < masts; i++) {
			if (coordinates[i][0] == x && coordinates[i][1] == y) {
				if (shots[i] == 1)
					return -1;
				shots[i] = 1;
				return 1;
			}
		}
		return 0;
	}

	public String printToString() {

		String string = "";

		for (int i = 0; i < masts; i++) {
			string += coordinates[i][0] + "" + coordinates[i][1] + " ";
		}

		return string;
	}
}
