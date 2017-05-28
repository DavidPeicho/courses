package syma.agent;

import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import syma.environment.Building;
import syma.environment.WorkPlace;
import syma.events.UpdateListener;
import syma.main.GridElement;

public class GodAgent extends AAgent {

	private static GodAgent instance_ = null;
	
	protected final CopyOnWriteArrayList<UpdateListener> yearListeners_;
	
	private long year_;
	private long day_;
	private int hour_;
	private int min_;
	
	public GodAgent(Grid<GridElement> grid) {
		super(0, 0, grid);
		
		yearListeners_ = new CopyOnWriteArrayList<UpdateListener>();
		day_ = 0;
		hour_ = 0;
		min_ = 0;
		year_ = 0;
	}
	
	public static void init(Grid<GridElement> grid) {
		if (instance_ == null) {
			instance_ = new GodAgent(grid);
		}
	}
	
	public static GodAgent instance() {
		return instance_;
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		++min_;
		if (min_ == 60) {
			min_ = 0;
			++hour_;
		}
		if (hour_ == 1) {
			++day_;
			hour_ = 0;
		}
		if (day_ == 365) {
			day_ = 0;
			++year_;
			// Calls every listener for new year event
			yearListeners_.forEach((UpdateListener e) -> {
				e.updateEvent(null);
			});
		}
		//System.out.println("Min = " + min_ + " " + hour_ + " " + day_);
	}
	
	public HumanAgent createAgent(int x, int y, Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		
		HumanAgent agent = new HumanAgent(x, y, grid, age, gender);
		agent.setHome(home);
		agent.setWorkPlace(workplace);
	
		yearListeners_.add(agent.getYearListener());
		
		return agent;
	}
	
	/* GETTERS // SETTERS */
	public void setGrid() {
		
	}
	
}
