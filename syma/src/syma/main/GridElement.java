package syma.main;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class GridElement {
	
	private static long ID = -1;

	protected Grid<GridElement> grid_;
	protected long id_;

	public GridElement(Grid<GridElement> grid) {
		grid_ = grid;
		id_ = ++ID;
	}
	
	public static void resetID() {
		ID = -1;
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
	
	public long getID() {
		return id_;
	}
}
