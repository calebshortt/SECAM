package motiondetection;


import java.util.TimerTask;

import javax.media.DataSink;
import javax.media.Processor;

import client.SystemState;

import connection.DatabaseConnectionManager;

public class StopBroadcastTask extends TimerTask
{
	private DatabaseConnectionManager manager = null;
	
	public StopBroadcastTask(DatabaseConnectionManager manager)
	{
		this.manager = manager;
	}
	
	public void run()
	{
		try {
			// TODO: Add a new class that will be in the database connection manager.
			//		This class should handle the actual saving of the video file to hard disk.
			//		Access it through the dbmanager class and stop it here.
			System.out.println("BROADCAST HAS BEEN TERMINATED");
			//manager.disconnect();
			SystemState.Recording = false;
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

