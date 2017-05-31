package syma.agent;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Tram extends AAgent {
	ArrayList<AAgent> passengers;

	public Tram(Grid<GridElement> grid) {
		super(grid);
		// TODO Auto-generated constructor stub
	}
	
	private void takeIn(int posx, int posy) {
		
		for (AAgent agent : passengers) {
			
		}
	}

}
