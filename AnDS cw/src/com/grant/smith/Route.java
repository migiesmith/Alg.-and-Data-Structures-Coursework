package com.grant.smith;

import java.util.ArrayList;


public class Route extends ArrayList<Customer>{

	// stores the demand of this route
	double demand = 0.0f;
	
	// stores the start and end to save time fetching them from the array
	private Customer start;
	private Customer end;
	
	// add a customer to the route
	@Override
	public boolean add(Customer c) {
		// increment the demand by the customer's requirement
		demand += c.c;
		
		// if this list is empty, cache c as the start
		if(size() == 0)
			start = c;
		
		// cache c as the end
		end = c;
		
		// add c to this list
		return super.add(c);
	}
	

	// Add all of the nodes from one circle to this one
	public Route mergeRoutes(Route route2){
		// increment the demand by the other route's requirement
		demand += route2.demand;
		end = route2.end;
		addAll(route2);
		return this;
	}

	// returns the start node
	public Customer getStart(){
		// return the cached start
		return start;
	}
	
	// returns the end node
	public Customer getEnd(){
		// return the cached end
		return end;
	}

	
}
