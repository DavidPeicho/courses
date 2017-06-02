package syma.goal;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;
import syma.events.IUpdateListener;
import syma.main.GridElement;

public class Follow extends AGoal {

	private final AAgent dest_;
	private final Grid<GridElement> grid_;

	public Follow(AAgent target, IUpdateListener callback, AAgent dest, Grid<GridElement> grid) {
		super(target, callback);
		dest_ = dest;
		grid_ = grid;
	}

	@Override
	public void update() {
		target_.setPos(dest_.getX(), dest_.getY());
	}

	@Override
	public boolean success() {
		return false;
	}

	public AAgent getDest() {
		return dest_;
	}

	@Override
	public String toString() {
		return "Follow [dest_=" + dest_ + ", target_=" + target_
				+ ", autoRemoveWhenReached_=" + autoRemoveWhenReached_ + "]";
	}

}
