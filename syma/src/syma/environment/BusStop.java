package syma.environment;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class BusStop extends AFixedGeography {

	private GridElement roadStop_;
	
	public BusStop(Grid<GridElement> grid) {
		super(grid);
	}
	
	public BusStop(Grid<GridElement> grid, GridElement roadStop) {
		super(grid);
		roadStop_ = roadStop_;
	}

	public GridElement getRoadStop() {
		return roadStop_;
	}
	
	public void setRoadStop(GridElement roadStop) {
		roadStop_ = roadStop;
	}
	
}
