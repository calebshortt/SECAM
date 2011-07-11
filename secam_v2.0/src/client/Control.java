package client;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import javax.media.MediaLocator;

import connection.DBConnSingleton;
import connection.DatabaseConnectionManager;
import connection.WebConnSingleton;
import connection.WebConnectionManager;
import motiondetection.MotionDetector;
import motiondetection.MotionEvent;
import motiondetection.MotionListener;
import motiondetection.MotionListenerInterface;
import motiondetection.StopBroadcastTask;

public class Control implements MotionListenerInterface, SystemStateChangeInterface {
	
	private MotionDetector motionDetector = null;
	private DatabaseConnectionManager dbConnManager = null;
	private WebConnectionManager webConnManager = null;
	
	/**
	 * Number of <b>minutes</b> that the system will record the video stream when motion is detected.
	 */
	private double recordingInterval = 0.5; // record for .5 of a minute; or 30 seconds.
	
	private SimpleDateFormat formatter = null;
	private Date currentTime = null;
	private Date targetTime = null;
	
	
	/**
	 * Constructor: Takes a frame that will display the results from the motion detection, and 
	 * also takes the location of the media to be used (Camera feed)
	 * @param frame
	 */
	public Control(MediaLocator ds, Frame frame) {
		MotionListener.addMotionListener(this);
		dbConnManager = DBConnSingleton.getInstance();
		webConnManager = WebConnSingleton.getInstance();
		motionDetector = new MotionDetector(ds, frame);
	}
	
	
	
	
	public void startBroadcast() {
		currentTime = new Date();
		long time = currentTime.getTime() + (long)(recordingInterval * 60 * 1000);
		targetTime = new Date(time);
		
		try {
			// init database broadcasting
			// init web broadcasting
		}
		catch (Exception e)
		{
			System.err.println("An error has occurred while trying to set up broadcasting: ");
			e.printStackTrace();
		}
		
		Timer timer = new Timer();
		timer.schedule(new StopBroadcastTask(dbConnManager, webConnManager), (long)(recordingInterval*60*1000));
	}
	
	
	public void stopBroadcast() {
		
	}
	
	
	public void cleanup() {
		try {
			dbConnManager.disconnect();
		} catch(Exception e) {
			System.err.println("[Warning]\tCould not close database connection.");
		}
		try {
			webConnManager.stopBroadcast();
		} catch(Exception e) {
			System.err.println("[Warning]\tFailed to stop broadcast.");
		}
	}
	
	
	public Frame getFrame() {
		return motionDetector.getFrame();
	}
	

	/**
	 * The motion event handler for this class. When motion 
	 * is detected, this function is called.
	 * @param motionevent (MotionEvent)
	 * @return void
	 */
	public void motion(MotionEvent me) {
		if (!SystemState.Recording && SystemState.Activated)
		{
			// TODO
			// Start Recording / Broadcasting
		}
	}


	/**
	 * Called when the system state changes. Handles the resulting logic.
	 * @param StateChangeEvent
	 */
	public void stateChanged(StateChangeEvent sce) {
		if(!SystemState.Activated)
		{
			// TODO
			// System has changed from activated to deactivated
			// stop trigger / recording / etc
		}
	}
}
