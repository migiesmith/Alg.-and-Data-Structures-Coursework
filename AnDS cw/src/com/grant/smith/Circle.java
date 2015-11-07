package com.grant.smith;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Circle extends Route{

	double radius;
	Point2D.Double pos;
	boolean canBeStolenFrom = true;
	
	Circle(double x, double y, double radius){
		pos = new Point2D.Double(x, y);
		this.radius = radius;
	}
	
	Circle(Point2D.Double pos, double radius){
		this.pos = pos;
		this.radius = radius;
	}
	
	public void shrink(){
		if(size() > 0)
			radius = farthestNode().distance(pos);
	}
	
	public boolean keepAlive(){
		if(size() == 0)
			return false;
		
		return true;
	}
	
	public Route mergeRoutes(Circle other){
	    double mx = (other.pos.x + pos.x)/2;
	    double my = (other.pos.y + pos.y)/2;
	    pos = new Point2D.Double(mx,my);
		shrink();
		return super.mergeRoutes(other);
	}

	
	public void steal(Circle other){
		if(!other.canBeStolenFrom) return;
		Iterator<Customer> it = other.iterator();
		while(it.hasNext()){
			Customer c = it.next();
			if(c.distance(pos) < c.distance(other.pos)){
				other.demand -= c.c;
				add(c);
				it.remove();
			}
		}
		shrink();
		other.shrink();
	}
	
	public void improve(Point2D.Double depot){		
		List<SavingsNode> savings = new ArrayList<SavingsNode>();
		for(int i = 0; i < size(); i++){
			for(int j = 0; j < size(); j++){
				if(i == j) continue;
				Customer ci = get(i);
				Customer cj = get(j);
				double saving = (depot.distance(ci) + depot.distance(cj)) - ci.distance(cj) ;
				if(saving > 0)
					savings.add(new SavingsNode(ci,cj,saving));
			}
		}
		
		Collections.sort(savings);

		List<Customer> ordered = new LinkedList<Customer>();
		ordered.add(savings.get(0).ci);
		ordered.add(savings.remove(0).cj);

		for(SavingsNode s : savings){
			boolean added = false;
			if(ordered.get(0) == s.ci && !ordered.contains(s.cj)){
				ordered.add(0,s.cj);
				added = true;
			}else if(ordered.get(ordered.size()-1) == s.ci && !ordered.contains(s.cj)){
				ordered.add(s.cj);
				added = true;
			}else if(!added){
				Customer first = ordered.get(0);
				Customer last = ordered.get(ordered.size()-1);
				
				double savingF = (depot.distance(first) + depot.distance(s.cj)) - first.distance(s.cj);
				double savingL = (depot.distance(last) + depot.distance(s.cj)) - last.distance(s.cj);

				if(savingF >= savingL && !ordered.contains(s.cj))
					ordered.add(0,s.cj);
				else if(!ordered.contains(s.cj))
					ordered.add(s.cj);
			}
		}
		clear();
		demand = 0.0f;
		addAll(ordered);
	}
	
	public Customer farthestNode(){
		double far = Double.MIN_VALUE;
		Customer farthest = null;
		for(Customer c : this){
			if(pos.distance(c) > far){
				far = pos.distance(c);
				farthest = c;
			}
		}
		return farthest;
	}
	
}
