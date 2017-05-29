package syma.agent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.behaviors.ABehavior;
import syma.events.UpdateListener;
import syma.main.GridElement;

public abstract class AAgent extends GridElement implements IAgent {
	
	private static long ID = -1;
	 
	protected int speed_;
	
	protected final Queue<ABehavior> behaviors_;
	protected final CopyOnWriteArrayList<UpdateListener> listeners_;

	protected long id_; 
	
	public AAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		id_ = ++ID;
		behaviors_ = new LinkedList<ABehavior>();
		listeners_ = new CopyOnWriteArrayList<UpdateListener>();
	}
	
	/* GETTERS // SETTERS */
	public long getID() {
		return id_;
	}

	@Override
	public void step() {
		if (behaviors_.isEmpty())
			return;
		behaviors_.poll().update();
	}
	
	public void setSpeed(int s) {
		speed_ = s;
	}
	
	public int getSpeed() {
		return speed_;
	}
	
	public void addListener(ABehavior behavior) {
		behaviors_.add(behavior);
	}
	
	public void removeBehavior(ABehavior behavior) {
		behaviors_.remove(behavior);
	}
	
}
