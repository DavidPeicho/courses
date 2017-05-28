package syma.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import syma.environment.Building;
import syma.environment.WorkPlace;
import syma.events.EventObject;
import syma.events.UpdateListener;
import syma.main.GridElement;
import syma.utils.Const;

public class HumanAgent extends AAgent {

	private static Logger LOGGER = Logger.getLogger(HumanAgent.class.getName());
	
	private int age_;
	private boolean gender_;
	
	private Building home_;
	private WorkPlace workplace_;
	
	private float maxAge_;
	
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
		maxAge_ = Const.MAX_AGE + (int)((Math.random() * 30) - 15);
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 10)
	public void step() {
		double deathRate = ((float)age_ / (float)maxAge_) * Math.random();
		if (deathRate >= 0.85) {
			die();
		}
	}
	
	private void die() {
		LOGGER.log(Level.INFO, "Agent " + id_ + " died at " + age_);
		
		Context<HumanAgent> context = ContextUtils.getContext(this);
		context.remove(this);
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
