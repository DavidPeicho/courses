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

import syma.environment.Building;
import syma.environment.School;
import syma.environment.WorkPlace;
import syma.goal.AGoal;
import syma.goal.Follow;
import syma.goal.MoveTo;
import syma.goal.Wait;
import syma.events.AEventObject;
import syma.events.EventTimeObject;
import syma.events.IUpdateListener;
import syma.events.SuccessEvent;
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
	
	Random rand_;

	private IUpdateListener bringToSchoolListener_ = (AEventObject o) -> {
		HumanAgent.this.pollGoal();
		HumanAgent.this.addGoalMoveToWork();
	};

	private boolean hasToWork() { return workplace_ != null; }

	private boolean hasToBringToSchool() {
		Stream<AAgent> c = getChildren();

		if (school_.isPresent() && c != null) {
			Optional<AAgent> child = c.findFirst();
			return child.isPresent() && child.get().getPos().equals(getPos());
		}

		return false;
	}

	private IUpdateListener yearListener_ = new IUpdateListener() {

		@Override
		public void updateEvent(AEventObject e) {
			if (e == null) return;

			EventTimeObject obj = (EventTimeObject)e;
			if (obj.type == EventTimeObject.Type.YEAR) {
				++age_;
				if (age_ == 18) {
					turnAdult();
				} else if (age_ == searchPartnerAge_) {
					if (!HumanAgent.this.hasCompanion()) findPartner();
				}
			} else if (obj.type == EventTimeObject.Type.MORNING_HOUR) {
				if (age_ >= 18 && hasToWork()) {
					// FIXME they won't go to school if it doesn't have a job!!!!!
					if (hasToBringToSchool()) {
						addGoal(new MoveTo(HumanAgent.this, bringToSchoolListener_, school_.get(), grid_));
						order_ = true;
					} else {
						HumanAgent.this.addGoalMoveToWork();
					}
				}
			}
		}
	};

	public HumanAgent(Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		super(grid);
		
		rand_ = new Random();
		
		age_ = age;
		gender_ = gender;
		//maxAge_ = Const.MAX_AGE + (int)((Math.random() * 30) - 15);
		maxAge_ = (rand_.nextInt(30) - 15) + Const.MAX_AGE; 
		workplace_ = workplace;
		order_ = false;

		home_ = home;
		home_.addAgent(this);

		PathSearch ps = new PathSearch(grid_);

		System.out.println("SCHOOL NB = " + School.globalList.size());
		/*school_ = School.globalList.stream().sorted(
				(School s1, School s2) -> {
					GridPoint h = home_.getPos();
					ps.search(h, s1.getPos());
					ps.computePath();
					int d1 = ps.getPath().size();
					
					ps.search(h, s2.getPos());
					ps.computePath();
					int d2 = ps.getPath().size();
					
					System.out.println("D1 = " + d1 + " | D2 = " + d2);
					
					return d2 - d1;
				}).findFirst();*/

		int minIdx = 0;
		int minVal = Integer.MAX_VALUE;
		GridPoint h = home_.getPos();
		for (int i = 0; i < School.globalList.size(); ++i) {
			School s = School.globalList.get(i);
			
			ps.search(h, s.getPos());
			ps.computePath();		
			int d1 = ps.getPath().size();
			System.out.println(s.getX() + " | " + s.getY());
			
			if (d1 < minVal) {
				d1 = minVal;
				minIdx = i;
			}
		}
		
		school_ = Optional.of(School.globalList.get(minIdx));

		searchPartnerAge_ = rand_.nextInt(Const.MAX_SEARCH_PARTNER_AGE + 1 - 18) + 18;
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
		AGoal g = peekGoal();
		if (g != null && g.success()) {
			g.triggerCallback(null);
			if (order_) {
				order_ = false;
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
	}

	private void die() {
		LOGGER.log(Level.INFO, "Agent " + id_ + " died at " + age_);

		GodAgent env = GodAgent.instance();
		env.decAgentNb();

		// Removes link between companion
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
			triggerCondition="$watcher.getAge() < 18 && $watcher.isParent($watchee)")
	public void react() {
		Stream<AAgent> ps = getParents().filter((AAgent a) -> ((HumanAgent)a).getOrder());
		Optional<AAgent> p = ps.findFirst();
		if (p.isPresent()) {
			IUpdateListener clbk = (AEventObject o) -> {
				HumanAgent.this.pollGoal();
			};
			addGoal(new Follow(this, clbk, p.get(), grid_));
		} else {
			AGoal g = peekGoal();
			if (g != null) {
				if (g instanceof Follow) {
					((Follow)g).setContinue(false);
				}
			} else {
				LOGGER.log(Level.INFO, "Agent " + id_ + "react twice to parent!");
			}
		}
	}

	public void addGoalMoveToWork() {
		MoveTo moveToWork = new MoveTo(this, e -> {
			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoalWaitAtWork();
		}, workplace_, grid_);
		
		int randomMin = rand_.nextInt(Const.MAX_DELAY_BEFORE_WORK);
		Wait waitBeforeWork = new Wait(this, e -> {
			HumanAgent.this.pollGoal();
			HumanAgent.this.addGoal(moveToWork);
		}, Const.timeToTick(0, 0, randomMin));
		
		this.addGoal(waitBeforeWork);
	}

	public void addGoalWaitAtWork() {
		int workHour = workplace_.getEndHour() - workplace_.getStartHour();
		
		Wait waitAtWork = new Wait(HumanAgent.this, e -> {
			HumanAgent.this.pollGoal();
			if (HumanAgent.this.hasToBringToSchool()) {
				Optional<AAgent> childOption = HumanAgent.this.getChildren().findFirst(); 
				if (childOption.isPresent()) {
					HumanAgent child = (HumanAgent)childOption.get();
					School school = child.school_.get();
					if (child.getPos().getX() == school.getX() && child.getPos().getY() == school.getY()) {
						HumanAgent.this.addGoalGetChildren();
						return;
					}
				}
			}
			MoveTo moveToHouse = new MoveTo(HumanAgent.this, null, home_, grid_);
			moveToHouse.setAutoremoveWhenReached(true);
			HumanAgent.this.addGoal(moveToHouse);
		}, Const.timeToTick(0, workHour, 0));
		
		this.addGoal(waitAtWork);
	}
	
	public void addGoalGetChildren() {
		MoveTo moveToSchool = new MoveTo(this, e -> {
			HumanAgent.this.pollGoal();
			MoveTo moveToHouse = new MoveTo(HumanAgent.this, null, home_, grid_);
			moveToHouse.setAutoremoveWhenReached(true);
			HumanAgent.this.order_ = true;
		}, school_.get(), grid_);
		
		this.addGoal(moveToSchool);
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

		LOGGER.log(Level.INFO, "Agent " + id_ + " is now major!");
		LOGGER.log(Level.INFO, "Agent " + id_ + " has now a new job and a house");
	}
}
