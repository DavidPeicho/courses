package syma.vehicles;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Car extends AVehicle {
	
	public Car(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		nbSeats_ = 4;
	}

}
