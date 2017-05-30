package syma.goal;

import syma.agent.AAgent;

public class Wait extends AGoal {
	private int time_;

	public Wait(AAgent target, int time) {
		super(target);
		time_ = time;
	}

	@Override
	public void update() {
		time_--;
	}

}
