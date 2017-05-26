package syma.vehicles;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Bus extends AVehicle {

	public Bus(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		nbSeats_ = 20;
	}
	
}
