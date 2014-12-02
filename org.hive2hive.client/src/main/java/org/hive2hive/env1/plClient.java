package org.hive2hive.env1;



import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.io.IOException;
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
import org.hive2hive.ml.helper.Pair;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

import java.nio.file.Path;
import java.nio.file.Paths;




public class plClient {

	//Define username
	private static final String name = "pl";
	//Define network to join
	private static final String core = "localhost";
	//Initalize node 
	IH2HNode node;
	//Define folders
	private static final String rootFolder =  "H2H" + name;
	private static Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);	
	private static final String folderPath = System.getProperty("user.home")  + "/" + rootFolder;

	public static void main(String[] args) throws Exception  {
		new plClient().start();
	}
	
	public void start() throws Exception  {
		createPeer();
		userManagement();
		relayWatch();
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
		
		if (!userManager.isRegistered(credentials.getUserId())) {
		    userManager.register(credentials).await();
		}
		userManager.login(credentials, rootDirectory).await();
		
		//create root folder
		File rootDir = new File(FileUtils.getUserDirectory(), rootFolder);
		FileUtils.forceMkdir(rootDir);
			
	}
	
	
	//Share a specific relay folder
	public void fileManager(String destination, String folder)  {
	
		ConsoleMenu.print(String.format("Executing share '%s'...", folder));

		File folderShared = new File(folderPath +  "/" + folder);
		
		node.getFileManager().configureAutostart(false);
		
		try {
			//Add file to DHT
			node.getFileManager().add(folderShared).start().await();
			//Share File
			node.getFileManager().share(folderShared, destination, PermissionType.WRITE).start().await();
		} catch (NoSessionException | NoPeerConnectionException
				| IllegalFileLocation | InterruptedException
				| InvalidProcessStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Start relay watch
		relayWatch(); 
	}
	
	//Runs constantly in the background and checks every 5 seconds if file needs to be relayed
	public void relayWatch() {
		String destination = ""; 
		String folder = ""; 
		boolean check = true; 
		while(check) {
			try {	
				Pair returnVal = doesHeaderExist(); 
				check = returnVal.getStatement();
				destination = returnVal.getDestination();
				folder = returnVal.getFolder();
				Thread.sleep(5 * 5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	System.out.println(destination  +folder); 	
	fileManager(destination, folder);		
	}

	

	private static  Pair doesHeaderExist()  {
		boolean noTransfer = true; 
		String path = System.getProperty("user.home")  + "/" + "H2H" + name + "/shared";
		String fileName = ""; 
		String fileDestination = ""; 
		File fileTemp = new File(path);
		File sourceHeader = new File(path  +"/","relay.txt");
		File sourceRoot = new File(System.getProperty("user.home") + "/" + "H2H" + name + "/");
		File sourceShared = new File(sourceRoot, "shared");
		try { 
			 if(FileUtils.directoryContains(sourceRoot, sourceShared)) {
				 if(FileUtils.directoryContains(fileTemp, sourceHeader)){
					 System.out.println("works...............................................");
					 try {
						 fileName = FileUtils.readLines(sourceHeader).get(1);
						 fileDestination = FileUtils.readLines(sourceHeader).get(0);
						 FileUtils.forceDelete(sourceHeader);
						
						 } catch (IOException e) {
							 e.printStackTrace();
							 }
					 if(fileDestination.equals(name)){
						 System.out.println(fileDestination + name);
						 noTransfer = true; }
					 else
						 noTransfer =  false; 
					 }
				 else {
					 System.out.println("nothing to do...............................................");
					 noTransfer =  true; 
					 }
				 }
			 } catch (IOException e) {
				 System.out.println("hallo2");
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 
		 Pair newPair =new Pair(fileName, fileDestination, noTransfer); 
		 return newPair; 	
	}
	
		

}