package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Andrew McIntosh
 */
public class ConferenceDB {

	private static Logger log = Logger.getLogger(ConferenceDB.class.getName());
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public static boolean insertConference(HashMap<String, Object> params) throws SQLException {
		Connection con = null;

		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			String insert = DBUtil.createInsertFromParameters("conference",params);		
			stat.executeUpdate(insert);
			con.commit();
		} catch (SQLException e) {
			log.error("insertConference",e);
			throw e;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		return true;
	}


	public static List<HashMap<String,Object>> getConferences(boolean active, boolean completed) {
		ArrayList<HashMap<String,Object>> conferences = new ArrayList<HashMap<String,Object>>();
		Connection con = null;

		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			String query = "SELECT * FROM conference WHERE 1=1";
			
			if (completed) {
				query += " AND startTime < DATETIME('now')";
			}
			
			ResultSet resultSet = stat.executeQuery(query);
			
			while (resultSet.next()) {
				HashMap<String,Object> conference = new HashMap<String,Object>();
				conference.put("conferenceName", resultSet.getString("conferenceName"));
				conference.put("conferenceType", resultSet.getString("conferenceType"));
				conference.put("uniqueId", resultSet.getInt("uniqueId"));
				conference.put("private", resultSet.getBoolean("private"));
				conference.put("joinAudioMuted", resultSet.getBoolean("joinAudioMuted"));
				conference.put("joinVideoMuted", resultSet.getBoolean("joinVideoMuted"));
				conference.put("enforceMaximumAudioPorts", resultSet.getBoolean("enforceMaximumAudioPorts"));
				conference.put("enforceMaximumVideoPorts", resultSet.getBoolean("enforceMaximumVideoPorts"));
				conference.put("templateName", resultSet.getString("templateName"));
				conference.put("templateNumber", resultSet.getInt("templateNumber"));
				conference.put("numericId", resultSet.getString("numericId"));
				conference.put("guestNumericId", resultSet.getString("guestNumericId"));
				conference.put("registerWithGatekeeper", resultSet.getBoolean("registerWithGatekeeper"));
				conference.put("registerWithSIPRegistrar", resultSet.getBoolean("registerWithSIPRegistrar"));
				conference.put("startTime", resultSet.getDate("startTime"));
				conference.put("pin", resultSet.getString("pin"));
				conference.put("guestPin", resultSet.getString("guestPin"));
				conference.put("description", resultSet.getString("description"));
				conference.put("startLocked", resultSet.getBoolean("startLocked"));
				conference.put("durationSeconds", resultSet.getInt("durationSeconds"));
				conference.put("conferenceMeEnabled", resultSet.getBoolean("conferenceMeEnabled"));
				conference.put("automaticLectureMode", resultSet.getString("automaticLectureMode"));
				conference.put("automaticLectureModeEnabled", resultSet.getBoolean("automaticLectureModeEnabled"));
				conference.put("automaticLectureModeTimeout", resultSet.getInt("automaticLectureModeTimeout"));
				conference.put("multicastStreamingEnabled", resultSet.getBoolean("multicastStreamingEnabled"));		
				conference.put("unicastStreamingEnabled", resultSet.getBoolean("unicastStreamingEnabled"));
				conference.put("contentMode", resultSet.getString("contentMode"));
				conference.put("h239Enabled", resultSet.getBoolean("h239Enabled"));
				conference.put("lastChairmanLeavesDisconnect", resultSet.getBoolean("lastChairmanLeavesDisconnect"));
				conference.put("cleanupTimeout", resultSet.getInt("cleanupTimeout"));
				conference.put("preconfiguredParticipantsDefer", resultSet.getBoolean("preconfiguredParticipantsDefer"));
				conference.put("maximumAudioPorts", resultSet.getInt("maximumAudioPorts"));
				conference.put("maximumVideoPorts", resultSet.getInt("maximumVideoPorts"));
				conference.put("reservedAudioPorts", resultSet.getInt("reservedAudioPorts"));
				conference.put("reservedVideoPorts", resultSet.getInt("reservedVideoPorts"));
				conference.put("contentTxCodec", resultSet.getString("contentTxCodec"));
				conference.put("contentTxMinimumBitRate", resultSet.getString("contentTxMinimumBitRate"));
				conference.put("repetition", resultSet.getString("repetition"));
				conference.put("weekDay", resultSet.getString("weekDay"));
				conference.put("whichWeek", resultSet.getString("whichWeek"));
				conference.put("weekDays", resultSet.getString("weekDays"));
				conference.put("terminationType", resultSet.getString("terminationType"));
				conference.put("numberOfRepeats", resultSet.getInt("numberOfRepeats"));
				conference.put("customLayoutEnabled", resultSet.getBoolean("customLayoutEnabled"));
				conference.put("layoutControlEx", resultSet.getString("layoutControlEx"));
				conference.put("cameraControl", resultSet.getString("cameraControl"));
				conference.put("newParticipantsCustomLayout", resultSet.getBoolean("newParticipantsCustomLayout"));
				conference.put("customLayout", resultSet.getInt("customLayout"));
				conference.put("chairControl", resultSet.getString("chairControl"));
				conference.put("suppressDtmfEx", resultSet.getString("suppressDtmfEx"));
				conference.put("inCallMenuControlChair", resultSet.getString("inCallMenuControlChair"));
				conference.put("inCallMenuControlGuest", resultSet.getString("inCallMenuControlGuest"));
				conference.put("encryptionRequired", resultSet.getInt("encryptionRequired"));
				conference.put("contentContribution", resultSet.getInt("contentContribution"));
				conference.put("contentTransmitResolutions", resultSet.getString("contentTransmitResolutions"));

				conferences.add(conference);
			}
		} catch (SQLException e) {
			log.error("getConferences",e);
		} 

		
		return conferences;
	}
	
	
}
