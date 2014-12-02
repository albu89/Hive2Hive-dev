package org.hive2hive.ml.fuzzy;

import java.util.Random;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class Evaluate {

	final static int  bound = 10; 
	



	
	public static double heuristic(String country) {
		
		double cpu = 0.0;
		double memory = 0.0; 
		double network = 0.0; 
		int randomNum = 0; 
		
		String filename = "/Users/alexanderbusser/Documents/uniYearFive/semesterProject"+"/ressources.fcl";
		FIS fis = FIS.load(filename, true);

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		// Get default function block
		FunctionBlock fb = fis.getFunctionBlock(null);
		
		RessourceSensor.initialize();
		randomNum = randInt(); 
		cpu = RessourceSensor.cpuSensor.get(country).get(randomNum);
		memory = RessourceSensor.memorySensor.get(country).get(randomNum);
		network = RessourceSensor.networkSensor.get(country).get(randomNum);
		
		
		// Set inputs
		fb.setVariable("cpu", cpu);
		fb.setVariable("memory", memory);
		fb.setVariable("network", network);

		
		
		// Evaluate
		fb.evaluate();
		
		System.out.println(fb.getVariable("quality").getValue());
		
		return fb.getVariable("quality").getValue();
		
			
	}
	
	public static int randInt() {
		int min = 1; 
		int max = 2000; 
	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

}
