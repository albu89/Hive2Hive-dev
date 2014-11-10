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
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.processes.files.list.FileTaste;
import org.hive2hive.core.security.UserCredentials;
import org.hive2hive.processframework.interfaces.IResultProcessComponent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class parisClient {
	IH2HNode node;
	
	static final String name = "paris";
	static final String core = "planetlab2.inf.ethz.ch";
	static final String rootFolder =  "H2H" + name + "_" + System.currentTimeMillis();
	Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);	
	
	public static void main(String[] args) throws Exception  {
		new parisClient().start();
	}
	
	public void start() throws Exception  {
		
		createPeer();
		userManagement();
		//menu();

	}
	
	
	public  void createPeer() throws UnknownHostException {
		
		//uncomment if soruce
		/*INetworkConfiguration masterConfig = NetworkConfiguration.create("masterID");*/
		
		//uncomment if client
		INetworkConfiguration nodeConfig = NetworkConfiguration.create("nodeID", InetAddress.getByName(core));
	
		BigInteger val1 = new BigInteger("10485760");
		BigInteger val2 = new BigInteger("209715200");
		IFileConfiguration customFileConf = FileConfiguration.createCustom(val1, 20, val2, 1024*1024);
		
		
		//uncomment if source
		/*node = H2HNode.createNode(masterConfig , customFileConf); */
		
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
	
	public void fileManagement () throws Exception  {
		
		IFileManager fileManager = node.getFileManager();
		fileManager.configureAutostart(false);
	
		
		IResultProcessComponent<List<FileTaste>> fileListProcess = fileManager.getFileList();
		fileListProcess.start().await();
		
		
		if(fileListProcess.getResult().isEmpty())
			ConsoleMenu.print("something went wrong");
		
		for (FileTaste fileTaste : fileListProcess.getResult()) {
			ConsoleMenu.print("* " + fileTaste);
		}
	
		
	}
	
	
	public void menu() throws Exception {
		Console console = System.console();
		while(true){
			
			BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
			
			ConsoleMenu.print("Enter an option: ");
			String input = br.readLine();
		
		
		if(input.equals("2")) 
			fileManagement();
	
		
		}
		
	}
	
	

		

}
