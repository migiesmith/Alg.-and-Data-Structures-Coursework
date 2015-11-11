package com.grant.smith;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Circle extends Route{

	// stores the radius of this circle
	double radius;
	// stores the position of this circle
	Point2D.Double pos;
	// stores the stealable state (whether another circle can steal from it or not)
	boolean canBeStolenFrom = true;
	
	
	// Constructor for a circle
	Circle(double x, double y, double radius){
		this(new Point2D.Double(x,y), radius);
	}

	// Constructor for a circle
	Circle(Point2D.Double pos, double radius){
		this.pos = pos;
		this.radius = radius;
	}

	// Shrink the circle so that it just covers all of the nodes within it
	public void shrink(){
		if(size() > 0)
			radius = farthestNode().distance(pos);
	}
	
	// Should this circle be kept (false if it has no children)
	public boolean keepAlive(){
		if(size() == 0)
			return false;
		
		return true;
	}
	
	// Add all of the nodes from one circle to this one
	public void mergeRoutes(Circle other){
		super.mergeRoutes(other);
		// Get the midpoint between the circle's position and the farthest node's position
	    double mx = (other.pos.x + pos.x)/2;
	    double my = (other.pos.y + pos.y)/2;
	    // Set the circle's position to the calculated midpoint
		pos.setLocation(mx, my);
		// Shrink this circle so that it just covers all of the nodes within it
		shrink();
	}

	// Attempt to steal nodes from another circle until full or out of nodes to steal
	public void steal(Circle other){
		if(!other.canBeStolenFrom) return; // If the other doesn't allow stealing, return
		Iterator<Customer> it = other.iterator();
		// For each node in the other circle
		while(it.hasNext()){
			Customer c = it.next();
			// Check which circle the node is closer to
			if(c.distance(pos) < c.distance(other.pos)){
				// If the node is closer to this circle then steal it
				other.demand -= c.c;
				add(c);
				it.remove();
			}
		}
		// Shrink this circle so that it just covers all of the nodes within it
		shrink();
		// Shrink the other circle so that it just covers all of the nodes within it
		other.shrink();
	}
	
	// Improves the pathing between the nodes contained withing this circle
	public void improve(Point2D.Double depot){		
		// Create a list of savings
		List<SavingsNode> savings = new ArrayList<SavingsNode>();
		// Loop through each node and add the relative savings to the savings list if there is a saving to be made
		for(int i = 0; i < size(); i++){
			for(int j = 0; j < size(); j++){
				if(i == j) continue;
				Customer ci = get(i);
				Customer cj = get(j);
				double saving = depot.distance(ci) + depot.distance(cj) - ci.distance(cj);
				if(saving > 0)
					savings.add(new SavingsNode(ci,cj,saving));
			}
		}
		
		// Sort the list into descending order
		Collections.sort(savings);

		// Create an ordered list
		List<Customer> ordered = new LinkedList<Customer>();
		// Add the highest saving nodes and remove them from the savings list
		ordered.add(savings.get(0).ci);
		ordered.add(savings.remove(0).cj);

		// Loop through the savings list
		for(SavingsNode s : savings){
			// if the first ordered node is ci and ordered doesn't contain cj then add cj
			if(ordered.get(0) == s.ci && !ordered.contains(s.cj)){
				ordered.add(0,s.cj);
			// else if the lat ordered node is ci and ordered doesn't contain cj then add cj
			}else if(ordered.get(ordered.size()-1) == s.ci && !ordered.contains(s.cj)){
				ordered.add(s.cj);
			// else add cj to the side that has the largest saving
			}else{
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
		// clear this list
		clear();
		// set the demand to 0
		demand = 0.0f;
		// add all of the ordered nodes to this list
		addAll(ordered);
	}
	
	// Return the farthest node from the center of this circle that this circle owns
	public Customer farthestNode(){
		// Set far to a very low number
		double far = Double.MIN_VALUE;
		Customer farthest = null;
		// Loop through each customer
		for(Customer c : this){
			// If the distance to c is larger than far then set the farthest to c
			if(pos.distance(c) > far){
				far = pos.distance(c);
				farthest = c;
			}
		}
		// return the farthest node
		return farthest;
	}
	
}
