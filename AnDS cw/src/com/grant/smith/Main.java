package com.grant.smith;

import java.text.DecimalFormat;


public class Main {
    
	public static void main(String[] args) {
		
		try{ //VRClarkWrightSolution
			VRPartitionSolution vrS = new VRPartitionSolution(new VRProblem("Test Data/rand00200prob.csv"));
			double total = 0;
			int loops = 1;
			long startTime = System.nanoTime();
			for(int i = 0; i < loops; i++){
				vrS.solve();
			}
			total = (System.nanoTime() - startTime)/1000000.0;
			DecimalFormat df = new DecimalFormat("#.###");
			System.out.println("Time taken for " + loops + " loops = " + df.format(total) + "ms");
			System.out.println("Average time taken = " + df.format(total/loops) + "ms");
			vrS.writeSVG("rand01000prob.svg", "rand01000solu.svg");
			vrS.writeOut("rand01000solu.csv");
			System.out.println(vrS.soln.size());
			System.out.println(vrS.solnCost());
			System.out.println("vrS.verify() returned " + vrS.verify());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
}

