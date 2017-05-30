package syma.agent;

import repast.simphony.engine.schedule.ScheduledMethod;

public interface IAgent{
	
	public abstract void decide();
	
	public abstract void step();
	
}
