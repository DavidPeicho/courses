package syma.agent;

import repast.simphony.engine.schedule.ScheduledMethod;

public interface IAgent{

	@ScheduledMethod(start = 1, interval = 1)
	public abstract void step();
	
}
