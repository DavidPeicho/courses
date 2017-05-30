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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.behaviors.MoveTo;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.environment.Road;
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
	
	private boolean order_;

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
		order_ = false;
	}
	
	@Override
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step() {
		super.step();
		double deathRate = ((float)age_ / (float)maxAge_) * Math.random();

		// Test Watcher
		if (!order_ && id_ == 1 && age_ >= 30) {
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
		   whenToTrigger=WatcherTriggerSchedule.IMMEDIATE)
	public void react() {
		Stream<AAgent> ps = getParents().filter((AAgent a) -> ((HumanAgent)a).getOrder());
		Optional<AAgent> p = ps.findFirst();

		if (p.isPresent()) {
			HumanAgent hp = (HumanAgent)p.get();
			System.out.println("I am " + id_);
			System.out.println("I received an order from " + hp.getID());
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


	public UpdateListener getYearListener() {
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
		Network n = (Network)ContextUtils.getContext(this).getProjection("genealogy");
		Iterable<RepastEdge> edges = in ? n.getInEdges(this) : n.getOutEdges(this);
		return StreamSupport.stream(edges.spliterator(), false)
				.filter((RepastEdge e) -> e.getWeight() == relationship)
				.map((RepastEdge e) -> (AAgent)(in ? e.getSource() : e.getTarget()));
	}
}
