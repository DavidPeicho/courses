package syma.agent;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public abstract class IAgent{

	protected int x;
	protected int y;
	
	protected Grid<IAgent> grid;

	public IAgent(int x, int y, Grid<IAgent> grid){
		this.x = x;
		this.y = y;
		this.grid = grid;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public abstract void step();
	
}
