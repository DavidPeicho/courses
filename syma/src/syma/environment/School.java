package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class School extends AFixedGeography {

	/**
	 * Contains every school that are on the grid.
	 * It allows to access every building quickly without
	 * making a search on the whole grid.
	 */
	public static ArrayList<School> globalList = new ArrayList<School>();
	
	public School(Grid<GridElement> grid) {
		super(grid);
	}

	@Override
	public String toString() {
		return "School [id_=" + id_ + "]";
	}

}
