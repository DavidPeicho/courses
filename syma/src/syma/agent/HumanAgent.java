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
				System.out.println("Age is " + age_ + " Married at " + searchPartnerAge_);
				if (age_ == searchPartnerAge_ && !HumanAgent.this.hasCompanion()) {
					findPartner();
				}
			} else if (obj.type == EventTimeObject.Type.MORNING_HOUR) {
				if (age_ >= 18 && hasToWork()) {
					// FIXME they won't go to school if it doesn't have a job!!!!!
					if (hasToBringToSchool()) {
						addGoal(new MoveTo(HumanAgent.this, bringToSchoolListener_, school_.get(), grid_));
						order_ = true;
						System.out.println("ORDER TRIGGERED ON " + getID());
					} else {
						HumanAgent.this.addGoalMoveToWork();
					}
				}
			}
		}
	};

	public HumanAgent(Grid<GridElement> grid, int age, boolean gender, Building home, WorkPlace workplace) {
		super(grid);
		age_ = age;
		gender_ = gender;
		maxAge_ = Const.MAX_AGE + (int)((Math.random() * 30) - 15);
		workplace_ = workplace;
		order_ = false;
		
		home_ = home;
		home_.addAgent(this);
		
		PathSearch ps = new PathSearch(grid_);
		school_ = School.globalList.stream().sorted(
				(School s1, School s2) -> {
					GridPoint h = home_.getPos();
					int d1 = ps.search(h, s1.getPos()).length;
					int d2 = ps.search(h, s2.getPos()).length;
					return d2 - d1;
				}).findFirst();
		
		Random rand = new Random();
		searchPartnerAge_ = rand.nextInt(Const.MAX_SEARCH_PARTNER_AGE + 1 - 18) + 18;
	}

	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
		AGoal g = peekGoal();
		if (g != null && g.success()) {
			g.triggerCallback(new SuccessEvent());
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
	
	/**
	 * Searches through every agents to find
	 * a partner to live with.
	 */
	private void findPartner() {
		Context<HumanAgent> context = ContextUtils.getContext(this);
		Stream<HumanAgent> agents = context.stream().filter((GridElement e) -> {
			if (!(e instanceof HumanAgent)) return false;
			
			HumanAgent a = (HumanAgent)e;
			if (a.getID() == id_) return false;
			
			return a.gender_ != gender_ && !a.hasCompanion() && a.getParents().anyMatch(p -> p != e);
		});
		
		Optional<HumanAgent> opt = agents.findFirst(); 
		if (opt.isPresent()) {
			HumanAgent partner = opt.get();
			Network n = (Network)context.getProjection("genealogy");
			n.addEdge(partner, this, Const.MARRIEDTO);
			n.addEdge(this, partner, Const.MARRIEDTO);
			
			partner.setHome(this.home_);
			
			// Spawns a child agent
			GodAgent env = GodAgent.instance();
			HumanAgent child = env.createChildAgent(grid_, home_);
			context.add(child);
			grid_.moveTo(child, home_.getX(), home_.getY());
			n.addEdge(this, child, Const.PARENTOF);
			
			LOGGER.log(Level.INFO, "Agent " + id_ + " get married with Agent " + partner.getID());
			LOGGER.log(Level.INFO, "A new agent is born from " + id_ + " and " + partner.getID());
		}
		
	}
	
	@Watch(watcheeClassName="syma.agent.HumanAgent",
			watcheeFieldNames="order_",
			query="linked_from 'genealogy'",
			whenToTrigger=WatcherTriggerSchedule.LATER,
			triggerCondition="$watcher.getAge() < 18 && $watcher.isParent($watchee)")
	public void react() {
		Stream<AAgent> ps = getParents().filter((AAgent a) -> ((HumanAgent)a).getOrder());
		Optional<AAgent> p = ps.findFirst();
		if (p.isPresent()) {
			LOGGER.log(Level.INFO, "Agent " + id_ + " should follow its parent " + p.get().getID());
			IUpdateListener clbk = (AEventObject o) -> {
				HumanAgent.this.pollGoal();
			};
			addGoal(new Follow(this, clbk, p.get(), grid_));
		} else {
			LOGGER.log(Level.INFO, "Agent " + id_ + " should not follow its parent.");
			peekGoal().triggerCallback(new SuccessEvent());
		}
	}
	
	public void addGoalMoveToWork() {
		IUpdateListener callback = new IUpdateListener() {

			@Override
			public void updateEvent(AEventObject e) {
				HumanAgent.this.pollGoal();
				HumanAgent.this.addGoalWaitAtWork();
			}
			
		};
		MoveTo moveToWork = new MoveTo(HumanAgent.this, callback, workplace_, grid_); 
		this.addGoal(moveToWork);
	}
	
	public void addGoalWaitAtWork() {
		IUpdateListener callback = new IUpdateListener() {

			@Override
			public void updateEvent(AEventObject e) {
				HumanAgent.this.pollGoal();
				MoveTo moveToHouse = new MoveTo(HumanAgent.this, null, home_, grid_);
				moveToHouse.setAutoremoveWhenReached(true);
				HumanAgent.this.addGoal(moveToHouse);
			}
			
		};
		Wait waitAtWork = new Wait(HumanAgent.this, callback, Const.timeToTick(0, 3, 0));
		this.addGoal(waitAtWork);
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
}
