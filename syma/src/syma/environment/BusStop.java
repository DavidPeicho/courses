package syma.environment;

import repast.simphony.space.grid.Grid;
import syma.agent.Tram;
import syma.main.GridElement;

public class BusStop extends AFixedGeography {

	private GridElement roadStop_;
	private static int nbBusStops = 0;
	private int number_;
	
	public BusStop(Grid<GridElement> grid) {
		super(grid);
		number_ = nbBusStops;
		nbBusStops++;
	}
	
	public BusStop(Grid<GridElement> grid, GridElement roadStop) {
		super(grid);
		roadStop_ = roadStop;
		number_ = nbBusStops;
		nbBusStops++;
	}

	public GridElement getRoadStop() {
		return roadStop_;
	}
	
	public void setRoadStop(GridElement roadStop) {
		roadStop_ = roadStop;
	}

	public int getNumber_() {
		return number_;
	}
	
}
