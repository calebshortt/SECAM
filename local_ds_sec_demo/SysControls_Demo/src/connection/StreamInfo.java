package connection;

import javax.media.Format;

/**
 * Contains information about a media stream.
 * @author Caleb Shortt
 */
public class StreamInfo {
	
	int user_id = -1;
	int computer_id = -1;
	String start_date = "";
	String end_date = "";
	Format format = null;
	
	
	// DATES MUST BE IN THE FORM YYYY-MM-DD HH:mm:SS
	/**
	 * The main constructor and the only way to initialise the StreamInfo class. This information will be used 
	 * to populate the columns in the database tables.
	 * @param user_id (int)
	 * @param computer_id (int)
	 * @param start_date (String)
	 * @param end_date (String)
	 * @param format (Format)
	 */
	public StreamInfo(int user_id, int computer_id, String start_date, String end_date, Format format) {
		this.user_id = user_id;
		this.computer_id = computer_id;
		this.end_date = end_date;
		this.start_date = start_date;
		this.format = format;
	}
	
	
	public int getUserID() {
		return user_id;
	}
	
	public int getCompID() {
		return computer_id;
	}
	
	
	/**
	 * Returns a date in the form: yyyy-MM-dd hh:mm:ss
	 */
	public String getStartDate() {
		return start_date;
	}
	
	
	/**
	 * Returns a date in the form: yyyy-MM-dd hh:mm:ss
	 */
	public String getEndDate() {
		return end_date;
	}
	
	public Format getFormat() {
		return format;
	}
	
	public void setFormat(Format format) {
		this.format = format;
	}
}
