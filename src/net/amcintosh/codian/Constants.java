package net.amcintosh.codian;

/**
 * @author Andrew McIntosh
 */
public class Constants {

	public static final String CODIAN_DATE_FORMAT = "yyyyMMdd'T'HH:mm:ss";
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	
	public static final int CONFERENCE_ENUMERATE_MAXRESULTS = 
			Integer.parseInt(Config.getConfig().getProperty("conference.enumerate.maxResults"));
	
}
