package connection;

/**
 * A manager for a DatabaseConnectionManager singleton.
 * @author Caleb Shortt
 */
public class DBConnSingleton {
	
	private static DatabaseConnectionManager manager = null;
	
	/**
	 * Returns the instance of the DatabaseConnectionManager singleton
	 * @return The connection manager singleton instance
	 */
	public static DatabaseConnectionManager getInstance() {
		if(manager == null) {
			manager = new DatabaseConnectionManager();
			manager.connect();
		}
		return manager;
	}
}