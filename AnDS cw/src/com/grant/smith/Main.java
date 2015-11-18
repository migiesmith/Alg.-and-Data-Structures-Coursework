package com.grant.smith;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class Main {

	static String testDataFolder = "Test Data/";
	static String resultsFolder = "Results/";
	
	public static void main(String[] args) {
		try{

			// Load all files in the test data folder that end with prob.csv and don't contain fail
			Pattern problemFilePattern = Pattern.compile("prob\\.csv");
			
			final File folder = new File(testDataFolder);
			ArrayList<File> files = new ArrayList<File>();
			for (final File f : folder.listFiles()) {
				if(problemFilePattern.matcher(f.getName()).find())
					if(!f.getName().contains("fail"))
						files.add(f);
			}
			// create a list to stores the times of each problem
			int[] times = new int[files.size()];
			
			int fileNo = 0;
			// For each file, run the VRP solver
			for(File f : files){
				// Solution classes:
				//	VRClarkeWrightSolution
				//	VRPartitionSolution
				VRPartitionSolution vrS = new VRPartitionSolution(new VRProblem(testDataFolder + f.getName()));
				
				double loops = 1;
				long startTime = System.nanoTime();
				for(int i = 0; i < loops; i++){
					vrS.solve();
				}
				// store the time taken for the total loops
				times[fileNo] = (int)((System.nanoTime() - startTime)/1000000.0);

				String fileName = f.getName().substring(0, f.getName().length() - "prob.csv".length());
				
				
				// output information about solution to the console
				System.out.println("-------------"+fileName+"-------------");
				System.out.println("Time taken for " + loops + " loops = " + times[fileNo] + "ms");
				System.out.println("Avg time " + (int)(times[fileNo]/loops) + "ms");
				System.out.println("Number of routes = " + vrS.soln.size());
				System.out.println("Cost = " + vrS.solnCost());
				System.out.println("vrS.verify() returned " + vrS.verify());
				System.out.println();
				
				// output the solution
				vrS.writeSVG(resultsFolder +  fileName + "prob.svg", resultsFolder + fileName + "solu.svg");
				vrS.writeOut(resultsFolder + fileName + "solu.csv");
				fileNo++;
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
}

