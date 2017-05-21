package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.GridPoint;
import syma.agent.IAgent;

public abstract class IFixedGeography {

	protected ArrayList<GridPoint> coords_;
	protected ArrayList<IAgent> agents_;
	
	public ArrayList<GridPoint> getCoords() {
		return coords_;
	}
	
}
