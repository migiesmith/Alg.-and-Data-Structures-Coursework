package com.grant.smith;

import java.util.ArrayList;

public class Main {
    
	public static void main(String[] args) {
		
		try{
			VRPartitionSolution vrS = new VRPartitionSolution(new VRProblem("Test Data/rand00700prob.csv"));
			long total = 0;
			int loops = 1;
			for(int i = 0; i < loops; i++){
				long startTime = System.currentTimeMillis();
				vrS.solve();
				total += System.currentTimeMillis() - startTime;
			}
			System.out.println("Time Taken = " + (total/loops) + "ms");
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

