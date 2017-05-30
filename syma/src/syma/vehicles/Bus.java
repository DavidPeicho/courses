package syma.vehicles;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Bus extends AVehicle {

	public Bus(Grid<GridElement> grid) {
		super(grid);
		nbSeats_ = 20;
	}
	
}
