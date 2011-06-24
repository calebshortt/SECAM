package connection;


/**
 * A manager for a WebConnectionManager singleton.
 * @author Caleb Shortt
 */
public class WebConnSingleton {
	
	private static WebConnectionManager manager = null;
	
	/**
	 * Returns the instance of the WebConnectionManager singleton
	 * @return The connection manager singleton instance
	 */
	public static WebConnectionManager getInstance() {
		if(manager == null) {
			manager = new WebConnectionManager();
		}
		return manager;
	}
}
