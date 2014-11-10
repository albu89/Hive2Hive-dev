package org.hive2hive.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import org.apache.commons.io.FileUtils;
import org.hive2hive.client.console.ConsoleMenu;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.api.configs.*;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.IFileVersion;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.processes.files.list.FileTaste;
import org.hive2hive.core.processes.files.recover.IVersionSelector;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class ethzServer {
	IH2HNode node;
	
	static final String name = "ethz";
	static final String core = "planetlab3.inf.ethz.ch";
	static final String rootFolder =  "H2H" + name + "_" + System.currentTimeMillis();
	Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);	
	static final String folderPath = System.getProperty("user.home")  + "/" + rootFolder;
	static final String fileNameBig = "/movie.mp4";
	static final String fileNameMedium = "/picture.mp4";
	static final String fileNameSmall = "/text.mp4";
	
	
	public static void main(String[] args) throws Exception  {
		new ethzServer().start();
	}
	
	public void start() throws Exception  {
		
		createPeer();
		userManagement();
		menu();

	}
	
	
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
	
	
	public void userManagement () throws NoPeerConnectionException, InterruptedException, IOException {
		
		IUserManager userManager = node.getUserManager();
		 
		UserCredentials credentials = new UserCredentials(name, name, name);
		
		if (!userManager.isRegistered(credentials.getUserId())) {
		    userManager.register(credentials).await();
		}
		userManager.login(credentials, rootDirectory).await();
		
//		create root folder
		File rootDir = new File(FileUtils.getUserDirectory(), rootFolder);
		FileUtils.forceMkdir(rootDir);
			
		
			
	}
	

	
	public void printFiles() throws NoSessionException, InterruptedException, InvalidProcessStateException {
		IFileManager fileManager = node.getFileManager();
		fileManager.configureAutostart(false);
		
		IResultProcessComponent<List<FileTaste>> fileListProcess = fileManager.getFileList();
		fileListProcess.start().await();
		
		for (FileTaste fileTaste : fileListProcess.getResult()) {
			ConsoleMenu.print("* " + fileTaste);
		}
	}
	
	
	public void analysis(String city) throws NoSessionException, NoPeerConnectionException, IllegalFileLocation, InterruptedException, InvalidProcessStateException {
		
		long start, elapsedTime; 
		IFileManager fileManager = node.getFileManager();
		fileManager.configureAutostart(false);
		
		File folder = new File(folderPath +  "/shared");
		
		File folderBig = new File(folderPath +  "/shared/big");
		File folderMedium = new File(folderPath +  "/shared/medium"); 
		File folderSmall = new File(folderPath +  "/shared/small"); 
		
		File fileBig = new File(folderBig, fileNameBig);
		File fileMedium = new File(folderMedium, fileNameMedium);
		File fileSmall = new File(folderSmall, fileNameSmall);
		
		ConsoleMenu.print(String.format("Executing '%s'...", "add file"));
		fileManager.add(folder).start().await();
		
		fileManager.add(folderBig).start().await();
		fileManager.add(folderMedium).start().await();
		fileManager.add(folderSmall).start().await();
		
		fileManager.add(fileBig).start().await();
		fileManager.add(fileMedium).start().await();
		fileManager.add(fileSmall).start().await();
		
		
		ConsoleMenu.print(String.format("Executing '%s'...", "Test - " + city));
		
		//Big File
		ConsoleMenu.print(String.format("Executing '%s'...", "Share big file"));
		
		start = System.nanoTime();    
		fileManager.share(folderBig, city, PermissionType.WRITE).start().await();
		elapsedTime = System.nanoTime() - start;
		
		ConsoleMenu.print("Transmission Time big "+ city + ": " + elapsedTime);
		
		//Medium File
		ConsoleMenu.print(String.format("Executing '%s'...", "Share medium file"));
		
		start = System.nanoTime();    
		fileManager.share(folderMedium, city, PermissionType.WRITE).start().await();
		elapsedTime = System.nanoTime() - start;
		
		ConsoleMenu.print("Transmission Time medium " + city + ": " + elapsedTime);
		
		//Small File
		ConsoleMenu.print(String.format("Executing '%s'...", "Share small file"));
		
		start = System.nanoTime();    
		fileManager.share(folderSmall, city, PermissionType.WRITE).start().await();
		elapsedTime = System.nanoTime() - start;
		
		ConsoleMenu.print("Transmission Time small " + city  + ": " + elapsedTime);	
		
		
	}
	
	
	public void printMenu () {
		
		ConsoleMenu.print("1.) List registered Users\n "
				+ "2.) Share File \n 3.) "
				+ "Print Files \n "
				+ "4.) Exit");
		
		
			}
	
	
	
	public void menu() throws Exception {
		Console console = System.console();
		while(true){
			
			BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
			
			ConsoleMenu.print("Enter an option: ");
			String input = br.readLine();
		
		if (input.equals("1")) {
			
			if(node.getUserManager().isRegistered("london")){
				ConsoleMenu.print("london is registered");
				
			}
			
			if(node.getUserManager().isRegistered("karlsruhe")){
				ConsoleMenu.print("karlsruhe is registered");
				
			}
			
			if(node.getUserManager().isRegistered("warsaw")){
				ConsoleMenu.print("warsaw is registered");
				
			}
			
			if(node.getUserManager().isRegistered("core")){
				ConsoleMenu.print("ethz is registered");
				
			}
			
			if(node.getUserManager().isRegistered("stockholm")){
				ConsoleMenu.print("stockholm is registered");
				
			}
			
			
		}
		
		if(input.equals("2")) {
		 br = new BufferedReader( new InputStreamReader(System.in));
		
		ConsoleMenu.print("Enter an option: ");
		String city = br.readLine();
       
        switch (city) {
            case "london": analysis("london"); 
                     break;
            case "stockholm": analysis("stockholm"); 
                     break;
            case "karlsruhe": analysis("karlsruhe"); 
                     break;
            case "warsaw":  analysis("warsaw"); 
                     break;
            case "core":  analysis("core");
                     break;
            default:  break;
            
        }
        
		}
	
		if(input.equals("3")) 
			printFiles();
		if(input.equals("4"))
			System.exit(0);
		
		}
		
	}
	
	

		

}
