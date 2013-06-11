package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import net.amcintosh.codian.Constants;

import org.apache.log4j.Logger;

/**
 * @author Andrew McIntosh
 */
public class DBUtil {

	private static Logger log = Logger.getLogger(DBUtil.class.getName());

	/**
	 * 
	 * @return
	 */
	public static HashMap<String, Object> getDeviceProperties() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Connection con = null;

		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			String query = "SELECT property, value FROM device_properties;";

			ResultSet resultSet = stat.executeQuery(query);
			while (resultSet.next()) {
				String propertyName = resultSet.getString("property");
				if ("restartTime".equals(propertyName)) {
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.CODIAN_DATE_FORMAT);
					Date restartTime = sdf.parse(resultSet.getString("value"));
					data.put(propertyName, restartTime);
				} else if ("totalVideoPorts".equals(propertyName)
						|| "totalAudioOnlyPorts".equals(propertyName)
						|| "isdnPorts".equals(propertyName)) {
					data.put(propertyName, resultSet.getInt("value"));
				} else {
					data.put(propertyName, resultSet.getObject("value"));
				}
			}
		} catch (SQLException e) {
			log.error("getDeviceProperties",e);
		} catch (ParseException e) {
			log.error("getDeviceProperties (restartTime)",e);
		}
		return data;
	}

	
	/**
	 * 
	 * @param tableName
	 * @param params
	 * @return
	 */
	protected static String createInsertFromParameters(String tableName, HashMap<String, Object> params) {
		
		String fields = "";
		String values = "";
		Iterator<String> iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			if (!"authenticationUser".equals(key) && !"authenticationPassword".equals(key)) {
				Object value = params.get(key);
				fields = fields + key + (iter.hasNext() ? "," : "");
				if (key.equals("startTime")) {
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT);
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  
					values = values + "\"" + sdf.format(value) + "\"" + (iter.hasNext() ? "," : "");
				} else {
					values = values + "\"" + value + "\"" + (iter.hasNext() ? "," : "");
				}
			}
		}
		String insert = "INSERT INTO " + tableName + "(" + fields + ") " + "VALUES ("+values+");";
		if (log.isDebugEnabled()) {
			log.debug(insert);
		}
		return insert;
	}

}
