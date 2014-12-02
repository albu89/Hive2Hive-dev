package org.hive2hive.client;


import java.net.UnknownHostException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.io.FileUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.api.configs.*;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.security.UserCredentials;
import java.nio.file.Path;
import java.nio.file.Paths;



public class ethzCore {
	IH2HNode node;
	
	static final String name = "core";
	static final String core = "planetlab2.inf.ethz.ch";
	static final String rootFolder =  "H2H" + name + "_" + System.currentTimeMillis();
	Path rootDirectory = Paths.get(System.getProperty("user.home") + "/" + rootFolder);	
	static final String folderPath = System.getProperty("user.home")  + "/" + rootFolder;

	public static void main(String[] args) throws Exception  {
		new ethzCore().start();
	}
	
	public void start() throws Exception  {
		
		createPeer();
		userManagement();
	
	}
	
	
	public  void createPeer() throws UnknownHostException {
		
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
		
//		create root folder
		File rootDir = new File(FileUtils.getUserDirectory(), rootFolder);
		FileUtils.forceMkdir(rootDir);
			
		
			
	}
	

	

	
	

	

		

}
