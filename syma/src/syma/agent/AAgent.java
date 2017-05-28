package syma.agent;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.behaviors.ABehavior;
import syma.events.UpdateListener;
import syma.main.GridElement;

public abstract class AAgent extends GridElement implements IAgent {
	
	private static long ID = -1;
	 
	protected int speed_;
	protected final ArrayList<ABehavior> behaviors_;

	protected long id_; 
	
	public AAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		behaviors_ = new ArrayList<ABehavior>();
		
		id_ = ++ID;
	}

	public abstract void step();
	
	/* GETTERS // SETTERS */
	public long getID() {
		return id_;
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
