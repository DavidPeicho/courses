package syma.agent;

import java.util.ArrayList;
import java.util.Iterator;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.BusStop;
import syma.environment.Road;
import syma.goal.MoveTo;
import syma.main.GridElement;

public class Tram extends AAgent {
	ArrayList<AAgent> passengers;
	ArrayList<BusStop> stops;
	ArrayList<GridElement> roadStops;

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
	}
	
	public void computeCycle() {
		if (roadStops.isEmpty())
			return;
		/*
		MoveTo goal = new MoveTo(this, roadStops.get(0), grid_);
		for (int i = 1; i < roadStops.size(); i++) {
			MoveTo nextGoal = new MoveTo(this, roadStops.get(i), grid_);
		}
		*/
	}
	
}
