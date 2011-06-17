package syscontrolsdemo;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.swing.JPanel;


/**
 * Deals with the streaming and display of a given video.
 * @author Caleb Shortt
 */
public class VideoComponent {
	
	private Player player;
	public Frame frame = null;
	public JPanel panel = null;

	
	public VideoComponent() {
	}

	
	/**
	 * Given a frame and capture device information, this funciton will link the two 
	 * so that the frame will be used as a display for the stream.
	 * @param <b>Output frame</b> (Frame)
	 * @param <b>Device info</b> (CaptureDeviceInfo)
	 * @return Modified output frame with capture device linked to it.
	 */
	public Frame makeFrame(Frame f, CaptureDeviceInfo info) {
		frame = f;
		panel = new JPanel();

		createPlayer(info);
		Button start = new Button("Start Camera");
		Button stop = new Button("Stop Camera");

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.start();
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.stop();
			}
		});

		panel.add(start, BorderLayout.NORTH);
		panel.add(stop, BorderLayout.SOUTH);

		frame.setSize(300, 300);
		frame.add(panel);
		return frame;
	}
	
	
	/**
	 * Creates a player and initialises its listeners given device information.
	 * @param info (CaptureDeviceInfo)
	 * @return void
	 */
	private void createPlayer(CaptureDeviceInfo info) {
		try {
			player = Manager.createPlayer( info.getLocator() );
			player.addControllerListener(new EventHandler());
			
		} catch (Exception e) {
			System.err.println("An unexpected error has occurred.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Starts the player (Displays the stream)
	 * @param
	 * @return void
	 */
	public void activateCamera() {
		player.start();
	}
	
	
	/**
	 * Stops the player (Stops the display stream)
	 * @param
	 * @return void
	 */
	public void stopCamera() {
		player.stop();
	}

	
	/**
	 * This function is purely for debugging. It displays the stream in a simple GUI.
	 * @param Input argument string array
	 */
	public static void main(String[] args) {
		Frame frame = new Frame();
		VideoComponent test = new VideoComponent();
		InputDeviceManager DeviceManager = new InputDeviceManager();
		Vector<CaptureDeviceInfo> videoDevices = DeviceManager.getVideoDevices();
		test.makeFrame(frame,videoDevices.get(0));
		frame.setVisible(true);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				System.exit(0);
			}
		});
	}
	
	
	/**
	 *  Inner class of VideoComponent to handler events from the media player.
	 * @author Caleb Shortt
	 */
	private class EventHandler implements ControllerListener {
		public void controllerUpdate(ControllerEvent e) {
			if (e instanceof RealizeCompleteEvent) {

				// load Visual and Control components if they exist
				Component visualComponent = player.getVisualComponent();
				if (visualComponent != null) {
					frame.add(visualComponent, BorderLayout.CENTER);
				}

				frame.doLayout();
			}
		}
	}
}
