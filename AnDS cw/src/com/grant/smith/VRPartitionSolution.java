package com.grant.smith;

import java.util.*;
import java.awt.geom.Point2D;
import java.io.*;


public class VRPartitionSolution {
	
	// Stores the information about the problem
	public VRProblem prob;
	// Stores the solution after solve() is ran
	public List<Route> soln;
	// Stores the circles used to create the solution
	ArrayList<Circle> circles;
	
	public VRPartitionSolution(VRProblem problem){
		this.prob = problem;
	}

	//Students should implement another solution
	public void solve(){
		
		// Create a circle that encompasses all nodes
		Circle root = new Circle(prob.depot.x, prob.depot.y, 0.0);
		for(Customer c : prob.customers){
			root.add(c);
		}
		// Shrink the circle so that it just covers all of the nodes within it
		root.shrink();
		
		// Create the list to contain all off the circles
		circles = new ArrayList<Circle>();
		// Add the first circle to the list
		circles.add(root);

		boolean overCap = true;
		while(overCap){
			overCap = false;
			for(int i = 0; i < circles.size(); i++){
				// Get the circle for this iteration
				Circle c = circles.get(i);
				// Check if this circle should be kept (if is contains any nodes)
				if(!c.keepAlive()) { circles.remove(i); continue;} // If this circle shouldn't be kept, remove it
				if(c.demand > prob.depot.c){
					// Get the farthest node in this circle
					Customer farthest = c.farthestNode();

					// Create a new circle between that node and this circle's centre
				    double mx = (farthest.x + c.pos.x)/2;
				    double my = (farthest.y + c.pos.y)/2;
					Circle newC = new Circle( new Point2D.Double(mx,my), c.radius/2);

					// Loop through all the circles and try to steal nodes
					for(Circle others : circles){
						newC.steal(others);
						if(others.demand <= prob.depot.c)
							others.canBeStolenFrom = false;
					}
					
					// Add the new circle to the list
					circles.add(newC);
					
					// Check if we need to keep looping the 'while'
					if(c.demand > prob.depot.c || newC.demand > prob.depot.c)
						overCap = true;
					
				}

			}

			// Merge all circles if they cross over and merging them wouldn't put them over capacity
			boolean merged = true;
			while(merged){ // If there was a merge, check again to see if there are any more
				merged = false;
				// Loop through every circle for each circle
				for(int i = 0; i < circles.size()-1; i++){
					for(int j = i+1; j < circles.size(); j++){ // Don't loop through circles that have already checked this circle
						// Get circle ci and circle cj						
						Circle ci = circles.get(i);
						Circle cj = circles.get(j);
						// Check if they should be merged
						if((ci.demand + cj.demand <= prob.depot.c) && (ci.pos.distance(cj.pos) <= ci.radius*2 + cj.radius*2)){
							// merge the circles
							circles.remove(cj);
							ci.mergeRoutes(cj);
							// Shrink the circle so that it just covers all of the nodes within it
							ci.shrink();
							// state that there was a merge
							merged = true;
						}
					}
				}
			}
			
			// Resize and reposition all of the circles depending on the farthest node within them and their position
			for(int i = 0; i < circles.size(); i++){
				Circle c = circles.get(i);
				Customer farthest = c.farthestNode();
				if(farthest == null){ circles.remove(i); continue;}
				// Get the midpoint between the circle's position and the farthest node's position
			    double mx = (farthest.x + c.pos.x)/2;
			    double my = (farthest.y + c.pos.y)/2;
			    // Set the circle's position to the calculated midpoint
				c.pos.setLocation(mx, my);
				// Shrink the circle so that it just covers all of the nodes within it
				c.shrink();
				
			}
		}

		
		for(Circle c : circles){
			if(c.size() > 1){
				c.improve(prob.depot);
			}
		}
		
		soln = new LinkedList<Route>();
		
		soln.addAll(circles);
		
		//Remove the circles so that they don't render
		circles.clear();
		
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
		for(Circle c : this.circles){
			String disk = String.format(
					"<g transform='translate(%.0f,%.0f)'>"+
							"<circle cx='0' cy='0' r='%d' fill='none' stroke='black' stroke-width='1'/>" +
							"<text text-anchor='middle' y='5'>%s</text>"+
							"</g>\n", 
							c.pos.x, c.pos.y, (int)c.radius, "");
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
