package syma.environment;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public abstract class AFixedGeography extends GridElement {

	public AFixedGeography(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
	}

	
}
