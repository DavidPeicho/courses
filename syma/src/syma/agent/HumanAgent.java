package syma.agent;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import syma.environment.Building;
import syma.environment.WorkPlace;
import syma.events.EventObject;
import syma.events.UpdateListener;
import syma.main.GridElement;

public class HumanAgent extends AAgent {

	private int age_;
	private boolean gender_;
	
	private Building home_;
	private WorkPlace workplace_;
	
	private UpdateListener yearListener_ = new UpdateListener() {

		@Override
		public void updateEvent(EventObject e) {
			++age_;	
		}
		
	};
	
	public HumanAgent(int x, int y, Grid<GridElement> grid, int age, boolean gender) {
		super(x, y, grid);
		age_ = age;
		gender_ = gender;
	}
	
	public HumanAgent(int x, int y, Grid<GridElement> grid) {
		super(x, y, grid);
		age_ = 0;
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 10)
	public void step() {
	}
	
	/* GETTERS // SETTERS */
	
	public int getAge() {
		return age_;
	}
	
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
	
	public UpdateListener getYearListener() {
		return yearListener_;
	}
	
}
