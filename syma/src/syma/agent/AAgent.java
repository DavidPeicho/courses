package syma.agent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.events.IUpdateListener;
import syma.goal.AGoal;
import syma.goal.Follow;
import syma.main.GridElement;
import syma.utils.Const;

public class AAgent extends GridElement implements IAgent {
	
	protected int speed_;
	
	protected final Queue<AGoal> goals_;
	protected final CopyOnWriteArrayList<IUpdateListener> listeners_;
	
	public AAgent(Grid<GridElement> grid) {
		super(grid);
		goals_ = new LinkedList<AGoal>();
		listeners_ = new CopyOnWriteArrayList<IUpdateListener>();
	}
	
	/* GETTERS // SETTERS */
	
	@Override
	public void decide() { }

	@Override
	public void step() {
		if (goals_.isEmpty())
			return;
		AGoal g = goals_.peek();
		g.refresh();
		g.update();
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
	
	public AGoal peekGoal() {
		return goals_.peek();
	}

	public AGoal pollGoal() {
		return goals_.poll();
	}

}
