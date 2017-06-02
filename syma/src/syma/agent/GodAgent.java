package syma.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.environment.School;
import syma.environment.WorkPlace;
import syma.events.AEventObject;
import syma.events.EventTimeObject;
import syma.events.IUpdateListener;
import syma.main.GridElement;
import syma.parsing.GridParser;
import syma.utils.Const;
import syma.utils.PathSearch;

public class GodAgent extends AAgent {

	public enum Day {
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY,
		SUNDAY
	};
	
	public static final HashMap<Integer, Day> TO_DAY = new HashMap<Integer, Day>();
	public static final HashMap<Day, String> TO_DAY_STR = new HashMap<Day, String>();
	
	{
		TO_DAY.put(0, Day.MONDAY);
		TO_DAY.put(1, Day.TUESDAY);
		TO_DAY.put(2, Day.WEDNESDAY);
		TO_DAY.put(3, Day.THURSDAY);
		TO_DAY.put(4, Day.FRIDAY);
		TO_DAY.put(5, Day.SATURDAY);
		TO_DAY.put(6, Day.SUNDAY);
		
		TO_DAY_STR.put(Day.MONDAY, "Monday");
		TO_DAY_STR.put(Day.TUESDAY, "Tuesday");
		TO_DAY_STR.put(Day.WEDNESDAY, "Wednesday");
		TO_DAY_STR.put(Day.THURSDAY, "Thursday");
		TO_DAY_STR.put(Day.FRIDAY, "Friday");
		TO_DAY_STR.put(Day.SATURDAY, "Saturday");
		TO_DAY_STR.put(Day.SUNDAY, "Sunday");
	}
	
	private static Logger LOGGER = Logger.getLogger(HumanAgent.class.getName());
	
	private static GodAgent instance_ = null;
	
	protected final CopyOnWriteArrayList<IUpdateListener> timeListeners_;
	
	private Random rand_;
	
	private int nbAgents_;
	private int nbChildren_;
	
	private int totalNbAgents_;
	private int totalNbChildren_;
	
	private long year_;
	private long day_;
	private int hour_;
	private int min_;
	
	private int weekDay_;
	
	private int nextDayToBurnHouse_;
	private int nextHourToBurnHouse_;
	
	private int totalNbHouseBurnt_;
	
	public GodAgent(Grid<GridElement> grid) {
		super(grid);
		timeListeners_ = new CopyOnWriteArrayList<IUpdateListener>();
		weekDay_ = 0;
		
		rand_ = new Random();
		
		nextDayToBurnHouse_ = Const.randBetween(0, 10000, rand_) % (Const.MAX_HOUSE_BURN_WEEK + 1);
		nextHourToBurnHouse_ = Const.randBetween(1, 10000, rand_) % 24;
		
		totalNbHouseBurnt_ = 0;
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
		instance_.totalNbAgents_ = 0;
		instance_.totalNbChildren_ = 0;
		instance_.totalNbHouseBurnt_ = 0;
		
		instance_.nextDayToBurnHouse_ = Const.randBetween(0, 10000, instance_.rand_) % (Const.MAX_HOUSE_BURN_WEEK + 1);
		instance_.nextHourToBurnHouse_ = Const.randBetween(1, 10000, instance_.rand_) % 24;
	}
	
	public static GodAgent instance() {
		return instance_;
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
		if (Const.IS_SIMULATION_OVER) return;
		
		++min_;

		if (min_ >= 60) {
			min_ = 0;
			++hour_;
			if (hour_ == Const.MORNING_HOUR && !isWeekend()) {
				callEvt(new EventTimeObject(EventTimeObject.Type.MORNING_HOUR));
			} else if (hour_ == Const.NIGHT_BEGIN_HOUR &&
					(getCurrentDay() == Day.FRIDAY || getCurrentDay() == Day.SATURDAY)) {
				callEvt(new EventTimeObject(EventTimeObject.Type.CHILL_HOUR));
			}
			
			if (nextDayToBurnHouse_ == weekDay_ &&
				nextHourToBurnHouse_ == hour_) {
				if (Const.MAX_HOUSE_BURN_WEEK > 0) this.probalityToBurnHouse();
			}
			
			//System.out.println("Clock: Hour = " + hour_);
		}
		
		if (hour_ >= 24) {
			++day_;
			weekDay_ = (++weekDay_ % 7);
			hour_ = 0;
			
			if (weekDay_ == 0) {
				nextDayToBurnHouse_ = Const.randBetween(0, 10000, rand_) % (Const.MAX_HOUSE_BURN_WEEK + 1);
				nextHourToBurnHouse_ = Const.randBetween(1, 1000, rand_) % 24;
				if (Math.random() < 0.6) nextDayToBurnHouse_ = -1;
			}
			
			String logMsg = Const.ENV_TAG + "\n";
			logMsg += "Day of the week: " + this.getCurrentDayStr() + "\n";
			LOGGER.log(Level.INFO, logMsg);
		}
		
		if (day_ >= 365 / Const.YEAR_FACTOR) {
			day_ = 0;
			++year_;
			callEvt(new EventTimeObject(EventTimeObject.Type.YEAR));
			
			String log = "\n-------------------------------------------\n";
			log += "-------------- YEAR SUMMARY ---------------\n";
			log += "- NB AGENTS: " + nbAgents_ + "\n";
			log += "- NB CHILDREN: " + nbChildren_ + "\n";
			log += "----------- END YEAR SUMMARY ------------\n";
			log += "-------------------------------------------";
			
			LOGGER.log(Level.INFO, log);
		}
		
	}
	
	public HumanAgent createAgent(Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		
		HumanAgent agent = new HumanAgent(grid, age, gender, home, workplace);
		timeListeners_.add(agent.getYearListener());
		
		return agent;
	}
	
	public HumanAgent createChildAgent(Context<GridElement> context, HumanAgent parent, Grid<GridElement> grid, Building home) {
		
		HumanAgent a = new HumanAgent(grid, 1, Math.random() >= 0.5f, home, null);
		timeListeners_.add(a.getYearListener());
		
		Network n = (Network)context.getProjection("genealogy");
		n.addEdge(parent, a, Const.PARENTOF);
		
		context.add(a);
		
		return a;
	}
	
	public <T extends AFixedGeography> T getClosestGeography(ArrayList<T> list, PathSearch pathSearch,
															 GridPoint pos) {
		
		if (list.size() == 0) return null;
		
		int nbGeography = list.size();
		
		int minIdx = 0;
		int minVal = Integer.MAX_VALUE;
		for (int i = 0; i < nbGeography; ++i) {
			AFixedGeography s = list.get(i);
				
			pathSearch.search(pos, s.getPos());
			pathSearch.computePath();
			int d1 = pathSearch.getPath().size();
			
			if (d1 < minVal) {
				minVal = d1;
				minIdx = i;
			}
		}
		
		return list.get(minIdx);
	}
	
	/**
	 * Loops through every registered building to find an empty one,
	 * after applying a random selection.
	 * @return The first building being empty after random search.
	 */
	public <T extends AFixedGeography> T getEmptyGeography(ArrayList<T> list) {
		
		if (list.size() == 0) return null;
		
		int nbGeography = list.size();
		
		int initialIdx = rand_.nextInt(nbGeography); 
		int idx = initialIdx;
		
		T elt = list.get(idx);
		while (!elt.isEmpty()) {
			idx = (idx + 1) % nbGeography;
			if (idx == initialIdx) {
				return null;
			}
			elt = list.get(idx);
		}
		
		return elt;
		
	}
	
	public void printRecap() {
		
		String logMsg = "-------------------------------------------\n";
			  logMsg += "---------------SIMULATION END--------------\n";
			  logMsg += "- TOTAL NUMBER AGGENTS THAT LIVED: " + totalNbAgents_ + "\n";
			  logMsg += "- TOTAL NUMBER CHILD BIRTH: " + totalNbChildren_ + "\n";
			  logMsg += "- - - - - - - - - - - - - - - - - - - - - -\n";
			  logMsg += "- YEARS SPENT BY SIMULATION: " + year_ + "\n";
			  logMsg += "- NUMBER OF GENERATION(S): " + (year_ / 30) + "\n";
			  logMsg += "- - - - - - - - - - - - - - - - - - - - - -\n";
			  logMsg += "- NUMBER OF HOUSE(S) BURNT: " + totalNbHouseBurnt_ + "\n";
			  logMsg += "-------------------------------------------\n";
			  
		LOGGER.log(Level.INFO, logMsg);	
	}
	
	/* GETTERS // SETTERS */
	public String getFormattedTime() {
		return getCurrentDayStr() + " at " + hour_ + "h " + min_ + "min";
	}
	
	public int getHour() {
		return hour_;
	}
	
	public int getMin() {
		return min_;
	}
	
	public boolean isHourInRange(int min, int max) {
		return hour_ > min && hour_ < max;
	}
	
	public void incChildNb() {
		++nbChildren_;
		++totalNbChildren_;
	}
	
	public void decChildNb() {
		--nbChildren_;
	}
	
	public void incAgentNb() {
		++nbAgents_;
		++totalNbAgents_;
	}
	
	public void decAgentNb() {
		--nbAgents_;
	}
	
	public void setAgentsNb(int nb) {
		nbAgents_ = nb;
	}
	
	public Day getCurrentDay() {
		return TO_DAY.get(weekDay_);
	}
	
	public String getCurrentDayStr() {
		return TO_DAY_STR.get(getCurrentDay());
	}
	
	public boolean isWeekend() {
		return getCurrentDay() == Day.SATURDAY || getCurrentDay() == Day.SUNDAY;
	}
	
	public int getNbAgents() {
		return nbAgents_;
	}
	
	private void probalityToBurnHouse() {
		
		Context<AAgent> contextAgents = ContextUtils.getContext(this);
		Iterable randAgentIt = contextAgents.getRandomObjects(HumanAgent.class, 1);
		
		if (randAgentIt.iterator().hasNext()) {
			HumanAgent agent = (HumanAgent)randAgentIt.iterator().next();
			Building oldHouse = agent.getHome();
			Building newHouse = getEmptyGeography(Building.globalList);
			
			int x = oldHouse.getX();
			int y = oldHouse.getY();
			
			++totalNbHouseBurnt_;
			oldHouse.setBurnt();
			
			if ((x == agent.getX() && y == agent.getY()) || newHouse == null) {
				
				String logMsg = Const.HOUSE_TAG + "\n";
				logMsg += "Building " + oldHouse.getID() + " burnt and kill Agent " + agent.getID() + " on " + getFormattedTime();
				LOGGER.log(Level.WARNING, logMsg);
				
				agent.die();
				
				if (newHouse == null) {
					Const.IS_SIMULATION_OVER = true;
					String logEnd = Const.ENV_TAG + "\n";
					logEnd += "No more houses are available.";
					logMsg += "The simulation has terminated on " + getFormattedTime();
					LOGGER.log(Level.WARNING, logEnd);
					
					printRecap();
				}
				
				return;
			}
			
			String logMsg = Const.HOUSE_TAG + "\n";
			logMsg += "Building " + oldHouse.getID() + " burnt everybody is safe. Burnt on " + getFormattedTime();
			LOGGER.log(Level.WARNING, logMsg);
			
			agent.setHome(newHouse);
			agent.getYearListener().updateEvent(new EventTimeObject(EventTimeObject.Type.BURNING_TIME));
		}
		
	}
	
	private void callEvt(AEventObject o) {
		timeListeners_.forEach((IUpdateListener e) -> {
			e.updateEvent(o);
		});	
	}
	
	
}
