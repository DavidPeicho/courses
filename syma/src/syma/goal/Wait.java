package syma.goal;

import syma.agent.AAgent;
import syma.events.IUpdateListener;

public class Wait extends AGoal {
	private int tickNb_;

	public Wait(AAgent target, IUpdateListener callback, int tickNb) {
		super(target, callback);
		tickNb_ = tickNb;
	}

	@Override
	public void update() {
		tickNb_--;
		if (tickNb_ == 0) {
			triggerCallback(null);
		}
	}

}
