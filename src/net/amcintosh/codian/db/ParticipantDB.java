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
public class ParticipantDB {

	private static Logger log = LoggerFactory.getLogger(ParticipantDB.class);
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static boolean insertParticipant(HashMap<String, Object> params) throws SQLException {
		Connection con = null;

		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			/* 
			 * TODO: This should probably be rewritten with a prepared statement,
			 * but since this is internal dev/testing use only, security is lower priority.   
			 */
			String insert = DBUtil.createInsertFromParameters("participant",params);		
			stat.executeUpdate(insert);
			con.commit();
		} catch (SQLException e) {
			log.error("insertParticipant",e);
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
	public static boolean deleteParticipants(String conferenceName) {
		Connection con = null;
		if (log.isDebugEnabled()) {
			log.debug("deleteParticipants");
		}
		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);
			
			String delete = "DELETE FROM participant WHERE conferenceName = ?";
			PreparedStatement stat = con.prepareStatement(delete);
			stat.setString(1, conferenceName);				
			int status = stat.executeUpdate();
			con.commit();
			return status==1 ? true : false;
		} catch (SQLException e) {
			log.error("deleteParticipants",e);
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
	 * Return data from conference for conference.enumerate operation.
	 * 
	 * @param enumerateFilter
	 * @param minId
	 * @return
	 */
	public static List<HashMap<String,Object>> getParticipantsForEnumerate(String enumerateFilter, int minId, 
			boolean currentState, boolean configuredState) {
		ArrayList<HashMap<String,Object>> participants = new ArrayList<HashMap<String,Object>>();
		Connection con = null;
		Statement stat = null;

		String query = "SELECT p.*, c.uniqueId AS 'connectionUniqueId' FROM participant p, conference c WHERE p.conferenceName = c.conferenceName ";
		
		if (minId > 0) {
			query += " AND autoAttendantUniqueID > " + minId;
		}
		//TODO: Clauses
		if (enumerateFilter != null) {
			if (enumerateFilter.contains("!connected")) {

			} else if (enumerateFilter.contains("connected")) {

			}
			if (enumerateFilter.contains("!disconnected")) {

			} else if (enumerateFilter.contains("disconnected")) {

			}
		}
			
		try {
			con = DBManager.getInstance().getConnection();
			
			stat = con.createStatement();
			if (log.isDebugEnabled()) {
				log.debug("getParticipantsForEnumerate: " + query);
			}
			
			ResultSet resultSet = stat.executeQuery(query);

			int numResults = 0;
			
			while (resultSet.next() && numResults < Constants.PARTICIPANT_ENUMERATE_MAXRESULTS) {
				HashMap<String,Object> participant = new HashMap<String,Object>();
				
				participant.put("conferenceName", resultSet.getString("conferenceName"));
				participant.put("participantName", resultSet.getString("participantName"));
				participant.put("participantProtocol", resultSet.getString("participantProtocol"));
				participant.put("participantType", resultSet.getString("participantType"));
				participant.put("connectionUniqueId", resultSet.getInt("connectionUniqueId"));
				
				if (currentState) {
					HashMap<String,Object> currentStateData = new HashMap<String,Object>();
					currentStateData.put("address", resultSet.getString("address"));
					//currentStateData.put("gatewayAddress", "");
					//currentStateData.put("ipAddress",);
					currentStateData.put("displayName", "");
					currentStateData.put("guest", resultSet.getBoolean("addAsGuest"));
					//currentStateData.put("remoteLinkType", "");
					currentStateData.put("displayNameOverrideStatus", resultSet.getBoolean("displayNameOverrideStatus"));
					currentStateData.put("maxBitRateToMCU", resultSet.getInt("maxBitRateToMCU"));
					currentStateData.put("maxBitRateFromMCU", resultSet.getInt("maxBitRateFromMCU"));
					currentStateData.put("motionSharpnessTradeoff", resultSet.getString("motionSharpnessTradeoff"));
					
					
					
					participant.put("currentState", currentStateData);
				}
				
				numResults++;
				participants.add(participant);
			}
		} catch (SQLException e) {
			log.error("getParticipantsForEnumerate",e);
		} finally {
			if (con!=null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	
		return participants;
	}
}
