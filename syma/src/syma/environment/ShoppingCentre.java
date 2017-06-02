package syma.environment;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import syma.main.GridElement;

public class ShoppingCentre extends AFixedGeography {

	/**
	 * Contains every shoppign places that are on the grid.
	 * It allows to access every building quickly without
	 * making a search on the whole grid.
	 */
	public static ArrayList<ShoppingCentre> globalList = new ArrayList<ShoppingCentre>();
	
	public ShoppingCentre(Grid<GridElement> grid) {
		super(grid);
	}

	@Override
	public String toString() {
		return "ShoppingCentre [id_=" + id_ + "]";
	}

}
