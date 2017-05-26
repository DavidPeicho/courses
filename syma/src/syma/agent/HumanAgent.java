package syma.agent;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class HumanAgent extends AAgent {

	int age_;
	
	public HumanAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		age_ = 0;
	}
	
}
