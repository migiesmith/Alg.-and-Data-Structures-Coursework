package com.grant.smith;

public class Main {

	public static void main(String[] args) {
		
		try{
			VRSolution vrS = new VRSolution(new VRProblem("Test Data/rand00030prob.csv"));
			long total = 0;
			int loops = 1;
			for(int i = 0; i < loops; i++){
				long startTime = System.currentTimeMillis();
				vrS.clarkWrightSolution();
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
