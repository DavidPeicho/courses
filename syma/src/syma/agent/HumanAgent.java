package syma.agent;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

import java.util.Optional;
import java.util.Random;
import java.util.Stack;

import syma.environment.Bar;
import syma.environment.Building;
import syma.environment.BusStop;
import syma.environment.School;
import syma.environment.ShoppingCentre;
import syma.environment.WorkPlace;
import syma.goal.AGoal;
import syma.goal.DriveTo;
import syma.goal.Follow;
import syma.goal.MoveTo;
import syma.goal.Wait;
import syma.goal.WaitForBus;
import syma.events.AEventObject;
import syma.events.EventTimeObject;
import syma.events.IUpdateListener;
import syma.main.GridElement;
import syma.utils.Const;
import syma.utils.PathSearch;

public class HumanAgent extends AAgent {

	private static Logger LOGGER = Logger.getLogger(HumanAgent.class.getName());

	private int age_;
	private int searchPartnerAge_;
	private boolean gender_;

	private Building home_;
	private WorkPlace workplace_;
	private Optional<School> school_;

	private boolean order_;
	private float maxAge_;

	private PathSearch pathSearch_;

	Random rand_;

	private boolean hasToWork() { return workplace_ != null; }

	private boolean hasToBringToSchool() {
		Stream<AAgent> c = getChildren();

		if (school_.isPresent() && c != null) {
			return c.findFirst().isPresent();
		}

		return false;
	}

	private IUpdateListener yearListener_ = new IUpdateListener() {

		@Override
		public void updateEvent(AEventObject e) {
			if (e == null) return;

			EventTimeObject obj = (EventTimeObject)e;
			switch (obj.type) {
			case YEAR:
				++age_;
				if (age_ < 18) {
					System.out.println("AGGGGGGGGGGGGGGGGGGGGGGE = " + age_);
				}
				if (age_ == Const.MAJOR_AGE) {
					turnAdult();
				} else if (age_ == searchPartnerAge_) {
					if (!HumanAgent.this.hasCompanion()) findPartner();
				}
				break;
			case MORNING_HOUR:
				if (age_ >= Const.MAJOR_AGE && hasToWork()) {
					// FIXME they won't go to school if it doesn't have a job!!!!!
					if (hasToBringToSchool()) {
						HumanAgent.this.addGoalGoToSchoolAndWork();
					} else {
						HumanAgent.this.addGoalMoveToWork(true);
					}
				}
				break;
			case CHILL_HOUR:
				if (!goals_.isEmpty()) {
					break;
				}
				if (age_ < Const.MAJOR_AGE || HumanAgent.this.getChildren().findFirst().isPresent()) {
					break;
				}
				if (Math.random() <= Const.SPARE_TIME_RATE) {
					GodAgent env = GodAgent.instance();
					Bar b = env.getClosestGeography(Bar.globalList, pathSearch_, HumanAgent.this.getPos());
					if (b != null) {
						HumanAgent.this.addGoalHangOut(b);
					}
				}
				break;
			default:
				break;
			}
		}
	};

	public HumanAgent(Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		super(grid);

		rand_ = new Random();

		age_ = age;
		gender_ = gender;
		maxAge_ = Const.randBetween(Const.MIN_DEATH_AGE, Const.MAX_AGE, rand_);
		workplace_ = workplace;
		order_ = false;

		home_ = home;
		home_.addAgent(this);

		pathSearch_ = new PathSearch(grid_);

		school_ = getClosestSchool();

		searchPartnerAge_ = Const.randBetween(18, Const.MAX_SEARCH_PARTNER_AGE, rand_);
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
		AGoal g = peekGoal();
		if (g != null && g.success()) {
			deactivateOrder();
			g.triggerCallback(null);
		}

		GodAgent env = GodAgent.instance();

		// Checks whether there are food
		// in its house.
		// If the house is empty, it should fill
		// the house whenever it is possible.
		if (home_.isFoodEmpty() && age_ >= 18 && goals_.isEmpty()) {

			if (env.isHourInRange(Const.END_AFTERNOON, Const.NIGHT_BEGIN_HOUR) ||
					env.isWeekend()) {
				ShoppingCentre s = env.getClosestGeography(ShoppingCentre.globalList, pathSearch_, HumanAgent.this.getPos());
				if (s != null) {
					this.addGoalShopping(s);
				}	
			}
		}

	}

	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step() {
		super.step();

		double deathRate = ((float)age_ / (float)maxAge_) * Math.random();

		if (deathRate >= 0.85) {
			die();
		}

		// Updates house food level
		this.home_.consumeFood();
	}

	private void die() {
		GodAgent env = GodAgent.instance();
		env.decAgentNb();

		String logMsg = Const.AGENT_TAG + "\n";
		logMsg += "Agent " + id_ + " died at " + age_ + " on " + env.getFormattedTime();
		LOGGER.log(Level.INFO, logMsg);

		// Kills all children that are minor
		// if parent die.
		if (this.hasChildren()) {
			this.getChildren().forEach(a -> {
				((HumanAgent)a).die();
			});
		}

		// Removes link between companion and children
		Optional<AAgent> partner = this.getCompanions().findFirst();
		if (partner.isPresent()) {
			Context<HumanAgent> context = ContextUtils.getContext(this);
			Network n = (Network)context.getProjection("genealogy");
			n.getEdges(this).forEach(e -> {
				n.removeEdge((RepastEdge)e);
			});
		}

		// Removes agent from home
		home_.removeAgent(this);
		if (home_.isEmpty()) {
			LOGGER.log(Level.INFO, "A new house is now available and living.");
		}

		env.timeListeners_.remove(this.yearListener_);

		Context<HumanAgent> context = ContextUtils.getContext(this);
		context.remove(this);
	}

	@Watch(watcheeClassName="syma.agent.HumanAgent",
			watcheeFieldNames="order_",
			query="linked_from 'genealogy'",
			whenToTrigger=WatcherTriggerSchedule.IMMEDIATE,
			triggerCondition="$watcher.getAge() < syma.utils.Const.MAJOR_AGE && $watcher.isParent($watchee)")
	public void react() {
		Stream<AAgent> ps = getParents().filter((AAgent a) -> ((HumanAgent)a).getOrder());
		Optional<AAgent> p = ps.findFirst();

		if (p.isPresent()) {
			addGoal(new Follow(this, ((o) -> HumanAgent.this.pollGoal()), p.get(), grid_));
		} else {
			AGoal g = peekGoal();
			if (g != null) {
				if (g instanceof Follow) {
					((Follow)g).setContinue(false);
					g.update();
				}
			} else {
				LOGGER.log(Level.INFO, "Agent " + id_ + "react twice to parent!");
			}
		}
	}

	private MoveTo computeTraject(GridElement dest, IUpdateListener callback, boolean autoremove, boolean child) {
		BusStop busStart = Tram.getNearestStop(getPos());
		BusStop busEnd = Tram.getNearestStop(dest.getPos());
		PathSearch p = new PathSearch(grid_);
		p.search(getPos(), busStart.getPos());
		Stack<GridPoint> s1 = p.computePath();
		int d1 = (s1 == null) ? 0 : s1.size();
		p.search(busStart.getPos(), busEnd.getPos());
		Stack<GridPoint> s2 = p.computePath();
		p.search(busEnd.getPos(), dest.getPos());
		int d2 = (s2 == null) ? 0 : s2.size() / 2;;
		Stack<GridPoint> s3 = p.computePath();
		p.search(getPos(), dest.getPos());
		int d3 = (s3 == null) ? 0 : s3.size();
		Stack<GridPoint> s4 = p.computePath();
		int d4= (s4 == null) ? 0 : s4.size();
		int busDist = d1 + d2 + d3;
		int walkDist = d4;
		if (busDist <= walkDist && !busStart.getPos().equals(busEnd.getPos())) {
			WaitForBus waitForBus = new WaitForBus(HumanAgent.this, null, busStart.getRoadStop(), grid_);
			DriveTo driveTo = new DriveTo(HumanAgent.this, null, busEnd.getRoadStop(), Tram.instance, grid_);
			IUpdateListener l1 = (AEventObject o) -> {
				HumanAgent.this.pollGoal();
				HumanAgent.this.addGoal(waitForBus);
				if (child) {
					activateOrder();
				}
			};
			MoveTo moveToBusStart = new MoveTo(HumanAgent.this, null, busStart, grid_);
			moveToBusStart.addCallback(l1);
			IUpdateListener l2 = (AEventObject o) -> {
				HumanAgent.this.pollGoal();
				HumanAgent.this.addGoal(driveTo);
				if (child) {
					activateOrder();
				}

			};
			MoveTo moveToEnd = new MoveTo(HumanAgent.this, null, dest, grid_);

			waitForBus.addCallback(l2);
			IUpdateListener l3 = (AEventObject o) -> {
				HumanAgent.this.pollGoal();
				HumanAgent.this.addGoal(moveToEnd);
				if (child) {
					activateOrder();
				}
			};
			driveTo.addCallback(l3);
			moveToEnd.addCallback(callback);
			moveToEnd.setAutoremoveWhenReached(autoremove);
			return moveToBusStart;
		}
		else {
			MoveTo moveTo = new MoveTo(HumanAgent.this, callback, dest, grid_);
			moveTo.setAutoremoveWhenReached(autoremove);
			return moveTo;
		}
	}

	public void addGoalMoveToWork(boolean wait) {
		GodAgent env = GodAgent.instance();

		MoveTo moveToWork = computeTraject(workplace_, e -> {
			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoalWaitAtWork(); 
		}, false, false);

		if (!wait) {
			addGoal(moveToWork);
			return;
		}

		int randomMin = rand_.nextInt(Const.MAX_DELAY_BEFORE_WORK);
		Wait waitBeforeWork = new Wait(this, e -> {

			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoal(moveToWork);

			String logMsg = Const.WORK_TAG + "\n";
			logMsg += "Agent " + id_ + " is moving to work on " + env.getFormattedTime();
			LOGGER.log(Level.INFO, logMsg);

		}, Const.timeToTick(0, 0, randomMin));

		this.addGoal(waitBeforeWork);
	}

	public void addGoalWaitAtWork() {

		GodAgent env = GodAgent.instance();
		int workHour = workplace_.getEndHour() - workplace_.getStartHour();

		Wait waitAtWork = new Wait(HumanAgent.this, e -> {
			HumanAgent.this.pollGoal();

			if (HumanAgent.this.hasToBringToSchool()) {
				HumanAgent child = (HumanAgent)HumanAgent.this.getChildren().findFirst().get();
				if (child.getPos().equals(school_.get().getPos())) {
					HumanAgent.this.addGoalGoToSchoolAndHome();
					return;
				}
			}

			HumanAgent.this.addGoal(computeTraject(home_, null, true, false));

			String logMsg = Const.WORK_TAG + "\n";
			logMsg += "Agent " + id_ + " is coming home on " + env.getFormattedTime();
			LOGGER.log(Level.INFO, logMsg);
		}, Const.timeToTick(0, workHour, 0));

		this.addGoal(waitAtWork);
	}

	public void addGoalGoToSchoolAndHome() {
		MoveTo moveToSchool = computeTraject(school_.get(), e -> {
			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoal(computeTraject(home_, null, true, true));
			HumanAgent.this.activateOrder();
		}, false, false);

		this.addGoal(moveToSchool);
	}

	public void addGoalAfterWait(int min, AGoal nextGoal, String logMsg) {

		Wait waitAt = new Wait(HumanAgent.this, e -> {
			HumanAgent.this.pollGoal();

			HumanAgent.this.addGoal(nextGoal);

			if (logMsg != null) LOGGER.log(Level.INFO, logMsg);	

		}, Const.timeToTick(0, 0, min));

		this.addGoal(waitAt);

	}

	public void addGoalGoToSchoolAndWork() {
		MoveTo moveToSchool = computeTraject(school_.get(), e -> {
			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoalMoveToWork(false);
		}, false, true);

		this.addGoal(moveToSchool);
		activateOrder();
	}

	public void addGoalShopping(ShoppingCentre shoppingCentre) {
		GodAgent env = GodAgent.instance();

		MoveTo moveTo = computeTraject(shoppingCentre, e -> {

			HumanAgent.this.pollGoal();

			int timeAtShop = Const.randBetween(Const.MIN_NB_MIN_SHOPPING, Const.MAX_NB_MIN_SHOPPING, rand_);
			System.out.println(timeAtShop);

			String logMsg = Const.SHOPPING_TAG + "\n";
			logMsg += "Agent " + id_ + " is coming home on " + env.getFormattedTime() + "\n";
			logMsg += "Building " + home_.getID() + " is now filled!";

			MoveTo moveToHouse = computeTraject(home_, null, true, false);
			HumanAgent.this.addGoalAfterWait(timeAtShop, moveToHouse, logMsg);

			home_.upFoodLevel();

		}, false, false);

		String logMsg = Const.SHOPPING_TAG + "\n";
		logMsg += "Agent " + id_ + " is going to shop on " + env.getFormattedTime();
		LOGGER.log(Level.INFO, logMsg);

		this.addGoal(moveTo);
	}

	public void addGoalHangOut(Bar bar) {
		GodAgent env = GodAgent.instance();
		int minBeforeGo = Const.randBetween(0, Const.MAX_DELAY_BEFORE_WORK, rand_);
		int minTimeToSpend = Const.randBetween(Const.MIN_NB_MIN_HANG_OUT, Const.MAX_NB_MIN_HANG_OUT, rand_); 

		Wait waitBeforeGo = new Wait(this, eInit -> {

			HumanAgent.this.pollGoal();

			MoveTo moveToBar = computeTraject(bar, e2 -> {

				HumanAgent.this.pollGoal();

				Wait waitAtBar = new Wait(HumanAgent.this, f -> {

					HumanAgent.this.pollGoal();
					MoveTo moveToHouse = computeTraject(home_, null, true, false);
					HumanAgent.this.addGoal(moveToHouse);

					String logMsg = Const.HANG_OUT_TAG + "\n";
					logMsg += "Agent " + id_ + " is coming home on " + env.getFormattedTime();
					LOGGER.log(Level.INFO, logMsg);

				}, Const.timeToTick(0, 0, minTimeToSpend)); 
				HumanAgent.this.addGoal(waitAtBar);

			}, false, false);

			HumanAgent.this.addGoal(moveToBar);

			String logMsg = Const.HANG_OUT_TAG + "\n";
			logMsg += "Agent " + id_ + " is hanging out on " + env.getFormattedTime();
			LOGGER.log(Level.INFO, logMsg);

		}, Const.timeToTick(0, 0, minBeforeGo));

		this.addGoal(waitBeforeGo);
	}

	/* GETTERS // SETTERS */

	public boolean getGender() {
		return gender_;
	}

	public int getAge() {
		return age_;
	}

	public void setHome(Building building) {
		if (home_ != null) {
			home_.removeAgent(this);
			home_ = building;
		}
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

	public boolean getOrder() {
		return order_;
	}

	public void deactivateOrder() {
		if (order_) {
			order_ = false;
		}
	}

	public void activateOrder() {
		if (!order_) {
			order_ = true;
		}
	}

	public IUpdateListener getYearListener() {
		return yearListener_;
	}

	public Stream<AAgent> getCompanions() {
		return getRelatedAgent(Const.MARRIEDTO, true);
	}

	public boolean hasCompanion() {
		return getCompanions().findFirst().isPresent();
	}

	public Stream<AAgent> getParents() {
		return getRelatedAgent(Const.PARENTOF, true);
	}

	public boolean isParent(HumanAgent a) {
		return this.getParents().anyMatch((e) -> e == a);
	}

	public Stream<AAgent> getChildren() {
		return getRelatedAgent(Const.PARENTOF, false);
	}

	public Stream<AAgent> getRelatedAgent(double relationship, boolean in) {
		Context c = ContextUtils.getContext(this);
		if (c == null) return null;

		Network n = (Network)c.getProjection("genealogy");
		Iterable<RepastEdge> edges = in ? n.getInEdges(this) : n.getOutEdges(this);
		return StreamSupport.stream(edges.spliterator(), false)
				.filter((RepastEdge e) -> e.getWeight() == relationship)
				.map((RepastEdge e) -> (AAgent)(in ? e.getSource() : e.getTarget()));
	}

	public boolean hasChildren() {
		return getChildren().findFirst().isPresent();
	}

	public boolean hasCompanions() {
		return getCompanions().findFirst().isPresent();
	}

	/**
	 * Searches through every agents to find
	 * a partner to live with.
	 */
	private void findPartner() {
		Context context = ContextUtils.getContext(this);
		Stream<HumanAgent> agents = context.stream().filter((Object e) -> {
			if (!(e instanceof HumanAgent)) return false;

			HumanAgent a = (HumanAgent)e;
			if (a.getID() == id_) return false;

			return a.gender_ != gender_ && !a.hasCompanion() && !a.getParents().anyMatch(p -> p == e);
		});

		Optional<HumanAgent> opt = agents.findFirst();
		if (opt.isPresent()) {
			HumanAgent partner = opt.get();
			Network n = (Network)context.getProjection("genealogy");
			n.addEdge(partner, this, Const.MARRIEDTO);
			n.addEdge(this, partner, Const.MARRIEDTO);

			partner.setHome(this.home_);

			GodAgent env = GodAgent.instance();
			env.incAgentNb();
			env.incChildNb();

			// Spawns a child agent
			HumanAgent child = env.createChildAgent(context, this, grid_, home_);
			grid_.moveTo(child, home_.getX(), home_.getY());

			LOGGER.log(Level.INFO, "Agent " + id_ + " get married with Agent " + partner.getID());
			LOGGER.log(Level.INFO, "A new agent is born from " + id_ + " and " + partner.getID());
		}

	}

	private Optional<School> getClosestSchool() {

		if (School.globalList.size() == 0) {
			return Optional.ofNullable(null);
		}

		int minIdx = 0;
		int minVal = Integer.MAX_VALUE;
		GridPoint h = home_.getPos();
		for (int i = 0; i < School.globalList.size(); ++i) {
			School s = School.globalList.get(i);

			pathSearch_.search(h, s.getPos());
			pathSearch_.computePath();
			int d1 = pathSearch_.getPath().size();

			if (d1 < minVal) {
				minVal = d1;
				minIdx = i;
			}
		}

		return Optional.of(School.globalList.get(minIdx));
	}

	private void turnAdult() {
		GodAgent env = GodAgent.instance();
		env.decChildNb();

		WorkPlace work = env.getEmptyGeography(WorkPlace.globalList);
		Building house = env.getEmptyGeography(Building.globalList);

		this.workplace_ = work;
		if (house != null) {
			house.addAgent(this);
			home_ = house;
		}

		// Removes link with parent
		Context<HumanAgent> context = ContextUtils.getContext(this);
		Network n = (Network)context.getProjection("genealogy");
		n.getEdges(this).forEach(e -> {
			n.removeEdge((RepastEdge)e);
		});

		goals_.clear();
		addGoal(computeTraject(home_, null, true, false));

		LOGGER.log(Level.INFO, "Agent " + id_ + " is now major!");
		LOGGER.log(Level.INFO, "Agent " + id_ + " has now a new job and a house");
	}

	@Override
	public String toString() {
		return "HumanAgent [age_=" + age_ + ", order_=" + order_ + ", id_=" + id_ + "]";
	}
}
