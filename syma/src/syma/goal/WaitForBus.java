package syma.goal;

import java.util.Iterator;

import repast.simphony.space.grid.Grid;
import syma.agent.AAgent;
import syma.agent.Tram;
import syma.environment.Road;
import syma.events.IUpdateListener;
import syma.main.GridElement;

public class WaitForBus extends AGoal {
	private final GridElement busStopPosition_;
	private final Grid<GridElement> grid_;

	public WaitForBus(AAgent target, IUpdateListener callback, GridElement busStopPosition, Grid<GridElement> grid) {
		super(target, callback);
		busStopPosition_ = busStopPosition;
		grid_ = grid;
	}

	@Override
	public boolean success() {
		Iterable<GridElement> neighbor = grid_.getObjectsAt(busStopPosition_.getX(), busStopPosition_.getY());
		Iterator<GridElement> it = neighbor.iterator();
		if (!neighbor.iterator().hasNext())
			return false;
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof Tram) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update() {
	}

	@Override
	public String toString() {
		return "WaitForBus [busStopPosition_=" + busStopPosition_ + ", target_=" + target_ + ", autoRemoveWhenReached_="
				+ autoRemoveWhenReached_ + "]";
	}
}
