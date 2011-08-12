package motiondetection;


import java.io.File;
import java.util.TimerTask;

import javax.media.DataSink;
import javax.media.Processor;

import client.SystemState;

import connection.DatabaseConnectionManager;
import connection.WebConnectionManager;

public class StopBroadcastTask extends TimerTask
{
	private WebConnectionManager webManager = null;
	private Processor processor = null;
	private DataSink datasink = null;
	private File file = null;
	
	public StopBroadcastTask(File file, Processor processor, DataSink datasink, WebConnectionManager webmanager)
	{
		this.processor = processor;
		this.datasink = datasink;
		this.webManager = webmanager;
		this.file = file;
	}
	
	public void run()
	{
		try {
			this.webManager.stopBroadcast();
			
			String name = this.file.getPath();
			name = name.replaceAll("temp.", "");
			name = name.replaceAll(".nonstreamable", "");
			
			this.processor.stop();
			this.file.renameTo(new File(name));
			this.datasink.stop();
			this.datasink.close();
			
			SystemState.Recording = false;
			System.out.println("BROADCAST HAS BEEN TERMINATED");
		} 
		catch(Exception e)
		{
			System.err.println("[ERROR] Could not stop broadcast task: " + e);
		}
	}
}

