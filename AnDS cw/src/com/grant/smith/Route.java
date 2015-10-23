package com.grant.smith;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Route extends ArrayList<Customer>{

	double demand = 0.0f;
	private Customer start;
	private Customer end;
	
	@Override
	public boolean add(Customer c) {
		demand += c.c;
		if(size() == 0)
			start = c;
		
		end = c;
		
		return super.add(c);
	}
	
	
	public Route mergeRoutes(Route route2){
		demand += route2.demand;
		end = route2.end;
		addAll(route2);
		return this;
	}

	public Customer getStart(){
		return start;
	}
	public Customer getEnd(){
		return end;
	}

	
}
