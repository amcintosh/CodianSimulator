package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
