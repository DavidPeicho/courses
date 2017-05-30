package syma.environment;

import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.agent.AAgent;
import syma.main.GridElement;

public abstract class AFixedGeography extends GridElement {

	protected final CopyOnWriteArrayList<AAgent> agents_;
	
	public AFixedGeography(Grid<GridElement> grid) {
		super(grid);
		agents_ = new CopyOnWriteArrayList<AAgent>();
	}
	
	/* GETTERS // SETTERS */
	
	public void addAgent(AAgent a) {
		agents_.add(a);
	}
	
	public void removeAgent(AAgent a) {
		agents_.remove(a);
	}
	
	public boolean isEmpty() {
		return agents_.isEmpty();
	}
	
}
