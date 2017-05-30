package syma.behaviors;

import syma.agent.AAgent;

public abstract class AGoal implements IGoal {

	protected final AAgent target_;
	
	public AGoal(AAgent target) {
		target_ = target;
	}
	
	@Override
	public abstract void update();
	
	public void imply(AGoal goal) {
		target_.addGoal(goal);
		target_.removeGoal(this);
	}

}
