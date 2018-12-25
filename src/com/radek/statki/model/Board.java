package com.radek.statki.model;
/**
 * Klasa przechowujaca pozycje statkow na planszy.
 * 
 * @author Radoslaw Tuzimek
 * @version 2.0
 */

import java.util.ArrayList;

public class Board {
	ArrayList<Ship> ships = new ArrayList<Ship>();

	public void addShip(Ship ship) {
		ships.add(ship);
	}

	public Ship getShip(int index) {
		return ships.get(index);
	}

	public ArrayList<Ship> getShips() {
		return ships;
	}
}
