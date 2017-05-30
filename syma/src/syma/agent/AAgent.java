package syma.agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.events.UpdateListener;
import syma.goal.AGoal;
import syma.main.GridElement;

public class AAgent extends GridElement implements IAgent {
	
	private static long ID = -1;
	 
	protected int speed_;
	
	protected final Queue<AGoal> goals_;
	protected final CopyOnWriteArrayList<UpdateListener> listeners_;

	protected long id_;
	
	public AAgent(Grid<GridElement> grid) {
		super(grid);
		id_ = ++ID;
		goals_ = new LinkedList<AGoal>();
		listeners_ = new CopyOnWriteArrayList<UpdateListener>();
	}
	
	/* GETTERS // SETTERS */
	public long getID() {
		return id_;
	}
	
	@Override
	public void decide() {
	
	}

	@Override
	public void step() {
		if (goals_.isEmpty())
			return;
		goals_.peek().update();
	}
	
	public void setSpeed(int s) {
		speed_ = s;
	}
	
	public int getSpeed() {
		return speed_;
	}
	
	public void addGoal(AGoal behavior) {
		goals_.add(behavior);
	}
	
	public void removeGoal(AGoal behavior) {
		goals_.remove(behavior);
	}
	
}
