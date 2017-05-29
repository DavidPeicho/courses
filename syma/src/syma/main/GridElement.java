package syma.main;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class GridElement {

	protected int x_;
	protected int y_;
	
	protected Grid<GridElement> grid_;

	public GridElement(int x, int y, Grid<GridElement> grid) {
		x_ = x;
		y_ = y;
		grid_ = grid;
	}
	
	public GridPoint getPos() {
		return new GridPoint(x_, y_);
	}
	
	public int getX() {
		return x_;
	}
	
	public int getY() {
		return y_;
	}
	
	public void setX(int val) {
		x_ = val;
	}
	
	public void setY(int val) {
		y_ = val;
	}
	
}
