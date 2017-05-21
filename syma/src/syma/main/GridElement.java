package syma.main;

import repast.simphony.space.grid.Grid;

public class GridElement {

	protected int x_;
	protected int y_;
	
	protected Grid<GridElement> grid_;

	public GridElement(int x, int y, Grid<GridElement> grid) {
		x_ = x;
		y_ = y;
		grid_ = grid;
	}
	
}
