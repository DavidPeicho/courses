package syma.goal;

import syma.agent.AAgent;
import syma.events.IUpdateListener;

public class Wait extends AGoal {
	private final int maxTickNb_;
	private int tickNb_;

	public Wait(AAgent target, IUpdateListener callback, int tickNb) {
		super(target, callback);
		tickNb_ = tickNb;
		maxTickNb_ = tickNb;
	}
	
	public void reset() {
		tickNb_ = maxTickNb_;
	}

	@Override
	public void update() {
		tickNb_--;
	}
	
	@Override
	public boolean success() {
		return tickNb_ <= 0;
	}

}
