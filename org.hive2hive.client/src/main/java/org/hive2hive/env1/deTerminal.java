package org.hive2hive.env1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import org.apache.commons.io.FileUtils;
import org.hive2hive.client.console.ConsoleMenu;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.api.configs.*;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.hive2hive.ml.fuzzy.*;


public class deTerminal {
	
	
	//Define username
	private static final String name = "de";
	//Define network to join
	private static final String core = "localhost";
	//Initalize node 
	IH2HNode node;
	//Define folders
	private static final String rootFolder =  "H2H" + name;
	private static final Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);	
	private static final String folderPath = System.getProperty("user.home")  + "/" + rootFolder;
	private static final File sharedFolder = new File(folderPath +  "/shared");
	private static final String sharedFileName = "/picture.jpg";
	private static final String headerFilename = "/relay.txt"; 
	private static final File sharedFile = new File(sharedFolder, sharedFileName);
	private static final File fileTemp = new File(sharedFolder,headerFilename );
	private static IFileManager fileManager; 
	private static final String CH = "ch", DE = "de", SE = "se", PL = "pl", UK = "uk"; 
	
	
	
	
	public static void main(String[] args) throws Exception  {
		new deTerminal().start();
	}
	
	//Automatically executes connect network and user login
	public void start() throws Exception  {
		
		//Connect network
		createPeer();
		//Register and login user
		userManagement();
		menu();
	
	}
	
	//Connect Network 
	public  void createPeer() throws UnknownHostException {
		
		//uncomment if soruce
		//INetworkConfiguration masterConfig = NetworkConfiguration.create("masterID");
		
		//uncomment if client
		INetworkConfiguration nodeConfig = NetworkConfiguration.create("nodeID", InetAddress.getByName(core));
	
		BigInteger val1 = new BigInteger("10485760");
		BigInteger val2 = new BigInteger("209715200");
		IFileConfiguration customFileConf = FileConfiguration.createCustom(val1, 20, val2, 1024*1024);
		
		
		//uncomment if source
		//node = H2HNode.createNode(masterConfig , customFileConf); 
		
		//uncomment if client
		node = H2HNode.createNode(nodeConfig, customFileConf);
		node.connect();	
	}
	
	//Register and login user
	public void userManagement () throws NoPeerConnectionException, InterruptedException, IOException {
		
		IUserManager userManager = node.getUserManager(); 
		UserCredentials credentials = new UserCredentials(name, name, name);
		
		//Register user
		if (!userManager.isRegistered(credentials.getUserId())) {
		    userManager.register(credentials).await();
		}
		
		//Login user
		userManager.login(credentials, rootDirectory).await();
		
		//Create root folder
		File rootDir = new File(FileUtils.getUserDirectory(), rootFolder);
		FileUtils.forceMkdir(rootDir);
		
		//Copy file into hive2hive workspace
		String command = "cp -r "+ System.getProperty("user.home") + "/shared" + " " + System.getProperty("user.home")+"/H2H"+name;
		executeCommand(command);
		
				
	}
	
	
	//Prototype of the intelligent forwarding......... not active at the moment
	public void intelligentRouting (String folder) throws NoSessionException, NoPeerConnectionException, IllegalFileLocation, InterruptedException, InvalidProcessStateException, FileNotFoundException, UnsupportedEncodingException {
		
		//static variable has to be changed 
		 String destination = null; 
		 String destinationMax = null; 
		 double max = 0, temp = 0; 
		 boolean relay = false; 
		
		if(Evaluate.heuristic(CH) > 0.3){
			System.out.println("Send to EPFL"); 
			destination = CH; 
		}
			
		else {
			System.out.println("Send to intermediate");
			RessourceSensor.initialize();
			
			for (Iterator<String> iter = RessourceSensor.Nodes.iterator(); iter.hasNext(); ) {
				destination = iter.next(); 
				temp = Evaluate.heuristic(destination);
				
				if(temp > max) {
					destinationMax = destination; 
					max = temp; 
				}
				
			}
			relay = true; 
			destination = destinationMax; 
		}
			
			//create flag with intermediate destination
			//implement write to file 
			if (relay){
				PrintWriter writer = new PrintWriter(folderPath + "/" + folder + "/relay.txt", "UTF-8");
				writer.println(CH);
				writer.println(folder);
				writer.close();		
				addFolder(relay);
				}
			else
				addFolder(relay); 
			//use heuristic to choose best intermediary connection	
			try {
				fileManager.share(sharedFolder, destination, PermissionType.WRITE).start().await();
			} catch (IllegalFileLocation | NoSessionException
					| NoPeerConnectionException | InterruptedException
					| InvalidProcessStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		
	}
	

	//Share a specific folder
	public void shareFile(String city) throws NoSessionException, NoPeerConnectionException, IllegalFileLocation, InterruptedException, InvalidProcessStateException {	
		fileManager = node.getFileManager();
		ConsoleMenu.print(String.format("Executing '%s' with '%s'...", "Share file", city));
		fileManager.share(sharedFolder, city, PermissionType.WRITE).start().await();

	}
	
	
	//Add folders to the DHT
	public void addFolder(boolean flag) throws NoSessionException, NoPeerConnectionException, IllegalFileLocation, InterruptedException, InvalidProcessStateException {
		
		//Initialize file manager
		fileManager = node.getFileManager();
		fileManager.configureAutostart(false);
		
		//Add Folder and File 
		ConsoleMenu.print(String.format("Executing '%s'...", "add file"));
		node.getFileManager().add(sharedFolder).start().await();
		node.getFileManager().add(sharedFile).start().await();
		
		if(flag)
			node.getFileManager().add(fileTemp).start().await(); 
	
		
	}
	
	//GUI
	public void printMenu () {
		
		ConsoleMenu.print("1.) List registered Nodes\n "
				+ "2.) Add File \n"
				+ "3.) Upload File to EPFL \n "
				+ "4.) Exit");
			}
	
	
	//Menu
	public void menu() throws Exception {
		 
		while(true){
			BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
			printMenu();
			ConsoleMenu.print("Enter an option: ");
			String input = br.readLine();	
			if (input.equals("1")){		
				
				if(node.getUserManager().isRegistered(CH)){
					ConsoleMenu.print(CH + " is registered");
					}
				if(node.getUserManager().isRegistered(DE)){
					ConsoleMenu.print(DE + " is registered");
					}
			
				if(node.getUserManager().isRegistered(SE)){
					ConsoleMenu.print(SE + " is registered");
					}
			
				if(node.getUserManager().isRegistered(UK)){
					ConsoleMenu.print(UK + " is registered");
					}
			
				if(node.getUserManager().isRegistered(PL)){
					ConsoleMenu.print(PL + " is registered");	
				}			
			}
		
		if(input.equals("2"))
			addFolder(false);
		
		if(input.equals("3")) {
			br = new BufferedReader( new InputStreamReader(System.in));
			ConsoleMenu.print("Enter which folder you would like to upload to EPFL: ");
			String folder = br.readLine(); 
			intelligentRouting(folder);
			}
		if(input.equals("4"))
			System.exit(0); 
		}
	}
	
	
	//Helper function to execute comand on Unix environment
	private String executeCommand(String command) {
		 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}

		

}
