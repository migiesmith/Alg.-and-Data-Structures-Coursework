package com.grant.smith;

import java.util.*;
import java.io.*;


public class VRClarkeWrightSolution {
	
	// Stores the information about the problem
	public VRProblem prob;
	// Stores the solution after solve() is ran
	public List<Route> soln;
	
	public VRClarkeWrightSolution(VRProblem problem){
		this.prob = problem;
	}

	//The dumb solver adds one route per customer
	public void oneRoutePerCustomerSolution(){
		this.soln = new ArrayList<Route>();
		for(Customer c:prob.customers){
			Route route = new Route();
			route.add(c);
			soln.add(route);
		}
	}

	// Clarke Wright solver function
	public void solve(){
		// Create a route for each customer
		oneRoutePerCustomerSolution();
		
		// Calculate the savings list for the problem
		List<SavingsNode> savings = getSavings();
		
		// Loop through each saving
		for (SavingsNode savingsNode : savings) {
			// Get the route where ci is the first customer
			Route route0 = routeWhereCustomerIsLast(savingsNode.ci);
			if(route0 != null){ // check if we found a route for route0
				// Get the route where cj is the first customer
				Route route1 = routeWhereCustomerIsFirst(savingsNode.cj);
				if(route1 != null){ // check if we found a route for route1
					if(route0 == route1){ continue;} // if route0 and route1 are the same, do nothing
					if (route0.demand + route1.demand <= prob.depot.c) { // if merge is feasible
						// Merge the two routes
						soln.remove(route1);
						route0.mergeRoutes(route1);
					}
				}
			}
		}

	}

	// returns a route where c is the last element in a route
	private Route routeWhereCustomerIsLast(Customer c){
		for(Route r : soln){
			if(r.getEnd() == c) return r;
		}
		return null;
	}

	// returns a route where c is the first element in a route
	private Route routeWhereCustomerIsFirst(Customer c){
		for(Route r : soln){
			if(r.getStart() == c) return r;
		}
		return null;
	}

	// Calculates the savings list
	public List<SavingsNode> getSavings(){
		// Create a list of customers for the savings list to use
		List<Customer> customers = new ArrayList<Customer>();
		// loop through every route
		for(Route r : soln){
			if(r.demand < prob.depot.c){
				// add the start of the route to the list
				customers.add(r.getStart());
				// if the route is larger than just 1 element, add the end as well
				if(r.size() > 1)
					customers.add(r.getEnd());
			}
		}
		
		// Create the savings list
		List<SavingsNode> savings = new ArrayList<SavingsNode>();
		// Loop through each node and add the relative savings to the savings list if there is a saving to be made
		for(int i = 0; i < customers.size(); i++){
			for(int j = i; j < customers.size(); j++){
				if(i == j) continue;
				Customer ci = customers.get(i);
				Customer cj = customers.get(j);
				double saving = (prob.depot.distance(ci) + prob.depot.distance(cj)) - ci.distance(cj) ;
				if(saving > 0)
					savings.add(new SavingsNode(ci,cj,saving));
			}
		}
		// Sort the list into descending order
		Collections.sort(savings);
		return savings;
	}

	//Calculate the total journey
	public double solnCost(){
		double cost = 0;
		for(List<Customer>route:soln){
			Customer prev = this.prob.depot;
			for (Customer c:route){
				cost += prev.distance(c);
				prev = c;
			}
			//Add the cost of returning to the depot
			cost += prev.distance(this.prob.depot);
		}
		return cost;
	}
	
	public Boolean verify(){
		//Check that no route exceeds capacity
		Boolean okSoFar = true;
		for(Route route : soln){
			//Start the spare capacity at
			int total = 0;
			for(Customer c:route)
				total += c.c;
			if (total>prob.depot.c){
				System.out.printf("********FAIL Route starting %s is over capacity %d\n",
						route.get(0),
						total
						);
				okSoFar = false;
			}
		}
		//Check that we keep the customer satisfied
		//Check that every customer is visited and the correct amount is picked up
		Map<String,Integer> reqd = new HashMap<String,Integer>();
		for(Customer c:this.prob.customers){
			String address = String.format("%fx%f", c.x,c.y);
			reqd.put(address, c.c);
		}
		for(Route route:this.soln){
			for(Customer c:route){
				String address = String.format("%fx%f", c.x,c.y);
				if (reqd.containsKey(address))
					reqd.put(address, reqd.get(address)-c.c);
				else
					System.out.printf("********FAIL no customer at %s\n",address);
			}
		}
		for(String address:reqd.keySet())
			if (reqd.get(address)!=0){
				System.out.printf("********FAIL Customer at %s has %d left over\n",address,reqd.get(address));
				okSoFar = false;
			}
		return okSoFar;
	}

	public void readIn(String filename) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String s;
		this.soln = new ArrayList<Route>();
		while((s=br.readLine())!=null){
			Route route = new Route();
			String [] xycTriple = s.split(",");
			for(int i=0;i<xycTriple.length;i+=3)
				route.add(new Customer(
						(int)Double.parseDouble(xycTriple[i]),
						(int)Double.parseDouble(xycTriple[i+1]),
						(int)Double.parseDouble(xycTriple[i+2])));
			soln.add(route);
		}
		br.close();
	}

	public void writeSVG(String probFilename,String solnFilename) throws Exception{
		String[] colors = "chocolate cornflowerblue crimson cyan darkblue darkcyan darkgoldenrod".split(" ");
		int colIndex = 0;
		String hdr = 
				"<?xml version='1.0'?>\n"+
						"<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' '../../svg11-flat.dtd'>\n"+
						"<svg width='8cm' height='8cm' viewBox='0 0 500 500' xmlns='http://www.w3.org/2000/svg' version='1.1'>\n";
		String ftr = "</svg>";
		StringBuffer psb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		psb.append(hdr);
		ssb.append(hdr);
		for(List<Customer> route:this.soln){
			ssb.append(String.format("<path d='M%s %s ",this.prob.depot.x,this.prob.depot.y));
			for(Customer c:route)
				ssb.append(String.format("L%s %s",c.x,c.y));
			ssb.append(String.format("z' stroke='%s' fill='none' stroke-width='2'/>\n",
					colors[colIndex++ % colors.length]));
		}
		for(Customer c:this.prob.customers){
			String disk = String.format(
					"<g transform='translate(%.0f,%.0f)'>"+
							"<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>" +
							"<text text-anchor='middle' y='5'>%d</text>"+
							"</g>\n", 
							c.x,c.y,10,c.c);
			psb.append(disk);
			ssb.append(disk);
		}
		String disk = String.format("<g transform='translate(%.0f,%.0f)'>"+
				"<circle cx='0' cy='0' r='%d' fill='pink' stroke='black' stroke-width='1'/>" +
				"<text text-anchor='middle' y='5'>%s</text>"+
				"</g>\n", this.prob.depot.x,this.prob.depot.y,20,"D");
		psb.append(disk);
		ssb.append(disk);
		psb.append(ftr);
		ssb.append(ftr);
		PrintStream ppw = new PrintStream(new FileOutputStream(probFilename));
		PrintStream spw = new PrintStream(new FileOutputStream(solnFilename));
		ppw.append(psb);
		spw.append(ssb);
		ppw.close();
		spw.close();
	}
	public void writeOut(String filename) throws Exception{
		PrintStream ps = new PrintStream(filename);
		for(List<Customer> route:this.soln){
			boolean firstOne = true;
			for(Customer c:route){
				if (!firstOne)
					ps.print(",");
				firstOne = false;
				ps.printf("%f,%f,%d",c.x,c.y,c.c);
			}
			ps.println();
		}
		ps.close();
	}
}
