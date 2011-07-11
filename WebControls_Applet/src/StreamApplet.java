
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Panel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.SizeChangeEvent;
import javax.media.protocol.DataSource;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionManager;
import javax.media.rtp.SessionManagerException;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.RemotePayloadChangeEvent;
import javax.media.rtp.rtcp.SourceDescription;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sun.media.rtp.RTPSessionMgr;
import com.sun.media.ui.PlayerWindow;


/**
 * Stream display applet. Heavily used examples from Java forums and examples.
 * @author Caleb Shortt
 */
@SuppressWarnings("deprecation")
public class StreamApplet extends Applet implements ReceiveStreamListener, ControllerListener
{
	private static final long serialVersionUID = 1L;
	
	int width = 320;
	int height = 0;
	
	SessionManager videoSession = null;
	Player videoPlayer = null;
	
	//JScrollPane consoleContainer = null;
	//JTextArea console = null;
	//boolean consoleInit = false;
	
	/**
	 * The address where the stream will be located
	 */
	InetAddress inetAddress = null;
	
	Component visualComponent = null;
	Panel panel = null;
	
	PlayerWindow window = null;
	
	
	public void init()
	{
		this.setLayout( new BorderLayout() );
		
		//console = new JTextArea();
		//console.setEditable(false);
		
		//consoleContainer = new JScrollPane(console);
		
		
		//str_input = getParameter("video");
		//if(str_input.equalsIgnoreCase("On"))
		//{
		//	str_address = getParameter("videosession");
		//	str_port = getParameter("videoport");
		
		//CreateSession("127.0.0.1", 22222, "video");
		CreateSession("localhost", 22222, "video");
		//if(videoSession == null)
		//	return;
		
		if (videoSession == null)
		{
			System.err.println("An error occurred while trying to initialize videoSession (is null)");
			return;
		}
	}
	
	
	public void start()
	{
		if(videoPlayer != null)
		{
			videoPlayer.start();
			new PlayerWindow(videoPlayer);
		}
		else
		{
			System.err.println("Could not start applet: videoPlayer is null");
		}
			
	}
	
	
	public void stop()
	{
		if(videoPlayer != null)
		{
			videoPlayer.close();
		}
	}
	
	
	public void destroy()
	{
		System.err.println("Disposing of applet");
		videoSession.closeSession("");
		videoSession = null;
		videoPlayer = null;
		super.destroy();
	}
	
	
	@SuppressWarnings("unused")
	private int StrToInt(String str)
	{
		if (str == null)
			return -1;
		Integer retint = new Integer(str);
		return retint.intValue();
	}
	
	private SessionManager CreateSession(String addr, int port, String inp)
	{
		System.err.println("Creating Session. Parameters (addr=" + addr + ", port=" + port + ", inp=" + inp);
		SessionManager mgr = new RTPSessionMgr();
		
		if(inp.equalsIgnoreCase("video"))
			videoSession = mgr;
		
		if(mgr == null)
			return null;
		
		mgr.addReceiveStreamListener(this);
		
		String cname = mgr.generateCNAME();
		String username = "jmf-user";
		
		SessionAddress localaddr = new SessionAddress();
		
		try
		{
			//inetAddress = InetAddress.getByName(addr);
			inetAddress = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e)
		{
			System.err.println("inetaddress " + e.getMessage());
			e.printStackTrace();
		}
		
		SessionAddress sessaddr = new SessionAddress(inetAddress, port, inetAddress, port+1);
		
		// TODO
		//JOptionPane.showMessageDialog(null, "session address = " + sessaddr);			// <--- DEBUG
		
		SourceDescription[] userdesclist = new SourceDescription[4];
		userdesclist[0] = new SourceDescription(SourceDescription.SOURCE_DESC_EMAIL, "", 1, false);
		userdesclist[1] = new SourceDescription(SourceDescription.SOURCE_DESC_NAME, username, 1, false);
		userdesclist[2] = new SourceDescription(SourceDescription.SOURCE_DESC_CNAME, cname, 1, false);
		userdesclist[3] = new SourceDescription(SourceDescription.SOURCE_DESC_TOOL, "JMF RTP Player", 1, false);
		
		try
		{
			mgr.initSession(localaddr, mgr.generateSSRC(), userdesclist, 0.05, 0.25);
			mgr.startSession(sessaddr, 1, null);
		} 
		catch (SessionManagerException e)
		{
			e.printStackTrace();
			return null;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		//JOptionPane.showMessageDialog(null, "mrg = " + mgr);			// <--- DEBUG
		return mgr;
	}

	
	public void update(ReceiveStreamEvent event)
	{
		
		//console.append("--- Update Reached ---\n");
		
		//JOptionPane.showMessageDialog(null, event.getClass());			// <--- DEBUG
		
		SessionManager source = (SessionManager)event.getSource();
		
		Player newPlayer = null;
		
		if(event instanceof NewReceiveStreamEvent)
		{
			
			//JOptionPane.showMessageDialog(null, "NewReceiveStreamEvent");			// <--- DEBUG
			//console.append("\n---------------------------\n");
			//console.append("--- NewReceiveStreamEvent ---\n");
			
			try
			{
				ReceiveStream stream = ((NewReceiveStreamEvent) event).getReceiveStream();
				DataSource dsource = stream.getDataSource();
				newPlayer = Manager.createPlayer(dsource);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if(newPlayer == null)
			{
				return;
			}
			
			//console.append("\n---------------------------\n");
			//console.append("--- Checking Source [update()] ---\n");
			
			if(source == videoSession)
			{
				//JOptionPane.showMessageDialog(null, "source == videoSession [update]");			// <--- DEBUG
				
				//console.append("--- Source == videoSession [update()] ---\n");
				
				if(videoPlayer == null)
				{
					videoPlayer = newPlayer;
					newPlayer.addControllerListener(this);
					newPlayer.start();
				}
				else 
				{
					window = new PlayerWindow(newPlayer);
				}
			}
			else
			{
				//console.append("\n---------------------------\n");
				//console.append("--- Source != videoSession ---\n");
				//console.append("" + source + "\n");
			}
		}
		else if(event instanceof RemotePayloadChangeEvent)
		{
			//console.append("\n---------------------------\n");
			//console.append("--- RemotePayloadChangeEvent ---\n");
		}
	}


	public synchronized void controllerUpdate(ControllerEvent event)
	{
		
		//JOptionPane.showMessageDialog(null, "Controller event of type: " + event.getClass());			// <--- DEBUG
		//console.append("\n---------------------------\n");
		//console.append("Controller event of type: " + event.getClass());
		
		
		
		Player player = null;
		Controller controller = (Controller)event.getSource();
		
		if(controller instanceof Player)
			player = (Player)event.getSource();
		
		if(player == null)
			return;
		
		if(event instanceof RealizeCompleteEvent)
		{
			//console.append("\n---------------------------\n");
			//console.append("--- Realization Event Occurred ---\n");
			
			if( (visualComponent = player.getVisualComponent()) != null )
			{
				width = visualComponent.getPreferredSize().width;
				height = visualComponent.getPreferredSize().height;
				
				if(panel == null)
				{
					panel = new Panel();
					positionPanel(width, height);
					panel.setLayout(new BorderLayout());
				}
				panel.add("Center", visualComponent);
				panel.validate();
			}
			
			if(panel != null)
			{
				this.add("Center", panel);
				this.invalidate();
			}
		}
		
		if(event instanceof SizeChangeEvent)
		{
			if(panel != null)
			{
				SizeChangeEvent sce = (SizeChangeEvent)event;
				int nWidth = sce.getWidth();
				int nHeight = sce.getHeight();
				positionPanel(nWidth, nHeight);
			}
		}
		//if(panel != null && !consoleInit)
		//{
			//height += consoleContainer.getPreferredSize().height;
			//panel.add("South", consoleContainer);
		//}
		this.validate();
	}
	
	
	public void positionPanel(int w, int h)
	{
		panel.setBounds(0, 0, w, h);
		panel.validate();
	}
}








