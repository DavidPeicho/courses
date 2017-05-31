package syma.goal;

import syma.agent.AAgent;
import syma.events.IUpdateListener;

public class Wait extends AGoal {
	private int time_;

	public Wait(AAgent target, IUpdateListener callback, int time) {
		super(target, callback);
		time_ = time;
	}

	@Override
	public void update() {
		time_--;
	}

}
