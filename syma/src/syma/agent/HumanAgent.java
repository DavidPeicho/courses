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
import syma.environment.WorkPlace;
import syma.goal.Follow;
import syma.goal.MoveTo;
import syma.events.AEventObject;
import syma.events.EventTimeObject;
import syma.events.IUpdateListener;
import syma.main.GridElement;
import syma.utils.Const;

public class HumanAgent extends AAgent {

	private static Logger LOGGER = Logger.getLogger(HumanAgent.class.getName());

	private int age_;
	private boolean gender_;
	
	private Building home_;
	private WorkPlace workplace_;
	
	private boolean order_;

	private float maxAge_;

	private IUpdateListener yearListener_ = new IUpdateListener() {
		
		@Override
		public void updateEvent(AEventObject e) {
			if (e == null) return;
			
			EventTimeObject obj = (EventTimeObject)e;
			if (obj.type == EventTimeObject.Type.YEAR) {
				++age_;
			} else if (obj.type == EventTimeObject.Type.MORNING_HOUR) {
				Stream<AAgent> c = getChildren(); 
				if (c != null && !c.findFirst().isPresent() && workplace_ != null) {
					//HumanAgent.this.addGoal(new MoveTo(HumanAgent.this, workplace_, grid_));
				}
			}	
		}
		
	};

	public HumanAgent(Grid<GridElement> grid, int age, boolean gender, WorkPlace workplace) {
		super(grid);
		age_ = age;
		gender_ = gender;
		maxAge_ = Const.MAX_AGE + (int)((Math.random() * 30) - 15);
		workplace_ = workplace;
		order_ = false;
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void decide() {
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step() {
		super.step();
		double deathRate = ((float)age_ / (float)maxAge_) * Math.random();

		if (!order_ && id_ == 10 && age_ >= 40) {
			addGoal(new MoveTo(this, workplace_, grid_));
			order_ = true;
		}

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
		   whenToTrigger=WatcherTriggerSchedule.IMMEDIATE,
		   triggerCondition="$watcher.getAge() < 18")
	public void react() {
		Stream<AAgent> ps = getParents().filter((AAgent a) -> ((HumanAgent)a).getOrder());
		Optional<AAgent> p = ps.findFirst();

		if (p.isPresent()) {
			LOGGER.log(Level.INFO, "Agent " + id_ + " should follow its parent.");
			addGoal(new Follow(this, p.get(), grid_));
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
}
