package syma.agent;

import repast.simphony.space.grid.Grid;
import syma.environment.Building;
import syma.environment.WorkPlace;
import syma.main.GridElement;

public class HumanAgent extends AAgent {

	int age_;
	boolean gender_;
	
	Building home_;
	WorkPlace workplace_;
	
	public HumanAgent(int x, int y, Grid<GridElement> grid, int age, boolean gender) {
		super(x, y, grid);
		age_ = age;
		gender_ = gender;
	}
	
	public HumanAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		age_ = 0;
	}
	
	/* GETTERS // SETTERS */
	
	public void setHome(Building building) {
		home_ = building;
	}
	
	public Building getHome() {
		return home_;
	}
	
	public void setWorkPlace(WorkPlace workplace) {
		workplace_ = workplace;
	}
	
	public WorkPlace getWorkPlace() {
		return workplace_;
	}
}
