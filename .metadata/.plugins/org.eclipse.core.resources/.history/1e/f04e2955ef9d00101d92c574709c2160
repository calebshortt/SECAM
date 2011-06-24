package motiondetection;

/**
 * Copyright:    Copyright (c) 2002 by Konrad Rzeszutek
 *
 *
 *
 *
 *    This file is part of Motion Detection toolkit.
 *
 *    Motion Detection toolkit is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    Motion Detection toolkit is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Foobar; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.Format;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.rtcp.SourceDescription;
import javax.swing.JOptionPane;

import syscontrolsdemo.InputDeviceManager;
import syscontrolsdemo.StateChangeEvent;
import syscontrolsdemo.SystemState;
import syscontrolsdemo.SystemStateChangeInterface;

import connection.ConnSingleton;
import connection.ConnectionManager;
import connection.StreamInfo;

/**
 * This class was created by Konrad Rzeszutek and heavily modified 
 * by Caleb Shortt to work with the SysControl System. This is the 
 * main controller for the motion detection of the SysControl System.
 * @author Konrad Rzeszutek and Caleb Shortt
 */
public class MotionDetector implements ControllerListener, MotionListenerInterface, SystemStateChangeInterface
{
	
	/**
	 * This is the processor that will display the stream to the client GUI screen
	 */
	private Processor displayProcessor = null;
	/**
	 * This is the processor that will take the webcam datasource and convert it to the output format for the database
	 */
	private Processor streamProcessor = null;
	
	/**
	 * This processor converts the input stream into a transmittable format for a network.
	 */
	private Processor networkProcessor = null;
	
	/**
	 * Is used to manage the transmitting network stream
	 */
	private RTPManager rtpManager = null;
	
	/**
	 * Output stream that transmits the video data over a network
	 */
	private SendStream stream = null;
	/**
	 * This is the data sink that will broadcast the stream to the destination specified by the destinationLocator
	 */
	private DataSink broadcaster = null; 
	
	DataSink fileW = null;
	Object waitSync = new Object();
	boolean stateTransitionOK = true;
	Frame frame = null;
	
	InputDeviceManager inputDeviceManager = null;

	/**
	 * Manages the connections to the database
	 */
	private ConnectionManager manager = null;
	
	/**
	 * This media locator specifies the location of the webcamera that will be used to obtain a video stream from
	 */
	private MediaLocator inputMediaLocator = null;
	/**
	 * This media locator will specify the location that the broadcaster will send the stream to (a URL)
	 */
	private MediaLocator destinationLocator = null;
	/**
	 * This is the data source created from the inputMediaLocator variable 
	 */
	private DataSource inputDataSource = null;
	
	private SimpleDateFormat formatter = null;
	private Date currentTime = null;
	private Date targetTime = null;
	
	/**
	 * Number of <b>minutes</b> that the system will record the video stream when motion is detected.
	 */
	private double recordingInterval = 0.5; // record for .5 of a minute; or 30 seconds.
	
	private TrackControl tc[];
	private TrackControl videoTrack = null;
	
	private MediaLocator locator = null;
	private DataSink dataSink = null;
	

	/**
	 * The default constructor will create a new test Frame to be used 
	 * by the motion detector system.
	 */
	public MotionDetector()
	{
		frame = new Frame("Test Motion Detection");
	}

	
	/**
	 * This overrided constructor takes a data source and a frame. The 
	 * data source is the location to the video stream (Webcam) and the 
	 * provided frame will be used to display the output video stream. 
	 * <b>This is the main constructor of this class and should be 
	 * initialised for its use.</b>
	 * @param datasource (MediaLocator)
	 * @param frame (Frame)
	 */
	public MotionDetector(MediaLocator ds, Frame frame)
	{
		this.frame = frame;
		MotionListener.addMotionListener(this);
		manager = ConnSingleton.getInstance();
		formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		inputMediaLocator = ds;

		if (!open(inputMediaLocator))
		{
			System.err.println("Could not create stream");
			System.exit(0);
		}
	}

	/**
	 * Given a datasource, create a processor and use that processor as a player
	 * to playback the media. During the processor's Configured state, the MotionDetectionEffect and
	 * TimeStampEffect is inserted into the video track.
	 * @param datasource (MediaLocator)
	 * @return success (boolean)
	 */
	public boolean open(MediaLocator ds)
	{

		try
		{
			inputDataSource = Manager.createCloneableDataSource(Manager.createDataSource(ds));
			displayProcessor = Manager.createProcessor(inputDataSource);
		} catch (Exception e)
		{
			System.err.println("Failed to create a processor from the given datasource: " + e);
			return false;
		}

		displayProcessor.addControllerListener(this);

		// Put the Processor into configured state.
		displayProcessor.configure();
		if (!waitForState(displayProcessor.Configured))
		{
			System.err.println("Failed to configure the processor.");
			return false;
		}

		// So I can use it as a player.
		displayProcessor.setContentDescriptor(null);
		// Obtain the track controls.
		tc = displayProcessor.getTrackControls();

		if (tc == null)
		{
			System.err.println("Failed to obtain track controls from the processor.");
			return false;
		}

		// Search for the track control for the video track.
		for (int i = 0; i < tc.length; i++)
		{
			if (tc[i].getFormat() instanceof VideoFormat)
			{
				videoTrack = tc[i];
				break;
			}
		}

		if (videoTrack == null)
		{
			System.err.println("The input media does not contain a video track.");
			return false;
		}

		// Instantiate and set the frame access codec to the data flow path.
		try
		{
			Codec codec[] = { new MotionDetectionEffect(), new TimeStampEffect() };
			videoTrack.setCodecChain(codec);
		} catch (UnsupportedPlugInException e)
		{
			System.err.println("The processor does not support effects.");
		}

		// Realize the processor.
		displayProcessor.prefetch();
		if (!waitForState(displayProcessor.Prefetched))
		{
			System.err.println("Failed to realize the processor.");
			return false;
		}
		// Display the visual & control component if there's one.

		// Get the player. Or construct the player from the processor

		frame.setLayout(new BorderLayout());

		// Component cc;

		Component vc;
		if ((vc = displayProcessor.getVisualComponent()) != null)
		{
			frame.add("Center", vc);
		}
		// Start the processor.
		displayProcessor.start();
		return true;
	}

	/**
	 * Adds notification to the class' frame and packs it.
	 * @param none
	 * @return void
	 */
	public void addNotify()
	{
		frame.addNotify();
		frame.pack();
	}

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 * @param state (int)
	 * @return success (boolean)
	 */
	public boolean waitForState(int state)
	{
		synchronized (waitSync)
		{
			try
			{
				while (displayProcessor.getState() != state && stateTransitionOK)
					waitSync.wait();
			} 
			catch (Exception e)
			{
			}
		}
		return stateTransitionOK;
	}

	
	/**
	 * Controller Listener.
	 * @param event (ControllerEvent)
	 * @return void
	 */
	public void controllerUpdate(ControllerEvent evt)
	{
		if (evt instanceof ConfigureCompleteEvent || evt instanceof RealizeCompleteEvent || evt instanceof PrefetchCompleteEvent)
		{
			synchronized (waitSync)
			{
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} 
		else if (evt instanceof ResourceUnavailableEvent)
		{
			synchronized (waitSync)
			{
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		} 
		else if (evt instanceof EndOfMediaEvent)
		{
			displayProcessor.close();
			System.exit(0);
		}
	}

	
	/**
	 * Closes the processor and cleans up the system.
	 * @param none
	 * @return void
	 * @throws IOException 
	 */
	public void cleanup()
	{
		manager.disconnect();
		displayProcessor.close();
		try
		{
			stopBroadcast();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * @param none
	 * @return The display frame
	 */
	public Frame getFrame()
	{
		return frame;
	}

	
	/**
	 * This main function is purely for debugging purposes.
	 * @param command arguments array
	 * @return void
	 */
	public static void main(String[] args)
	{

		if (args.length == 0)
		{
			prUsage();
			System.exit(0);
		}

		String url = args[0];

		if (url.indexOf(":") < 0)
		{
			prUsage();
			System.exit(0);
		}

		MediaLocator ml;
		if ((ml = new MediaLocator(url)) == null)
		{
			System.err.println("Cannot build media locator from: " + url);
			System.exit(0);
		}

		MotionDetector fa = new MotionDetector();

		if (!fa.open(ml))
			System.exit(0);
	}
	
	
	/**
	 * Test output function for the main function of this class.
	 * @param none
	 * @return void
	 */
	private static void prUsage()
	{
		System.err.println("Usage: java TestMotionDetection <url>");
	}

	
	/**
	 * The motion event handler for this class. When motion 
	 * is detected, this function is called.
	 * @param motionevent (MotionEvent)
	 * @return void
	 */
	public void motion(MotionEvent me)
	{
		if (!SystemState.Recording && SystemState.Activated)
		{
			//SystemState.Recording = true;
			startRecording();
		}
	}

	
	/**
	 * Starts the recording of the video stream to the database for 
	 * <b>[recordingInterval]</b> minutes.
	 * @param none
	 * @return void
	 */
	private void startRecording()
	{
		System.out.println("\n--- ATTEMPTING TO START BROADCAST ---\n");
		
		currentTime = new Date();
		long time = currentTime.getTime() + (long)(recordingInterval * 60 * 1000);
		targetTime = new Date(time);
		
		/*
		Object[] list = videoTrack.getControls();									// DEBUG
		for(int i=0; i<list.length; i++)											// DEBUG
		{																			// DEBUG
			System.out.println(list[i].getClass());									// DEBUG
			System.out.println("----------------------------------------------");	// DEBUG
		}																			// DEBUG
		*/
		
		//manager.connect();
		
		try {
			initBroadcasting();
		}
		catch (Exception e)
		{
			System.err.println("An error has occurred while trying to set up broadcasting: ");
			e.printStackTrace();
		}
		
		Timer timer = new Timer();
		timer.schedule(new StopBroadcastTask(manager, broadcaster, streamProcessor), (long)(recordingInterval*60*1000));
	}
	
	
	/**
	 * Sets the broadcaster DataSink to the destination url (from the ConnectionStatics class) and creates the stream 
	 * realized processor. The data source parameter is the webcamera data source. This streams to the database. There 
	 * is a separate broadcaster for streaming to a network (web controls).
	 * @param datasource
	 * @throws IOException
	 * @throws NoProcessorException
	 * @throws CannotRealizeException
	 * @throws NoDataSinkException
	 */
	public void initBroadcasting() throws IOException, NoProcessorException, CannotRealizeException, NoDataSinkException
	{
		System.out.println("\t--- Initializing Broadcast ---");
		
		FileTypeDescriptor outputType = new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME);
		DataSource source = ((SourceCloneable)inputDataSource).createClone();
		
		Format[] formats = { new VideoFormat(VideoFormat.CINEPAK) };
		
		streamProcessor = Manager.createRealizedProcessor(new ProcessorModel(source, formats, outputType));
		
		String url_str = manager.createStream( new StreamInfo(0, 0, formatter.format(new Date()), formatter.format(new Date()), formats[0]) );
		
		destinationLocator = new MediaLocator("file://" + url_str);
		broadcaster = Manager.createDataSink(streamProcessor.getDataOutput(), destinationLocator);
		
		// TODO: delete
		// --------------------------------------------------------------------------------------------------------
		//System.err.println("Destination File: " + url_str);											// <----- DEBUG
		//System.err.println("Destination Media Locator: " + destinationLocator);						// <----- DEBUG
		//System.err.println("Broadcaster: " + broadcaster.getOutputLocator());
		// --------------------------------------------------------------------------------------------------------
		
		
		initNetworkBroadcast();
		startBroadcast();
	}
	
	/**
	 * Initializes the network broadcasting processors and data sinks. It does NOT start or stop them: there are separate 
	 * methods for that.
	 * @throws NoProcessorException
	 * @throws CannotRealizeException
	 * @throws IOException
	 * @throws NoDataSinkException
	 * @throws NotRealizedError
	 */
	public void initNetworkBroadcast() throws NoProcessorException, CannotRealizeException, IOException, NoDataSinkException, NotRealizedError 
	{
		System.out.println("\t--- Initializing Network Broadcast ---");
		
		DataSource source = ((SourceCloneable)inputDataSource).createClone();
		
		Format[] formats = new Format[] { new VideoFormat(VideoFormat.JPEG_RTP) };
		ProcessorModel model = new ProcessorModel(source, formats, new ContentDescriptor(ContentDescriptor.RAW_RTP));
		
		// TODO
		System.err.println("\t\tCreating Network Processor");				// DEBUG
		
		networkProcessor = Manager.createRealizedProcessor(model);
		
		System.err.println("\t\tGetting new instance of RTPManager");
		
		rtpManager = RTPManager.newInstance();
		SessionAddress local, dest;
		InetAddress ipAddr;
		
		int port = 22222;
		SourceDescription desc;
		
		try
		{
			ipAddr = InetAddress.getLocalHost();
			local = new SessionAddress();
			dest = new SessionAddress(ipAddr, port);
			
			// TODO
			System.err.println("\t\tInitializing RTPManager to local (" + local + ")");
			
			rtpManager.initialize(local);
			
			// TODO
			System.err.println("\t\tSetting TRPManager target to dest (" + dest + ")");
			
			rtpManager.addTarget(dest);
			
			// TODO
			System.err.println("\t\tCreating Send Stream from Network Processor: " + networkProcessor.getDataOutput());
			
			stream = rtpManager.createSendStream(networkProcessor.getDataOutput(), 0);
			
			// TODO
			System.err.println("\t\tStream Created: " + stream);
			
			// TODO: delete
			//JOptionPane.showMessageDialog(null, "local = " + local);					// <--- DEBUG
			//JOptionPane.showMessageDialog(null, "dest = " + dest);						// <--- DEBUG
			//JOptionPane.showMessageDialog(null, "rtpManager = " + rtpManager);			// <--- DEBUG
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the broadcast of the stream across the network
	 * @throws IOException
	 */
	public void startNetworkBroadcast() throws IOException
	{
		System.out.println("\t--- Starting Network Broadcast ---");
		System.err.println("\t\tStarting Network Processor: " + networkProcessor);
		networkProcessor.start();
		System.err.println("\t\tStarting Stream: " + stream);
		stream.start();
		System.out.println("\t--- Network Broadcast Started ---");
	}
	
	/**
	 * Stops the broadcast of the stream across the network
	 * @throws IOException
	 */
	public void stopNetworkBroadcast() throws IOException
	{
		System.out.println("\t--- Stopping Network Broadcast ---");
		System.err.println("\t\tStopping Network Processor: " + networkProcessor);
		networkProcessor.stop();
		System.err.println("\t\tStopping Stream: " + stream);
		stream.stop();
	}
	
	/**
	 * Starts the conversion of the video stream to the database and broadcasts it to the destination location specified by 
	 * the destinationLocator media locator variable. Also starts the network broadcast.
	 * @throws IOException
	 */
	public void startBroadcast() throws IOException
	{
		System.out.println("\t--- Starting Broadcast ---");
		System.err.println("\t\tStarting Stream Processor: " + streamProcessor);
		streamProcessor.start();
		System.err.println("\t\tOpening Broadcaster: " + broadcaster);
		broadcaster.open();
		System.err.println("\t\tStarting Broadcaster");
		broadcaster.start();
		startNetworkBroadcast();
		SystemState.Recording = true;
		
		System.out.println("\t--- Broadcasting ---");
	}
	
	/**
	 * Stops the conversion of the video stream to the database and the broadcast to the destination location. 
	 * Also stops the network broadcast.
	 * @throws IOException
	 */
	public void stopBroadcast() throws IOException
	{
		System.out.println("\t--- Stopping ---");
		streamProcessor.stop();
		streamProcessor.close();
		
		broadcaster.stop();
		broadcaster.close();
		
		stopNetworkBroadcast();
		
		SystemState.Recording = false;
	}
	
	/**
	 * Sets the input device manager to be used for the motion detector
	 * @param InputDeviceManager
	 */
	public void setInputDeviceManager(InputDeviceManager idm)
	{
		inputDeviceManager = idm;
	}

	/**
	 * Called when the system state changes. Handles the resulting logic.
	 * @param StateChangeEvent
	 */
	public void stateChanged(StateChangeEvent sce)
	{
		if(!SystemState.Activated)
		{
			// System has changed from activated to deactivated
			// stop trigger / recording / etc
			
			try
			{
				stopBroadcast();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}














