package syma.behaviors;

import syma.agent.AAgent;

public abstract class ABehavior implements IBehavior {

	protected final AAgent target_;
	
	public ABehavior(AAgent target) {
		target_ = target;
	}
	
	@Override
	public abstract void update();

}
