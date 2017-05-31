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
import repast.simphony.util.ContextUtils;

import java.util.Optional;

import repast.simphony.space.grid.GridPoint;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.environment.School;
import syma.environment.WorkPlace;
import syma.goal.AGoal;
import syma.goal.Follow;
import syma.goal.MoveTo;
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
	private boolean gender_;

	private Building home_;
	private WorkPlace workplace_;
	private Optional<School> school_;

	private boolean order_;
	private float maxAge_;

	private IUpdateListener bringToSchoolListener_ = (AEventObject o) -> {
		HumanAgent.this.pollGoal();
		HumanAgent.this.addGoal(new MoveTo(HumanAgent.this, null, workplace_, grid_));
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
			} else if (obj.type == EventTimeObject.Type.MORNING_HOUR) {
				if (hasToWork()) {
					// FIXME they won't go to school if it doesn't have a job!!!!!
					if (hasToBringToSchool()) {
						addGoal(new MoveTo(HumanAgent.this, bringToSchoolListener_, school_.get(), grid_));
						order_ = true;
					} else {
						addGoal(new MoveTo(HumanAgent.this, null, workplace_, grid_));
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
		home_ = home;
		workplace_ = workplace;
		order_ = false;
		PathSearch ps = new PathSearch(grid_);
		school_ = School.globalList.stream().sorted(
				(School s1, School s2) -> {
					GridPoint h = home_.getPos();
					int d1 = ps.search(h, s1.getPos()).length;
					int d2 = ps.search(h, s2.getPos()).length;
					return d2 - d1;
				}).findFirst();
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

		Context<HumanAgent> context = ContextUtils.getContext(this);
		context.remove(this);
	}

	@Watch(watcheeClassName="syma.agent.HumanAgent",
			watcheeFieldNames="order_",
			query="linked_from 'genealogy'",
			whenToTrigger=WatcherTriggerSchedule.LATER,
			triggerCondition="$watcher.getAge() < 18")
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

	public void setOrder(boolean order) {
		order_ = order;
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

	public Stream<AAgent> getParents() {
		return getRelatedAgent(Const.PARENTOF, true);
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
