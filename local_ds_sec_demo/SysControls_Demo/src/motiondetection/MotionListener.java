package motiondetection;

import javax.swing.event.EventListenerList;


/**
 * Listener manager for motion events. Handles adding and removing listeners for 
 * the MotionEvent event.
 * @author Caleb Shortt
 */
public class MotionListener
{
	/**
	 * List of listeners who will be notified when a MotionEvent has occurred.
	 */
	protected static EventListenerList listeners = new EventListenerList();
	
	
	public MotionListener()
	{
	}
	
	
	/**
	 * Adds a listener class to the list of listeners who will be notified when a MotionEvent 
	 * occurs. The listener class must implement the MotionListenerInterface interface to be 
	 * considered a compatible listener.
	 * @param listener (MotionListenerInterface)
	 * @return void
	 */
	public static void addMotionListener(MotionListenerInterface listener)
	{
		listeners.add(MotionListenerInterface.class, listener);
	}
	
	
	/**
	 * Removes the given listener from the list of classes notified when a MotionEvent 
	 * occurs. 
	 * @param listener (MotionListenerInterface)
	 * @return void
	 */
	public static void removeMotionListener(MotionListenerInterface listener)
	{
		listeners.remove(MotionListenerInterface.class, listener);
	}
	
	
	/**
	 * Fires a MotionEvent event and notifies all listeners of the event.
	 * @param event (MotionEvent)
	 * @return void
	 */
	public static void fireMotionEvent(MotionEvent evt)
	{
		Object[] listenerList = listeners.getListenerList();
		// Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
		for(int i=0; i<listenerList.length; i+=2)
		{
			if(listenerList[i] == MotionListenerInterface.class)
			{
				((MotionListenerInterface) listenerList[i+1]).motion(evt);
			}
		}
	}
}
