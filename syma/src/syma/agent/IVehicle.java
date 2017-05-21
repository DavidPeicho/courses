package syma.agent;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public abstract class IVehicle extends AAgent {

	public IVehicle(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
	}

}
