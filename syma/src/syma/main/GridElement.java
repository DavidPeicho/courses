package syma.main;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class GridElement {

	protected Grid<GridElement> grid_;

	public GridElement(Grid<GridElement> grid) {
		grid_ = grid;
	}

	public GridPoint getPos() {
		return grid_.getLocation(this);
	}

	public int getX() {
		return getPos().getX();
	}

	public int getY() {
		return getPos().getY();
	}

	public void setPos(int x, int y) {
		grid_.moveTo(this, x, y);
	}
}
