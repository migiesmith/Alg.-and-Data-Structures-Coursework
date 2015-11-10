package com.grant.smith;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	static String testDataFolder = "Test Data/";
	static String resultsFolder = "Results/";
	
	public static void main(String[] args) {
		
		try{ //VRClarkWrightSolution

			Pattern problemFilePattern = Pattern.compile("prob\\.csv");

			final File folder = new File(testDataFolder);
			ArrayList<File> files = new ArrayList<File>();
			for (final File f : folder.listFiles()) {
				if(problemFilePattern.matcher(f.getName()).find())
					files.add(f);
			}
			int[] times = new int[files.size()];
			
			int fileNo = 0;
			for(File f : files){
				VRPartitionSolution vrS = new VRPartitionSolution(new VRProblem(testDataFolder + f.getName()));
				
				int loops = 100;
				long startTime = System.currentTimeMillis();
				for(int i = 0; i < loops; i++){
					vrS.solve();
				}
				times[fileNo] = (int)(System.currentTimeMillis() - startTime);

				String fileName = f.getName().substring(0, f.getName().length() - "prob.csv".length());
//				System.out.println(fileName);
				
//				System.out.println("Time taken for " + loops + " loops = " + times[fileNo] + "ms");
//				System.out.println("Avg time " + (int)(times[fileNo]/loops) + "ms");
//				System.out.println((int)(times[fileNo]/loops));
				
				vrS.writeSVG(resultsFolder +  fileName + "prob.svg", resultsFolder + fileName + "solu.svg");
				vrS.writeOut(resultsFolder + fileName + "solu.csv");
//				System.out.println(vrS.soln.size());
				System.out.println(vrS.solnCost());
//				System.out.println("vrS.verify() returned " + vrS.verify());
				fileNo++;
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
}

