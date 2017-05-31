package syma.agent;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.BusStop;
import syma.environment.Road;
import syma.events.AEventObject;
import syma.events.IUpdateListener;
import syma.events.SuccessEvent;
import syma.goal.AGoal;
import syma.goal.MoveTo;
import syma.goal.Wait;
import syma.main.GridElement;
import syma.utils.Const;

public class Tram extends AAgent {
	ArrayList<AAgent> passengers;
	private ArrayList<BusStop> stops;
	private ArrayList<GridElement> roadStops;
	private GridPoint start;

	public Tram(Grid<GridElement> grid, ArrayList<BusStop> busStops) {
		super(grid);
		stops = busStops;
		roadStops = new ArrayList<>();
		computeRoadStops();
		computeCycle();
	}
	
	private void computeRoadStops() {
		for (BusStop e : stops) {
			int x = e.getX();
			int y = e.getY();
			Boolean found = false;
			for (int i = -1; i <= 1 && !found; i++) {
				for (int j = -1; j <= 1 && !found; j++) {
					if (i == j || j * i != 0) 
						continue;
					int w = x + i;
					int h = y + j;
					Iterable<GridElement> neighbor = grid_.getObjectsAt(w, h);
					Iterator<GridElement> it = neighbor.iterator();
					if (!neighbor.iterator().hasNext())
						continue;
					while (it.hasNext()) {
						Object o = it.next();
						if (o instanceof Road) {
							roadStops.add(((Road)o));
							found = true;
							break;
						}
					}
				}
			}
		}
		start = roadStops.get(0).getPos();
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step() {
		super.step();
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
		AGoal g = peekGoal();
		if (g != null && g.success()) {
			g.triggerCallback(new SuccessEvent());
		}
	}
	
	private void computeCycle() {
		Wait lastW = null;
		MoveTo firstMt = null;
		for (GridElement e : roadStops) {
			MoveTo mt = new MoveTo(this, null, e, grid_);
			if (firstMt == null)
				firstMt = mt;
			IUpdateListener l1 = (AEventObject o) -> {
				((Wait)Tram.this.peekGoal()).reset();
				Tram.this.pollGoal();
				Tram.this.addGoal(mt);
			};
			if (lastW != null)
				lastW.addCallback(l1);
			Wait w = new Wait(this, null, Const.timeToTick(0, 0, Const.BUS_WAITING_TIME));
			IUpdateListener l2 = (AEventObject o) -> {
				Tram.this.pollGoal();
				Tram.this.addGoal(w);
			};
			mt.addCallback(l2);
			lastW = w;
		}
		final MoveTo constFirstMt = firstMt;
		IUpdateListener l = (AEventObject o) -> {
			((Wait)Tram.this.peekGoal()).reset();
			Tram.this.pollGoal();
			Tram.this.addGoal(constFirstMt);
		};
		lastW.addCallback(l);
		goals_.add(lastW);
	}

	public GridPoint getStart() {
		return start;
	}
	
}
