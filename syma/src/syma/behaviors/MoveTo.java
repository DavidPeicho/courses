package syma.behaviors;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;
import syma.main.GridElement;

public class MoveTo extends ABehavior {

	private final GridPoint dest_;
	private final Grid<GridElement> grid_;
	
	public MoveTo(AAgent target, GridPoint dest, Grid<GridElement> grid) {
		super(target);
		dest_ = dest;
		grid_ = grid;
	}
	
	@Override
	public void update() {
		grid_.moveTo(target_, dest_.getX(), dest_.getY());
	}
	
	public GridPoint getDest() {
		return dest_;
	}

}
