package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class Bar extends AFixedGeography {

	public static ArrayList<Bar> globalList = new ArrayList<Bar>();
	
	public Bar(Grid<GridElement> grid) {
		super(grid);
	}

	@Override
	public String toString() {
		return "Bar [id_=" + id_ + "]";
	}

}
