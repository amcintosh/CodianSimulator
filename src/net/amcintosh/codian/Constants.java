package net.amcintosh.codian;

/**
 * @author Andrew McIntosh
 */
public class Constants {
	public static final String API_VERSION = "2.9";
	
	public static final String CODIAN_DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss";
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final int CONFERENCE_ENUMERATE_MAXRESULTS = 
			Integer.parseInt(Config.getConfig().getProperty("conference.enumerate.maxResults"));

	public static final int PARTICIPANT_ENUMERATE_MAXRESULTS = 
			Integer.parseInt(Config.getConfig().getProperty("participant.enumerate.maxResults"));

	public static final String STATUS_KEY = "status";

	public static final String SUCCESS_MESSAGE = "operation successful";
}
