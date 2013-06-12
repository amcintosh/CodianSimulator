package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import net.amcintosh.codian.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew McIntosh
 */
public class ConferenceDB {
	private static Logger log = LoggerFactory.getLogger(ConferenceDB.class);
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public static boolean insertConference(HashMap<String, Object> params) throws SQLException {
		if (log.isDebugEnabled()) {
			log.debug("insertConference: " + params);
		}
		
		Connection con = null;
		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			/* 
			 * TODO: This should probably be rewritten with a prepared statement,
			 * but since this is internal dev/testing use only, security is lower priority.   
			 */
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

	/**
	 * 
	 * @param conferenceName
	 * @return
	 * @throws SQLException
	 */
	public static boolean deleteConference(String conferenceName) {
		Connection con = null;
		if (log.isDebugEnabled()) {
			log.debug("deleteConference: " + conferenceName);
		}
		try {
			ParticipantDB.deleteParticipants(conferenceName);
			
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			String delete = "DELETE FROM conference WHERE conferenceName = ?";
			PreparedStatement stat = con.prepareStatement(delete);
			stat.setString(1, conferenceName);				
			int status = stat.executeUpdate();
			con.commit();
			return status==1 ? true : false;
		} catch (SQLException e) {
			log.error("deleteConference",e);
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public static boolean getConferenceExists(String conferenceName) {
		Connection con = null;
		if (log.isDebugEnabled()) {
			log.debug("getConferenceExists: " + conferenceName);
		}
		try {
			con = DBManager.getInstance().getConnection();

			String query = "SELECT uniqueId FROM conference WHERE conferenceName = ?";
			PreparedStatement stat = con.prepareStatement(query);
			stat.setString(1, conferenceName);				
			ResultSet res = stat.executeQuery();
			boolean success = res.isBeforeFirst();
			stat.close();
			return success;
		} catch (SQLException e) {
			log.error("deleteConference",e);
			return false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<HashMap<String,Object>> getConferences() {
		return getConferences(null);
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */	
	public static List<HashMap<String,Object>> getConferences(int id) {
		String query = null;
		if (id > 0) {
			query += " AND uniqueId = " + id;
		}
		return getConferences(query);
	}

	
	/**
	 * 
	 * @param queryClause
	 * @return
	 */
	public static List<HashMap<String,Object>> getConferences(String queryClause) {
		ArrayList<HashMap<String,Object>> conferences = new ArrayList<HashMap<String,Object>>();
		Connection con = null;
		Statement stat = null;

		String query = "SELECT * FROM conference WHERE 1=1 " + queryClause;
		try {
			con = DBManager.getInstance().getConnection();
			
			stat = con.createStatement();
			if (log.isDebugEnabled()) {
				log.debug("getConferences: " + query);
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

				conferences.add(conference);
			}
		} catch (SQLException e) {
			log.error("getConferences",e);
		} finally {
			if (con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	
		return conferences;
	}
	
	
	/**
	 * Return data from conference for conference.enumerate operation.
	 * 
	 * @param enumerateFilter
	 * @param minId
	 * @return
	 */
	public static List<HashMap<String,Object>> getConferencesForEnumerate(String enumerateFilter, int minId) {
		ArrayList<HashMap<String,Object>> conferences = new ArrayList<HashMap<String,Object>>();
		Connection con = null;
		Statement stat = null;

		String query = "SELECT * FROM conference WHERE 1=1 ";
		if (minId > 0) {
			query += " AND uniqueId > " + minId;
		}
		if (enumerateFilter!=null) {
			if (enumerateFilter.contains("!completed")) {
				query += " AND DATETIME(strftime('%s', startTime) + durationSeconds, 'unixepoch') > DATETIME('now')";
			} else if (enumerateFilter.contains("completed")) {
				query += " AND DATETIME(strftime('%s', startTime) + durationSeconds, 'unixepoch') < DATETIME('now')";
			}
		
			if (enumerateFilter.contains("!active")) {
				query += " AND (startTime > DATETIME('now')";
				query += " OR DATETIME(strftime('%s', startTime) + durationSeconds, 'unixepoch') < DATETIME('now'))";
			} else if (enumerateFilter.contains("active")) {
				query += " AND startTime < DATETIME('now')";
				query += " AND DATETIME(strftime('%s', startTime) + durationSeconds, 'unixepoch') > DATETIME('now')";			
			}
		}
			
		try {
			con = DBManager.getInstance().getConnection();
			
			stat = con.createStatement();
			if (log.isDebugEnabled()) {
				log.debug("getConferencesForEnumerate: " + query);
			}
			
			ResultSet resultSet = stat.executeQuery(query);

			int numResults = 0;
			
			while (resultSet.next() && numResults < Constants.CONFERENCE_ENUMERATE_MAXRESULTS) {
				HashMap<String,Object> conference = new HashMap<String,Object>();
				
				boolean active = false;
				Date startTime = null;
				Date endTime = null;
				int durationSeconds = resultSet.getInt("durationSeconds");
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT);
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					startTime = sdf.parse(resultSet.getString("startTime"));
					endTime = new Date(startTime.getTime() + (durationSeconds*1000) );
					Date now = new Date();
					if (now.after(startTime) && (now.before(endTime) || durationSeconds==0)) { 
						active = true;
					}
				} catch (ParseException e) {
					log.error("getConferencesForEnumerate: ",e);
				}

				
				conference.put("conferenceName", resultSet.getString("conferenceName"));
				conference.put("conferenceType", resultSet.getString("conferenceType"));
				conference.put("uniqueId", resultSet.getInt("uniqueId"));
				conference.put("conferenceActive", active);
				conference.put("description", resultSet.getString("description"));
				conference.put("pin", resultSet.getString("pin")==null ? "" : resultSet.getString("pin"));
				conference.put("guestPin", resultSet.getString("guestPin")==null ? "" : resultSet.getString("guestPin"));
				conference.put("numericId", resultSet.getString("numericId")==null ? "" : resultSet.getString("numericId"));
				conference.put("guestNumericId", resultSet.getString("guestNumericId")==null ? "" : resultSet.getString("guestNumericId"));
				conference.put("registerWithGatekeeper", resultSet.getBoolean("registerWithGatekeeper"));
				conference.put("registerWithSIPRegistrar", resultSet.getBoolean("registerWithSIPRegistrar"));
				conference.put("multicastStreamingEnabled", resultSet.getBoolean("multicastStreamingEnabled"));		
				conference.put("unicastStreamingEnabled", resultSet.getBoolean("unicastStreamingEnabled"));
				conference.put("conferenceMeEnabled", resultSet.getBoolean("conferenceMeEnabled"));
				conference.put("contentMode", resultSet.getString("contentMode"));
				conference.put("h239Enabled", resultSet.getBoolean("h239Enabled")); //Deprecated by contentMode.
				conference.put("contentImportant", resultSet.getBoolean("contentImportant"));
				conference.put("h239Important", resultSet.getBoolean("contentImportant")); //Consider this setting deprecated by contentImportant
				conference.put("contentTxCodec", resultSet.getString("contentTxCodec"));
				conference.put("contentTxMinimumBitRate", resultSet.getString("contentTxMinimumBitRate"));
				conference.put("lastChairmanLeavesDisconnect", resultSet.getBoolean("lastChairmanLeavesDisconnect"));
				conference.put("preconfiguredParticipantsDefer", resultSet.getBoolean("preconfiguredParticipantsDefer"));
				conference.put("startLocked", resultSet.getBoolean("startLocked"));
				conference.put("locked", resultSet.getBoolean("startLocked")); //TODO: examine on create
				conference.put("maximumAudioPorts", resultSet.getInt("maximumAudioPorts"));
				conference.put("maximumVideoPorts", resultSet.getInt("maximumVideoPorts"));
				conference.put("reservedAudioPorts", resultSet.getInt("reservedAudioPorts"));
				conference.put("reservedVideoPorts", resultSet.getInt("reservedVideoPorts"));
				conference.put("customLayoutEnabled", resultSet.getBoolean("customLayoutEnabled"));
				conference.put("customLayout", resultSet.getInt("customLayout"));
				conference.put("private", resultSet.getBoolean("private"));
				conference.put("chairControl", resultSet.getString("chairControl"));
				conference.put("suppressDtmfEx", resultSet.getString("suppressDtmfEx"));
				conference.put("layoutControlEx", resultSet.getString("layoutControlEx"));
				conference.put("layoutControlEnabled", "disabled".equals(resultSet.getString("layoutControlEx")) ? false : true);
				conference.put("joinAudioMuted", resultSet.getBoolean("joinAudioMuted"));
				conference.put("joinVideoMuted", resultSet.getBoolean("joinVideoMuted"));
				conference.put("cameraControl", resultSet.getString("cameraControl"));
				conference.put("inCallMenuControlChair", resultSet.getString("inCallMenuControlChair"));
				conference.put("inCallMenuControlGuest", resultSet.getString("inCallMenuControlGuest"));
				conference.put("automaticLectureMode", resultSet.getString("automaticLectureMode"));
				conference.put("automaticLectureModeEnabled", resultSet.getBoolean("automaticLectureModeEnabled"));
				conference.put("automaticLectureModeTimeout", resultSet.getInt("automaticLectureModeTimeout"));
				conference.put("encryptionRequired", resultSet.getBoolean("encryptionRequired"));
				conference.put("contentContribution", resultSet.getBoolean("contentContribution"));
				/* 
				 * Note: contentTransmitResolutions and newParticipantsCustomLayout 
				 * are not documented in API spec for enumerate endpoint, but returns with actual codian.
				 */
				conference.put("contentTransmitResolutions", resultSet.getString("contentTransmitResolutions")); 
				conference.put("newParticipantsCustomLayout", resultSet.getBoolean("newParticipantsCustomLayout"));

				/*
				 *  Note: joinAGC and contentPassthroughLimit are not documented anywhere!
				 *  Both are returned by actual codian. 
				 */
				conference.put("joinAGC", false);
				conference.put("contentPassthroughLimit", "none");
				
				
				conference.put("floorStatus", resultSet.getString("floorStatus"));
				/* 
				 * TODO: floorStatus can be set by conference.floor.modify.
				 * When that endpoint is supported, floor status will have to be accounted for
				 * - floorParticipant struct
				 * - chairParticipant struct
				 */

				// Conditionally returned for scheduled conferences only: (Currently all)
				conference.put("startTime", startTime);
				conference.put("durationSeconds", durationSeconds);
				
				String repetition = resultSet.getString("repetition");
				conference.put("repetition", repetition);
				if (!"none".equals(repetition)) {
					conference.put("weekDay", resultSet.getString("weekDay"));
					conference.put("whichWeek", resultSet.getString("whichWeek"));
					conference.put("weekDays", resultSet.getString("weekDays"));
					conference.put("terminationType", resultSet.getString("terminationType"));

					SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATETIME_FORMAT);
					sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
					try {
						Date terminationDate = sdf.parse(resultSet.getString("terminationDate"));
						conference.put("terminationDate", terminationDate);
					} catch (ParseException e) {
						log.error("getConferencesForEnumerate", e);
					}
				}

				// Conditionally returned for active conferences only:
				if (active) {
					conference.put("activeStartTime", startTime);
					conference.put("activeEndTime", endTime);
					// TODO: activeConferenceId should eventually become its own sequence
					conference.put("activeConferenceId", resultSet.getInt("uniqueId")); 
				}
				 
				conferences.add(conference);
				numResults++;
			}
		} catch (SQLException e) {
			log.error("getConferencesForEnumerate",e);
		} finally {
			if (con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	
		return conferences;
	}
	
}