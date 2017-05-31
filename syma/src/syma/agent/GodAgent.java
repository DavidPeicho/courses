package syma.agent;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import syma.environment.Building;
import syma.environment.WorkPlace;
import syma.events.AEventObject;
import syma.events.EventTimeObject;
import syma.events.IUpdateListener;
import syma.main.GridElement;
import syma.utils.Const;

public class GodAgent extends AAgent {

	private static GodAgent instance_ = null;
	
	protected final CopyOnWriteArrayList<IUpdateListener> timeListeners_;
	
	private long year_;
	private long day_;
	private int hour_;
	private int min_;
	
	public GodAgent(Grid<GridElement> grid) {
		super(grid);
		timeListeners_ = new CopyOnWriteArrayList<IUpdateListener>();
	}
	
	public static void init(Grid<GridElement> grid) {
		if (instance_ == null) {
			instance_ = new GodAgent(grid);
		}

		instance_.timeListeners_.clear();
		instance_.day_ = 0;
		instance_.hour_ = 6;
		instance_.min_ = 58;
		instance_.year_ = 0;
	}
	
	public static GodAgent instance() {
		return instance_;
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		++min_;
		if (min_ >= 60) {
			min_ = 0;
			++hour_;
			if (hour_ == Const.MORNING_HOUR) {
				callEvt(new EventTimeObject(EventTimeObject.Type.MORNING_HOUR));
			}
		}
		if (hour_ >= 24) {
			++day_;
			hour_ = 0;
		}
		if (Const.dayToMin(day_) >= (Const.YEAR_IN_MIN / Const.MINUTE_TIME_FACTOR)) {
			day_ = 0;
			++year_;
			callEvt(new EventTimeObject(EventTimeObject.Type.YEAR));
		}
		
	}
	
	public HumanAgent createAgent(Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		
		HumanAgent agent = new HumanAgent(grid, age, gender, home, workplace);
		timeListeners_.add(agent.getYearListener());
		
		return agent;
	}
	
	public HumanAgent createChildAgent(Grid<GridElement> grid, Building home) {
		
		HumanAgent a = new HumanAgent(grid, 1, Math.random() >= 0.5f, home, null);
		timeListeners_.add(a.getYearListener());
		
		return a;
	}
	
	/* GETTERS // SETTERS */
	public int getHour() {
		return hour_;
	}
	
	public int getMin() {
		return min_;
	}
	
	private void callEvt(AEventObject o) {
		timeListeners_.forEach((IUpdateListener e) -> {
			e.updateEvent(o);
		});	
	}
	
}
