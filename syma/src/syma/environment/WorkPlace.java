package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class WorkPlace extends AFixedGeography {

	/**
	 * Contains every work places that are on the grid.
	 * It allows to access every building quickly without
	 * making a search on the whole grid.
	 */
	public static ArrayList<WorkPlace> globalList = new ArrayList<WorkPlace>();
	
	public WorkPlace(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
	}

}
