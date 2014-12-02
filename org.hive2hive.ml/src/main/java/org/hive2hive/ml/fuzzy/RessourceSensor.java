package org.hive2hive.ml.fuzzy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RessourceSensor {
	
	public static Map<String, ArrayList<Double>> cpuSensor = new HashMap<String, ArrayList<Double>>();
	public static Map<String, ArrayList<Double>> memorySensor = new HashMap<String, ArrayList<Double>>();
	public static Map<String, ArrayList<Double>> networkSensor = new HashMap<String, ArrayList<Double>>();
	public static ArrayList<String> Nodes = new ArrayList<String>(); 
	private final static String CPU = "cpu"; 
	private final static String Memory = "memory";
	private final static String Network = "network";
	
	
	
	public static void initialize() {
		
		Nodes = readNodes();
		cpuSensor = readValues(CPU); 
		memorySensor = readValues(Memory); 
		networkSensor = readValues(Network); 
			
	}
	
	
	@SuppressWarnings("resource")
	private static HashMap<String, ArrayList<Double>> readValues(String component) {
		 Map<String, ArrayList<Double>> map = new HashMap<String, ArrayList<Double>>();
		ArrayList<Double> values = new ArrayList<Double>();
		String name = null; 
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(System.getProperty("user.home") + "/data/files.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			while((name = file.readLine()) != null){
				
				values = constructMap(component, name); 
				map.put(name, values); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return (HashMap<String, ArrayList<Double>>) map; 
	}
	
	
	@SuppressWarnings("resource")
	private static ArrayList<Double> constructMap(String component, String name) {
		String line = null; 
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(System.getProperty("user.home") + "/data/"+ component +"/" + name + ".txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Double> values = new ArrayList<Double>();
		try {
			while((line = file.readLine()) != null){
			
				  values.add(Double.parseDouble(line));
				}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return values; 	
		
	}

	
	@SuppressWarnings("resource")
	private static ArrayList<String> readNodes() {
		ArrayList<String> values = new ArrayList<String>();
		BufferedReader file = null;
		String name = null;
		try {
			file = new BufferedReader(new FileReader(System.getProperty("user.home") + "/data/files.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			while((name = file.readLine()) != null){
				
				  values.add(name);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return values; 
	}

}
