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
	
	private int startHour_;
	private int endHour_;
	
	public WorkPlace(Grid<GridElement> grid) {
		super(grid);
		
		startHour_ = 9;
		endHour_ = 18;
	}
	
	/* GETTERS // SETTERS */
	
	public int getStartHour() {
		return startHour_;
	}
	
	public int getEndHour() {
		return endHour_;
	}

}
