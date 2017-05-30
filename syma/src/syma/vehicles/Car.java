package syma.vehicles;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Car extends AVehicle {
	
	public Car(Grid<GridElement> grid) {
		super(grid);
		nbSeats_ = 4;
	}

}
