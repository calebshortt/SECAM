package motiondetection;

import java.awt.AWTEvent;
import java.util.EventObject;

/**
 * Basic motion event. Signifies when motion has been detected in the motion detector.
 * @author Caleb Shortt
 */
public class MotionEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FLAG_CHANGED = AWTEvent.RESERVED_ID_MAX + 1;
	public MotionEvent(Object source) {
		super(source);
	}
}
