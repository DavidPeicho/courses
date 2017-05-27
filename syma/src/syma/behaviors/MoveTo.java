package syma.behaviors;

import repast.simphony.space.grid.GridPoint;
import syma.agent.AAgent;

public class MoveTo extends ABehavior {

	private final GridPoint dest_;
	
	public MoveTo(AAgent target, GridPoint dest) {
		super(target);
		dest_ = dest;
	}
	
	@Override
	public void update() {
		
	}

}
