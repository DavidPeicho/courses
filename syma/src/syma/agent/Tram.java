package syma.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
	private static Logger LOGGER = Logger.getLogger(HumanAgent.class.getName());
	
	public static ArrayList<BusStop> stops;
	public static ArrayList<GridElement> roadStops = new ArrayList<>();
	public static Tram instance = null; // TODO: either singleton or wrap grid and get rid of all static containers
	private GridPoint start;
	public static int currentStop = 0;
	
	public static boolean isValid = true;
	
	public static BusStop getNearestStop(GridPoint p) {
		int minDist = Integer.MAX_VALUE;
		BusStop minBusStop = null;
		for (BusStop e : stops) {
			int candidateDist = (e.getX() - p.getX()) * (e.getX() - p.getX())
					+ (e.getY() - p.getY()) * (e.getY() - p.getY());
			if (candidateDist < minDist) {
				minBusStop = e;
				minDist = candidateDist;
			}
		}
		return minBusStop;
	}
	

	public Tram(Grid<GridElement> grid, ArrayList<BusStop> busStops) {
		super(grid);
		stops = busStops;
		computeRoadStops();
		computeCycle();
		instance = this;
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
							roadStops.add((Road)o);
							e.setRoadStop((Road)o);
							found = true;
							break;
						}
					}
				}
			}
		}
		
		if (roadStops.isEmpty()) {
			Tram.isValid = false;
			return;
		}
		
		if (roadStops.size() != stops.size()) {
			String str = Const.ENV_TAG + "\n";
			str += "The provided bus path contains stop that are not connected to the road.";
			LOGGER.log(Level.SEVERE, str);
			Tram.isValid = false;
		} else {
			start = roadStops.get(0).getPos();
		}
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step() {
		super.step();
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
		if (Const.IS_SIMULATION_OVER) return;
		
		AGoal g = peekGoal();
		if (g != null && g.success()) {
			g.triggerCallback(new SuccessEvent());
		}
	}
	
	private void computeCycle() {
		
		if (!Tram.isValid) return;
		
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
				Tram.currentStop = (Tram.currentStop + 1) % Tram.stops.size();
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


	@Override
	public String toString() {
		return "Tram [start=" + start + ", id_=" + id_ + "]";
	}

}
