package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Building extends AFixedGeography {
	
	/**
	 * Contains every buildings that are on the grid.
	 * It allows to access every building quickly without
	 * making a search on the whole grid.
	 */
	public static ArrayList<Building> globalList = new ArrayList<Building>();

	public Building(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
	}
	
}
