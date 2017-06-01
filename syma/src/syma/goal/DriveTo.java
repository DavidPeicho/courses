package syma.goal;

import repast.simphony.space.grid.Grid;
import syma.agent.AAgent;
import syma.agent.Tram;
import syma.events.IUpdateListener;
import syma.main.GridElement;

public class DriveTo extends AGoal {
	
	private final GridElement dest_;
	private final Grid<GridElement> grid_;
	private final Tram tram_;

	public DriveTo(AAgent target, IUpdateListener callback, GridElement dest, Tram tram, Grid<GridElement> grid) {
		super(target, callback);
		grid_ = grid;
		dest_ = dest;
		tram_ = tram;
	}

	@Override
	public void update() {
		target_.setPos(tram_.getX(), tram_.getY());
	}
	
	@Override
	public boolean success() {
		return dest_.getPos().equals(tram_.getPos());
	}
}
