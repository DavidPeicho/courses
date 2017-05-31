package syma.goal;

import syma.agent.AAgent;
import syma.events.IUpdateListener;

public abstract class AGoal implements IGoal {

	protected final AAgent target_;
	protected IUpdateListener callback_;
	
	public AGoal(AAgent target) {
		target_ = target;
	}
	
	@Override
	public abstract void update();
	
	public void imply(AGoal goal) {
		target_.addGoal(goal);
		target_.removeGoal(this);
	}
	
	public void addCallback(IUpdateListener l) {
		callback_ = l;
	}

}
