package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.security.MessageDigest;

import client.SystemState;

/**
 * Client-side program: Handles all of the communications from client to server
 * @author Caleb Shortt
 */
public class ConnectionController {
	
	private int connectionRetries = 5;
	private int port = 4444;
	private String server = "";
	private Socket clientSocket = null;
	
	private DataInputStream inputStream = null;
	private PrintStream outputStream = null;
	
	
	
	private ConnectionController() {
		
		// Try to connect to server
		int attemptCount = 0;
		boolean success = false;
		while(!success && attemptCount < connectionRetries) {
			success = connect();
			try {
				Thread.sleep(100);
			} catch (Exception e) {}
			attemptCount++;
		}
		
		// Check if connection was successful and update system state
		if (!success) {
			System.err.println("[Error] Could not contact server.");
		} else {
			SystemState.Connected = true;
		}
		
		// Attempt to create an input and output stream to the server
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new PrintStream(clientSocket.getOutputStream());
		} catch(Exception e) {
			System.err.println("[Error] Could not create communication channels to server.");
			e.printStackTrace();
		}
		
		
	}
	
	
	private static ConnectionController controller = null;
	public static ConnectionController getInstance() {
		if (controller == null)
			controller = new ConnectionController();
		return controller;
	}
	
	
	/**
	 * Attempts to connect to the server from the given parameters 'server' and 'port'
	 * @return success
	 */
	private boolean connect() {
		try {
			clientSocket = new Socket(server, port);
		} catch (IOException ioe) {
			return false;
		}
		return true;
	}
	
	
	// TODO: Finish this function
	public boolean validateUser(String username, String password, String ip) {
		// - The username and password will be sent to this function in plain text form
		// - The password will have to be run through a md5 hash before being sent to the server
		// - The ip address will be used to link the user and the computer where the client is installed
		// - This function will also "register" the client with the server
		//		- The client will be given a unique id upon successful validation
		//		- This id will be required upon for stream creation and ending streams
		byte[] passwordDigest = null;
		try {
			byte[] passwordHash = password.getBytes("UTF-8");
			MessageDigest digest = MessageDigest.getInstance("MD5");
			passwordDigest = digest.digest(passwordHash);
		} catch (Exception e) {
			System.err.println("[Critical Error] Could not securely valudate username and password.");
			return false;
		}
		String hashedPassword = new String(passwordDigest);
		
		
		
		
		
		return true;
	}
	
	
	// TODO: Finish this function
	public boolean createStream() {
		// - Not sure what variables will be passed to this function yet.
		// - This function will tell the server to register a stream with the unique id given to the client
		// 		- It will also begin the streaming process (Probably done in a separate thread)
		
		return true;
	}
	
	
	// TODO: Finish this function
	public boolean endStream() {
		// - This function will contact the server and tell it that the streaming has stopped
		// - This will provide a log trace for all ending of streams
		// - This function will help prevent unauthorized users from stopping the streaming
		
		return true;
	}
}
