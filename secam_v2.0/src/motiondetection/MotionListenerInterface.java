package motiondetection;

import java.util.EventListener;

/**
 * Basic interface that every class must implement if it is to be added as a listener for the MotionEvent event.
 * @author Caleb Shortt
 */
public interface MotionListenerInterface extends EventListener {
	public void motion(MotionEvent me);
}
