package org.hive2hive.ml.strategy;

public class Pair {
	
	
	
	    private final String folder;
	    private final String destination;
	    private final boolean statement; 

	    public Pair(String aFolder, String aDestination, boolean aStatement)
	    {
	        folder   = aFolder;
	        destination = aDestination;
	        statement = aStatement; 
	    }

	    public String getFolder()   { return folder; }
	    public String getDestination() { return destination; }
	    public boolean getStatement() { return statement; }
	}


