package syscontrolsdemo;

import java.util.Enumeration;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;


/**
 * This class finds and manages all of the audio and video input devices connected to the host computer. 
 * It has various methods for retrieving streams, devices, and information about them.
 * @author Caleb Shortt
 */
public class InputDeviceManager
{
	Vector<CaptureDeviceInfo> deviceList = null;
	Vector<CaptureDeviceInfo> videoList = null;
	Vector<CaptureDeviceInfo> otherList = null;

	
	/**
	 * Initialises and populates the video and audio lists
	 */
	public InputDeviceManager()
	{
		videoList = new Vector<CaptureDeviceInfo>();
		otherList = new Vector<CaptureDeviceInfo>();
		getDevices(null);
		sortDevices();
	}

	// -----------------------------------------------------------------------------------------------
	// Utility Functions
	// -----------------------------------------------------------------------------------------------

	/**
	 * Gets all of the media devices connected to the computer
	 * @param
	 * @return void
	 */
	@SuppressWarnings("unchecked")
	private void getDevices(Format format)
	{
		try
		{
			deviceList = (Vector<CaptureDeviceInfo>) CaptureDeviceManager.getDeviceList(format).clone();
		} catch (Exception e)
		{
			System.err.println("An error occurred while trying to create device list.");
		}
	}

	
	/**
	 * Sort devices into either video or other(audio) devices.
	 * @param
	 * @return void
	 */
	private void sortDevices()
	{

		Enumeration<CaptureDeviceInfo> enu = deviceList.elements();
		String name = "";

		while (enu.hasMoreElements())
		{
			CaptureDeviceInfo info = enu.nextElement();

			if (info != null)
			{
				name = info.getName();

				if (name.startsWith("vfw:"))
				{
					videoList.add(info);
				} else
				{
					otherList.add(info);
				}
			}
		}
	}

	
	/**
	 * Get all video devices.
	 * @param
	 * @return video device list
	 */
	public Vector<CaptureDeviceInfo> getVideoDevices()
	{
		return videoList;
	}

	
	/**
	 * Gets all 'other' media devices.
	 * @return OtherDevices
	 */
	public Vector<CaptureDeviceInfo> getOtherDevices()
	{
		return otherList;
	}
	
	
	/**
	 * Get all audio and video devices connected to the current computer
	 * @return Vector if devices
	 */
	public Vector<CaptureDeviceInfo> getAllDevices()
	{
		return deviceList;
	}
	
	
	/**
	 * Clear all the current device lists and refresh them with the current devices connected to the 
	 * current computer.
	 */
	public void refreshDeviceList()
	{
		deviceList.clear();
		videoList.clear();
		otherList.clear();
		getDevices(null);
		sortDevices();
	}
	

	
	/**
	 * This function is purely here for debugging purposes.
	 * @param Input argument string array
	 */
	public static void main(String[] args)
	{
		new InputDeviceManager();
	}
}
