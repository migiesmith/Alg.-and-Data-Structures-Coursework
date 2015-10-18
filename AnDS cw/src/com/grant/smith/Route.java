package com.grant.smith;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Route extends ArrayList<Customer>{

	double demand = 0.0f;
	
	@Override
	public boolean add(Customer c) {
		demand += c.c;
		return super.add(c);
	}
	
	public Route mergeRoutes(Route route2){
		demand += route2.demand;
		addAll(route2);
		return this;
	}

	public Customer getStart(){
		return get(0);
	}
	public Customer getEnd(){
		return get(size()-1);
	}

	public double routeDemand(){
		return demand;
	}
	
}
