package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.amcintosh.codian.Constants;

import org.apache.log4j.Logger;

/**
 * @author Andrew McIntosh
 */
public class ParticipantDB {

	private static Logger log = Logger.getLogger(ParticipantDB.class.getName());
	
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

	public static List<HashMap<String,Object>> getParticipants(int id) {
		String query = "";
		if (id > 0) {
			query += " AND autoAttendantUniqueID = " + id;
		}
		return getParticipants(query);
	}
	
	public static List<HashMap<String,Object>> getParticipants(String enumerateFilter, int minId) {
		String query = "";
		if (minId > 0) {
			query += " AND autoAttendantUniqueID > " + minId;
		}
		if (enumerateFilter != null) {
			if (enumerateFilter.contains("!connected")) {

			} else if (enumerateFilter.contains("connected")) {

			}
			if (enumerateFilter.contains("!disconnected")) {

			} else if (enumerateFilter.contains("disconnected")) {

			}
		}
		return getParticipants(query);
	}

	/**
	 * 
	 * @param active
	 * @param completed
	 * @param minId
	 * @return
	 */
	public static List<HashMap<String,Object>> getParticipants(String queryClause) { 
		ArrayList<HashMap<String,Object>> participants = new ArrayList<HashMap<String,Object>>();
		Connection con = null;
		Statement stat = null;
		
		try {
			con = DBManager.getInstance().getConnection();
			
			stat = con.createStatement();
			String query = "SELECT * FROM participant WHERE 1=1 " + queryClause;
			if (log.isTraceEnabled()) {
				log.trace(query);
			}
			
			ResultSet resultSet = stat.executeQuery(query);
			int numResults = 0;
			
			while (resultSet.next() && numResults < Constants.CONFERENCE_ENUMERATE_MAXRESULTS) {
				HashMap<String,Object> participant = new HashMap<String,Object>();
				participant.put("participantName", resultSet.getString("participantName"));
				participant.put("participantProtocol", resultSet.getString("participantProtocol"));
				participant.put("participantType", resultSet.getString("participantType"));
				participant.put("conferenceName", resultSet.getString("conferenceName"));
				participant.put("autoAttendantUniqueId", resultSet.getInt("autoAttendantUniqueId"));
				participant.put("connectionUniqueId", resultSet.getInt("connectionUniqueId"));

				participants.add(participant);
				numResults++;
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

		return participants;
	}
	
	
}
