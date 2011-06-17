package connection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Holds various static variables that change very rarely for the connection protocols and usage.
 * @author Caleb Shortt
 */
public class ConnectionStatics
{
	/**
	 * Defines the RTP protocol string used to define a RTP URL
	 */
	public static final String RTP_PROTOCOL= "rtp://";
	
	/**
	 * Destination port that the system will stream to
	 */
	public static final int DEST_PORT = 8000;
	
	/**
	 * Describes the video content type string
	 */
	public static final String VIDEO_CONTENT_TYPE = "video";
	
	/**
	 * Describes the audio content type string
	 */
	public static final String AUDIO_CONTENT_TYPE = "audio";
	
	
	private static byte[] IP_ADDRESS = null;
	private static String HOST_NAME = null;
	/**
	 * Gets the IP address of the destination to stream media to.
	 * @return IP_Address
	 */
	public static byte[] getADDRESS()
	{
		if(IP_ADDRESS.length <= 0 || IP_ADDRESS == null)
		{
			try {
			    InetAddress addr = InetAddress.getLocalHost();
			    IP_ADDRESS = addr.getAddress();
			} catch (UnknownHostException e) {
				System.err.println("The system was unable to extract the IP address.");
				e.printStackTrace();
			}
		}
		return IP_ADDRESS;
	}
	
	/**
	 * Gets the host name of the destination to stream media to.
	 * @return Host_Name
	 */
	public static String getHostName()
	{
		if(HOST_NAME == null || HOST_NAME.equalsIgnoreCase(""))
		{
			try {
			    InetAddress addr = InetAddress.getLocalHost();
			    HOST_NAME = addr.getHostName();
			} catch (UnknownHostException e) {
				System.err.println("The system was unable to extract the IP address.");
				e.printStackTrace();
			}
		}
		return HOST_NAME;
	}
}
