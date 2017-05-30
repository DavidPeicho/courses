package syma.goal;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;
import syma.main.GridElement;

public class Follow extends AGoal {

	private final AAgent dest_;
	private final Grid<GridElement> grid_;
	
	public Follow(AAgent target, AAgent dest, Grid<GridElement> grid) {
		super(target);
		dest_ = dest;
		grid_ = grid;
	}
	
	@Override
	public void update() {
		target_.setPos(dest_.getX(), dest_.getY());
	}
	
	public AAgent getDest() {
		return dest_;
	}

}
