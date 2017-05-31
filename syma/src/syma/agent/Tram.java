package syma.agent;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.BusStop;
import syma.environment.Road;
import syma.events.AEventObject;
import syma.events.IUpdateListener;
import syma.goal.AGoal;
import syma.goal.MoveTo;
import syma.goal.TakePassengers;
import syma.goal.Wait;
import syma.main.GridElement;

public class Tram extends AAgent {
	ArrayList<AAgent> passengers;
	private ArrayList<BusStop> stops;
	private ArrayList<GridElement> roadStops;
	private ArrayList<AGoal> cycle;
	private final int waitingTime_ = 10;
	private GridPoint start;
	
	private IUpdateListener cycleEndedListener_ = (AEventObject o) -> {
		Tram.this.pollGoal();
		Tram.this.addGoal(cycle.get(0));
	};

	public Tram(Grid<GridElement> grid, ArrayList<BusStop> busStops) {
		super(grid);
		stops = busStops;
		roadStops = new ArrayList<>();
		computeRoadStops();
	}
	
	private void computeRoadStops() {
		for (BusStop e : stops) {
			int x = e.getX();
			int y = e.getY();
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i == j || i == 0 && j == 0) 
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
							break;
						}
					}
				}
			}
		}
		start = roadStops.get(0).getPos();
	}
	
	private void computeCycle() {
		Wait lastW = null;
		for (GridElement e : roadStops) {
			MoveTo mt = new MoveTo(this, null, e, grid_);
			IUpdateListener l1 = (AEventObject o) -> {
				Tram.this.pollGoal();
				Tram.this.addGoal(mt);
			};
			lastW.addCallback(l1);
			Wait w = new Wait(this, null, waitingTime_);
			IUpdateListener l2 = (AEventObject o) -> {
				Tram.this.pollGoal();
				Tram.this.addGoal(w);
			};
			mt.addCallback(l2);
			lastW = w;
			goals_.add(mt);
			goals_.add(lastW);
		}
		IUpdateListener l = (AEventObject o) -> {
			Tram.this.pollGoal();
			Tram.this.addGoal(Tram.this.goals_.peek());
		};
		lastW.addCallback(l);
		/*
		for (int i = 1; i < roadStops.size(); i++) {
			MoveTo nextGoal = new MoveTo(this, roadStops.get(i), grid_);
		}
		*/
	}

	public GridPoint getStart() {
		return start;
	}
	
}
