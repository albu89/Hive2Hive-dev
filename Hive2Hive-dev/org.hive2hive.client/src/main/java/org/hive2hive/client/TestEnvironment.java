package org.hive2hive.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.math.BigInteger;

import org.apache.commons.io.FileUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.api.configs.*;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.security.UserCredentials;

import java.nio.file.Paths;
public class TestEnvironment {
	
	IH2HNode node1;
	IH2HNode node2;
	File rootDirectory;
	IUserManager userManager;
	
	public static void main(String[] args) throws UnknownHostException, NoPeerConnectionException, IllegalFileLocation, NoSessionException {
		new TestEnvironment().start();
		
		
		System.exit(0);
	}
	
	public void start() throws UnknownHostException, NoPeerConnectionException, IllegalFileLocation, NoSessionException {
		
		createPeer();
		userManagement();
		fileManagement();
	}
	
	
	public  void createPeer() throws UnknownHostException {
		
		
		// master peer configuration
		INetworkConfiguration masterConfig = NetworkConfiguration.create(" masterID");
		// node peer configuration with bootstrap address
		INetworkConfiguration nodeConfig = NetworkConfiguration.create("nodeID", InetAddress.getByName("localhost"));
		// default file configuration
		//IFileConfiguration defaultFileConf = FileConfiguration.createDefault();
		// custom file configuration:
		// max. file size: 10 MB
		// nr. of versions: 20
		// max. size of all versions: 200 MB
		// chunk size: 1 MB
		BigInteger val1 = new BigInteger("10485760");
		BigInteger val2 = new BigInteger("209715200");
		IFileConfiguration customFileConf = FileConfiguration.createCustom(val1, 20, val2, 1024*1024);
		
		
		//create a node 
		node1 = H2HNode.createNode(masterConfig , customFileConf); 
		node2 = H2HNode.createNode(nodeConfig, customFileConf);
		//connects the node
		node1.connect();
		node2.connect();
	
		
	}
	
	
	public void userManagement () throws NoPeerConnectionException {
		
		userManager = node1.getUserManager();
		UserCredentials credentials1, credentials2; 
		
		credentials1 = new UserCredentials("alex", "alex",
		"alex");
		credentials2 = new UserCredentials("max", "max",
				"max");
		
		userManager.register(credentials1);
		userManager.register(credentials2);
		
		// login the user and provide the local root directory path
		userManager.login(credentials1 , Paths.get(System.getProperty("user.home") ));
		userManager.login(credentials2 , Paths.get(System.getProperty("user.home") ));
		
		
	}
	
	
	public void fileManagement () throws IllegalFileLocation, NoSessionException, NoPeerConnectionException {
	
		
		if(userManager.isLoggedIn("alex"))
			System.out.println();
		
		rootDirectory = new File(FileUtils.getUserDirectory(), "H2H_"
				+ "alex" + "_" + System.currentTimeMillis());

		
		
		//IFileManager fileManager = node1.getFileManager(); 
		File folder = new File(rootDirectory, "demo-folder");
		File file = new File(folder , "demo -file"); // add a file
		
		node1.getFileManager().add(file);
		//fileManager.add(file);
		// share a folder with another user (write permission)
		node1.getFileManager().share(folder , "max", PermissionType.WRITE); // update a file
		node1.getFileManager().update(file);
		
		
	}
	
	

}
