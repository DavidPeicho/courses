package syma.vehicles;

import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.agent.AAgent;
import syma.main.GridElement;

public class AVehicle extends GridElement {

	protected final CopyOnWriteArrayList<AAgent> agents_;
	protected int nbSeats_;
	
	public AVehicle(Grid<GridElement> grid) {
		super(grid);
		
		agents_ = new CopyOnWriteArrayList<AAgent>();
	}

}
