package syma.agent;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.behaviors.ABehavior;
import syma.events.UpdateListener;
import syma.main.GridElement;

public class AAgent extends GridElement implements IAgent {
	
	protected int speed_;
	
	protected final ArrayList<ABehavior> behaviors_;
	protected final CopyOnWriteArrayList<UpdateListener> listeners_;

	public AAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		behaviors_ = new ArrayList<ABehavior>();
		listeners_ = new CopyOnWriteArrayList<UpdateListener>();
	}

	@Override
	public void step() { }
	
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
	
	public void addListener(UpdateListener listener) {
		listeners_.add(listener);
	}
	
	public void removeListener(UpdateListener listener) {
	    listeners_.remove(listener);
	}
	
}
