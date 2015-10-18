package com.grant.smith;

public class Main {

	public static void main(String[] args) {
		
		try{
			VRSolution vrS = new VRSolution(new VRProblem("Test Data/rand01000prob.csv"));
			long total = 0;
			int loops = 1;
			for(int i = 0; i < loops; i++){
				long startTime = System.currentTimeMillis();
				vrS.testSolution();
				total += System.currentTimeMillis() - startTime;
			}
			System.out.println("Time Taken = " + (total/loops));
			vrS.writeSVG("TESTPROB.csv", "TESTSOL.svg");
			System.out.println(vrS.soln.size());
			System.out.println(vrS.solnCost());
			System.out.println("Verify: " + vrS.verify());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
