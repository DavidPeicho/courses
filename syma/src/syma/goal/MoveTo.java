package syma.goal;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;
import syma.main.GridElement;
import syma.utils.PathSearch;

public class MoveTo extends AGoal {

	private final GridElement dest_;
	private final Grid<GridElement> grid_;
	
	private PathSearch path_;
	
	public MoveTo(AAgent target, GridElement dest, Grid<GridElement> grid) {
		super(target);
		dest_ = dest;
		grid_ = grid;
		
		if (dest == null) return;
		
		path_ = new PathSearch(grid_);
		path_.search(target.getPos(), dest.getPos());
		path_.computePath();
	}
	
	@Override
	public void update() {
		
		if (dest_ == null) {
			callCallback();
			return;
		}
		
		if (path_.getPath().isEmpty()) return;
		
		GridPoint dest = path_.getPath().pop();
		target_.setPos(dest.getX(), dest.getY());
	}
	
	public GridPoint getDest() {
		return dest_.getPos();
	}
	
	private void callCallback() {
		if (callback_ == null) return;
		callback_.updateEvent(null);
	}

}
