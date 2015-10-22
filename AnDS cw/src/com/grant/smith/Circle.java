package com.grant.smith;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
			radius = farthestNode().distance(pos) + 10.0;
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
	
	/*
	public void improveOLD(Point2D.Double depot){
		Route orderedList = new Route();

	    double nearestDist=Double.POSITIVE_INFINITY;
	    int closestIndex = 0;
		for(int i = 0; i < size(); i++){
	        double dist = get(i).distance(depot);
	        if(dist < nearestDist){
	        	nearestDist = dist;
	        	closestIndex = i;
	        }
			
		}
		
		orderedList.add(remove(closestIndex)); //Arbitrary starting point
		
		
		nearestDist=Double.POSITIVE_INFINITY;
	    closestIndex = 0;
		for(int i = 0; i < size(); i++){
	        double dist = get(i).distance(depot);
	        if(dist < nearestDist){
	        	nearestDist = dist;
	        	closestIndex = i;
	        }
			
		}
		Customer end = remove(closestIndex);
		
		while (size() > 0) {
		   //Find the index of the closest point (using another method)
		   int nearestIndex=findNearestIndex(orderedList.get(orderedList.size()-1));

		   //Remove from the unorderedList and add to the ordered one
		   orderedList.add(remove(nearestIndex));
		}
		addAll(orderedList);
		add(end);
	}
	
	private int findNearestIndex (Point2D.Double currentPoint) {
	    double nearestDist=Double.POSITIVE_INFINITY;
	    int nearestIndex = 0;
	    for (int i=0; i< size(); i++) {
	    	Point2D.Double point2= get(i);
	        double dist = point2.distance(currentPoint);
	        if(dist < nearestDist) {
	        	nearestDist = dist;
	            nearestIndex=i;
	        }
	    }
	    return nearestIndex;
	}
	*/
	
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

		List<Customer> ordered = new ArrayList<Customer>();
		ordered.add(savings.get(0).ci);

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
				
				double savingF = (depot.distance(first) + depot.distance(s.ci)) - first.distance(s.ci);
				double savingL = (depot.distance(last) + depot.distance(s.ci)) - last.distance(s.ci);

				if(savingF > savingL && !ordered.contains(s.ci))
					ordered.add(0,s.ci);
				else if(!ordered.contains(s.ci))
					ordered.add(s.ci);
			}
		}
		clear();
		demand = 0.0f;
		addAll(ordered);
	}
	
	public Customer farthestNode(){
		double far = -10000.0;
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
