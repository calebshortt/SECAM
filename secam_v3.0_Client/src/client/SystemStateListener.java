package client;

import javax.swing.event.EventListenerList;
import client.StateChangeEvent;
import client.SystemStateChangeInterface;

/**
 * Listener class for the System State. Holds a list of all the listening classes and, 
 * when a StateChangeEvent occurs, it notifies all the listeners in the list.
 * @author Caleb Shortt
 */
public class SystemStateListener
{
	/**
	 * List of listeners who will be notified when a StateChangeEvent has occurred.
	 */
	protected static EventListenerList listeners = new EventListenerList();
	
	public SystemStateListener() { }
	
	
	/**
	 * Adds a class to the list of listeners that will be notified when the state change event occurs.
	 * @param listener
	 */
	public static void addStateListener(SystemStateChangeInterface listener)
	{
		listeners.add(SystemStateChangeInterface.class, listener);
	}
	
	/**
	 * Removes a given class from the list of listeners that will be notified when a state change occurs.
	 * @param listener
	 */
	public static void removeStateListener(SystemStateChangeInterface listener)
	{
		listeners.remove(SystemStateChangeInterface.class, listener);
	}
	
	/**
	 * fires a given event and notify all of the listeners in the listeners list.
	 * @param StateChangeEvent
	 */
	public static void fireStateEvent(StateChangeEvent sce)
	{
		Object[] listenerList = listeners.getListenerList();
		// Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
		for(int i=0; i<listenerList.length; i+=2)
		{
			if(listenerList[i] == SystemStateChangeInterface.class)
			{
				((SystemStateChangeInterface) listenerList[i+1]).stateChanged(sce);
			}
		}
	}
}
