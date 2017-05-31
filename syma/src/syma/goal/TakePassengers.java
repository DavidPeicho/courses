package syma.goal;

import syma.agent.AAgent;
import syma.events.IUpdateListener;

public class TakePassengers extends AGoal {

	public TakePassengers(AAgent target, IUpdateListener callback) {
		super(target, callback);
	}

	@Override
	public void update() {
	}

}
