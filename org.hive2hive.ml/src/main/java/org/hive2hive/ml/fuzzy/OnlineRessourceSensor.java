package org.hive2hive.ml.fuzzy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class OnlineRessourceSensor {
	
	public static HashMap<String, String> hm = new HashMap<String, String>();
	
	
	public static void initalize () {
		
	    hm.put("ch", "planetlab3.inf.ethz.ch");
	    hm.put("de", "iraplab1.iralab.uni-karlsruhe.de");
	    hm.put("se", "planetlab-1.ssvl.kth.se");
	    hm.put("pl", "planetlab3.mini.pw.edu.pl");
	    hm.put("uk", "planetlab-4.imperial.ac.uk"); 
	    	
	}
	
	
	
	public static double getCPU(String source)  {
		URL url = null;
		double cpu = 0.0; 
		try {
			url = new URL("http://"+ source +":5000/cpu");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		
		try {
			doc = Jsoup.parse(url, 3*1000);
		} catch (IOException e) {

			try {
			    Thread.sleep(5 * 1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			try {
				doc = Jsoup.parse(url, 3*1000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		String cpuRaw = doc.body().text();
		cpu = Double.parseDouble(prepString(cpuRaw)); 
		return cpu; 
	}
	
	
	public static double getMem(String source) {
		URL url = null;
		double mem = 0.0; 
		try {
			url = new URL("http://"+ source +":5000/mem");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = Jsoup.parse(url, 3*1000);
		} catch (IOException e) {

			try {
			    Thread.sleep(5 * 1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			try {
				doc = Jsoup.parse(url, 3*1000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		String memRaw = doc.body().text();
		mem = Double.parseDouble(prepString(memRaw.substring(30, memRaw.length()))); 
		
		return mem; 
	}
	
	
	public static double getNetwork(String source)  {
		URL url = null;
		Document doc = null;
		String networkRaw = ""; 
		double throughput = 0, rx1 = 0, rx2 = 0, tx1 = 0, tx2 = 0; 
		
		try {
			url = new URL("http://"+ source +":5000/network");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			doc = Jsoup.parse(url, 3*1000);
		} catch (IOException e) {

			try {
			    Thread.sleep(5 * 1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			try {
				doc = Jsoup.parse(url, 3*1000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		networkRaw = doc.body().text();
		if(networkRaw.length() > 9) {
			rx1  = Double.parseDouble(prepNetworkString(networkRaw.substring(9, networkRaw.length()-1))[0]);
			tx1  = Double.parseDouble(prepNetworkString(networkRaw.substring(9, networkRaw.length()-1))[1]); 	
		}
	
		
		
		try {
		    Thread.sleep(5 * 1000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		
		
		try {
			doc = Jsoup.parse(url, 3*1000);
		} catch (IOException e)  {

			try {
			    Thread.sleep(5 * 1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			try {
				doc = Jsoup.parse(url, 3*1000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		networkRaw = doc.body().text();
		if(networkRaw.length() > 9) {
			rx2  = Double.parseDouble(prepNetworkString(networkRaw.substring(9, networkRaw.length()-1))[0]);
			tx2  = Double.parseDouble(prepNetworkString(networkRaw.substring(9, networkRaw.length()-1))[1]); 
		}
		
		throughput = ( (rx2 - rx1) + (tx2-tx1) ) / (5 * 1000);
		
		return throughput;
	}
	
	public static String prepString (String line) {
		int i = 0; 
		String subString = "";
		
		if(line.charAt(0) == 'l' )
			line = line.substring(14, line.length());
		
		while(line.charAt(i) != ' '){
			subString = subString + line.charAt(i); 
			i++; 
		}
			
		return subString;
	}
	
	
	
	
	public static String[] prepNetworkString (String line) {
		
		String substring1 = "";
		String substring2 = ""; 
		String [] aString = new String[2]; 
		int i = 0; 
		while(line.charAt(i) != '('){
			
				substring1 = substring1  + line.charAt(i); 
				i++; 
		}
				while(line.charAt(i) != ':') {
					i++; 
					
				}
			i++; 
			
			while(line.charAt(i) != '('){
				substring2 = substring2 + line.charAt(i); 
				i++; 
			}
			
		
		
		aString[0] = substring1;
		aString[1] = substring2; 
		
		return aString; 
		
	}
	
	
	

}
