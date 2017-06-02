package syma.vehicles;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Car extends AVehicle {
	
	public Car(Grid<GridElement> grid) {
		super(grid);
		nbSeats_ = 4;
	}

	@Override
	public String toString() {
		return "Car [nbSeats_=" + nbSeats_ + ", id_=" + id_ + "]";
	}

}
