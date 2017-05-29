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
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

import repast.simphony.space.gis.Road;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.behaviors.MoveTo;
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
	@ScheduledMethod(start = 1, interval = 10)
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
	
	public void getPath(GridPoint dest) {
		Queue<MoveTo> q = new LinkedList<MoveTo>();
		int[] weights = new int[grid_.getDimensions().getWidth() * grid_.getDimensions().getHeight()];
		int width = grid_.getDimensions().getWidth();
		int height = grid_.getDimensions().getHeight();
		for (int i = 0; i < grid_.getDimensions().getWidth() * grid_.getDimensions().getHeight(); i++) {
			/*
			Iterable<GridElement> e = grid_.getObjectsAt(i);
			System.out.println(e);
			Object x = null;
			if (e != null && e.iterator().hasNext())
				x = e.iterator().next();
			System.out.println(x);
			if (x != null && x instanceof Road)
			*/
				weights[i] = -1;
			/*
			else
				weights[i] = -3;
			*/
		}
		weights[dest.getY() * width + dest.getX()] = -2;
		q.add(new MoveTo(this, this.getPos(), grid_));
		int posCurrent = -3;
		do {
			MoveTo mt = q.poll();
			GridPoint current = mt.getDest();
			if (current.getX() < 0 || current.getX() >= width || current.getY() < 0 || current.getY() >= height
					|| (weights[current.getY() * width + current.getX()] != -1
					&& weights[current.getY() * width + current.getX()] != -2))
				continue;
			if (weights[current.getY() * width + current.getX()] == -2) {
				weights[current.getY() * width + current.getX()] = posCurrent;
				break;
			}
			weights[current.getY() * width + current.getX()] = posCurrent;
			posCurrent = current.getY() * width + current.getX();
			q.add(new MoveTo(this, new GridPoint(current.getX() - 1, current.getY()), grid_));
			q.add(new MoveTo(this, new GridPoint(current.getX(), current.getY() - 1), grid_));
			q.add(new MoveTo(this, new GridPoint(current.getX() + 1, current.getY()), grid_));
			q.add(new MoveTo(this, new GridPoint(current.getX(), current.getY() + 1), grid_));
		} while (!q.isEmpty());
		int curr = dest.getY() * width + dest.getX();
		Stack<MoveTo> m = new Stack<MoveTo>();
		while (curr != -3) {
			m.push(new MoveTo(this, new GridPoint(curr % width, curr / width), grid_));
			curr = weights[curr];
		}
		while (!m.isEmpty())
			behaviors_.add(m.pop());
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