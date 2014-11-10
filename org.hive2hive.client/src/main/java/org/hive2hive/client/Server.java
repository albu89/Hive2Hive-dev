package org.hive2hive.client;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.lang.System;

import org.hive2hive.client.console.ConsoleMenu;
import org.apache.commons.io.FileUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.api.configs.*;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.IFileVersion;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.processes.files.list.FileTaste;
import org.hive2hive.core.processes.files.recover.IVersionSelector;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;
import org.hive2hive.core.api.interfaces.IUserManager;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Formatter;



public class Server {
	
	IH2HNode node;
	
	//File rootDirectory;
	IUserManager userManager;
	
	
	static final String name = "alex";
	static final String core = "planetlab2.inf.ethz.ch";
	static final String rootFolder =  "H2H" + name + "_" + System.currentTimeMillis();
	Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);		
	static final String folderPath = System.getProperty("user.home")  + "/" + rootFolder;
	static final String fileName = "/movie.mp4";
		
	public static void main(String[] args) throws Exception {
		new Server().start();
	}
	
	public void start() throws Exception {
	
		createPeer();
		userManagement();
		printMenu();
		menu();
		
	}
	
	
	public  void createPeer() throws UnknownHostException {

	/*	// master peer configuration
		INetworkConfiguration masterConfig = NetworkConfiguration.create("masterID");
		BigInteger val1 = new BigInteger("10485760");
		BigInteger val2 = new BigInteger("209715200");
		IFileConfiguration customFileConf = FileConfiguration.createCustom(val1, 20, val2, 1024*1024);
		
		//create a node 
		node = H2HNode.createNode(masterConfig , customFileConf); 
		node.connect();
		*/
		
		
		//uncomment if soruce
		INetworkConfiguration masterConfig = NetworkConfiguration.create("masterID");
		
		//uncomment if client
		//INetworkConfiguration nodeConfig = NetworkConfiguration.create("nodeID", InetAddress.getByName(core));
	
		BigInteger val1 = new BigInteger("10485760");
		BigInteger val2 = new BigInteger("209715200");
		IFileConfiguration customFileConf = FileConfiguration.createCustom(val1, 20, val2, 1024*1024);
		
		
		//uncomment if source
		node = H2HNode.createNode(masterConfig , customFileConf); 
		
		//uncomment if client
		//node = H2HNode.createNode(nodeConfig, customFileConf);
		node.connect();	
		
		
		
	}
	
	
	public void userManagement () throws NoPeerConnectionException, InterruptedException, IOException {
		
		IUserManager userManager = node.getUserManager();
		 
		UserCredentials credentials = new UserCredentials(name, name, name);
	
		 
		if (!userManager.isRegistered(credentials.getUserId())) {
		    userManager.register(credentials).await();
		}
		userManager.login(credentials, rootDirectory).await();
		
//		Creating root Directory
		
		File rootDir = new File(FileUtils.getUserDirectory(), rootFolder);
		FileUtils.forceMkdir(rootDir);
			
	}
	
	
	public void fileManagement () throws Exception  {
		
		IFileManager fileManager = node.getFileManager();
		fileManager.configureAutostart(false);
		
		
		
	/*	IFileObserverListener listener = new H2HFileObserverListener(fileManager);
		 
		IFileObserver observer = new H2HFileObserver(rootDirectory.toFile());
		observer.addFileObserverListener(listener);
		observer.start();*/
		
		
		
		File folder = new File(folderPath +  "/shared");
		File file = new File(folder, fileName);
		
		ConsoleMenu.print(String.format("Executing '%s'...", "add file"));
		fileManager.add(folder).start().await();
		fileManager.add(file).start().await();

//		Sharing the file
		ConsoleMenu.print(String.format("Executing '%s'...", "share file"));
		
		long start = System.nanoTime();    
		fileManager.share(file, "duke", PermissionType.WRITE).start().await();
		long elapsedTime = System.nanoTime() - start;
		
		ConsoleMenu.print("Transmission Time: " + elapsedTime);
	
//		Version selection	
		IVersionSelector versionSelector = new IVersionSelector() {
		    @Override
		    public IFileVersion selectVersion(List<IFileVersion> availableVersions) {
		        return availableVersions.get(0);
		    }

			@Override
			public String getRecoveredFileName(String fullName, String name,
					String extension) {
				// TODO Auto-generated method stub
				return null;
			}
		};
					
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
			
			if(node.getUserManager().isRegistered("paris")){
				ConsoleMenu.print("paris is registered");
				
			}
			
			if(node.getUserManager().isRegistered("karlsruhe")){
				ConsoleMenu.print("karlsruhe is registered");
				
			}
			
			if(node.getUserManager().isRegistered("warsaw")){
				ConsoleMenu.print("warsaw is registered");
				
			}
			
			if(node.getUserManager().isRegistered("ethz")){
				ConsoleMenu.print("ethz is registered");
				
			}
			
			if(node.getUserManager().isRegistered("alex")){
				ConsoleMenu.print("alex is registered");
				
			}
			
			
		}
		
		if(input.equals("2")) 
			fileManagement();
	
		if(input.equals("3")) 
			printFiles();
		if(input.equals("4"))
			System.exit(0);
		
		}
		
	}
	
	

	
	
	
	
	
}
