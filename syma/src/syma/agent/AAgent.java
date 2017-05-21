package syma.agent;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class AAgent extends GridElement implements IAgent {
	
	protected Grid<GridElement> grid;

	public AAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
	}

	@Override
	public void step() { }
	
}
