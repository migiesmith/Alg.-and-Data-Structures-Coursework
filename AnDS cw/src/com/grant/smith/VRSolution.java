package com.grant.smith;

import java.util.*;
import java.io.*;


public class VRSolution {
	public VRProblem prob;
	public List<Route>soln;
	public VRSolution(VRProblem problem){
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

	//Students should implement another solution
	public void clarkWrightSolution(){
		oneRoutePerCustomerSolution();

		boolean merged = true;

		while(merged){
			merged = false;
			List<SavingsNode> savings = getSavings();
			for (SavingsNode savingsNode : savings) {
				Route route0 = routeWhereCustomerIsLast(savingsNode.ci);
				if(route0 != null){
					Route route2 = routeWhereCustomerIsFirst(savingsNode.cj);
					if(route2 != null){
						if(route0 == route2){ continue;}

						if (route0.routeDemand() + route2.routeDemand() <= prob.depot.c) { // if merge is feasible
							// Merge the two routes
							soln.remove(route0);
							soln.remove(route2);
							soln.add(route0.mergeRoutes(route2));
							merged = true;
						}
					}
				}
			}
		}	
	}

	private Route routeWhereCustomerIsLast(Customer c){
		for(Route r : soln){
			if(r.getEnd() == c) return r;
		}
		return null;
	}

	private Route routeWhereCustomerIsFirst(Customer c){
		for(Route r : soln){
			if(r.getStart() == c) return r;
		}
		return null;
	}

	public List<SavingsNode> getSavings(){
		Iterator<Route> it = soln.iterator();

		List<Customer> customers = new ArrayList<Customer>();
		Route r;
		while(it.hasNext()){
			r = it.next();
			if(r.demand < prob.depot.c){
				customers.add(r.getStart());
				if(r.size() > 1)
					customers.add(r.getEnd());
			}
		}

		List<SavingsNode> savings = new ArrayList<SavingsNode>();
		for(int i = 0; i < customers.size(); i++){
			for(int j = 0; j < customers.size(); j++){
				if(i == j) continue;
				Customer ci = customers.get(i);
				Customer cj = customers.get(j);
				double saving = (prob.depot.distance(ci)+prob.depot.distance(cj)) -ci.distance(cj) ;
				if(saving > 0)
					savings.add(new SavingsNode(ci,cj,saving));
			}
		}
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
