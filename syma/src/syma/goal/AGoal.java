package syma.goal;

import syma.agent.AAgent;
import syma.events.AEventObject;
import syma.events.IUpdateListener;

public abstract class AGoal implements IGoal {

	protected final AAgent target_;
	protected IUpdateListener callback_;
	
	protected boolean autoRemoveWhenReached_;
	
	public AGoal(AAgent target, IUpdateListener callback) {
		target_ = target;
		callback_ = callback;
		autoRemoveWhenReached_ = false;
	}
	
	@Override
	public abstract void update();
	
	@Override
	public boolean success() {
		return false;
	}

	@Override
	public void refresh() {
	}
	
	public void addCallback(IUpdateListener l) {
		callback_ = l;
	}

	public void triggerCallback(AEventObject o) {
		if (callback_ != null) {
			callback_.updateEvent(o);
		}
	}
	
	public void setAutoremoveWhenReached(boolean v) {
		autoRemoveWhenReached_ = v;
	}
	
	public IUpdateListener getCallback() {
		return callback_;
	}

}
