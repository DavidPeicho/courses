package syma.vehicles;

import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.space.grid.Grid;
import syma.agent.AAgent;
import syma.main.GridElement;

public class AVehicle extends GridElement {

	protected final CopyOnWriteArrayList<AAgent> agents_;
	protected int nbSeats_;
	
	public AVehicle(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		
		agents_ = new CopyOnWriteArrayList<AAgent>();
	}

}
