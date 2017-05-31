package syma.goal;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;
import syma.events.IUpdateListener;
import syma.main.GridElement;
import syma.utils.PathSearch;

public class MoveTo extends AGoal {

	private final GridElement dest_;
	private final Grid<GridElement> grid_;

	private PathSearch path_;

	public MoveTo(AAgent target, IUpdateListener callback, GridElement dest, Grid<GridElement> grid) {
		super(target, callback);
		dest_ = dest;
		grid_ = grid;

		if (dest == null) return;

		path_ = new PathSearch(grid_);
	}

	@Override
	public void refresh() {
		if (path_.getPath().isEmpty()) {
			path_.search(target_.getPos(), dest_.getPos());
			path_.computePath();
		}
	}

	@Override
	public void update() {
		if (target_.getX() == dest_.getX() && target_.getY() == dest_.getY()) {
			triggerCallback(null);
			if (autoRemoveWhenReached_) {
				target_.pollGoal();
			}
			return;
		}
		if (dest_ == null) {
			triggerCallback(null);
			return;
		}
		
		if (path_.getPath().isEmpty()) return;
		
		GridPoint dest = path_.getPath().pop();
		target_.setPos(dest.getX(), dest.getY());
		
		if (success()) target_.pollGoal();

	}

	@Override
	public boolean success() {
		return target_.getPos().equals(dest_.getPos());
	}

	public GridPoint getDest() {
		return dest_.getPos();
	}

}
