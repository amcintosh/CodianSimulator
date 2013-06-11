package net.amcintosh.codian.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import net.amcintosh.codian.Constants;
import net.amcintosh.codian.db.ParticipantDB;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

/**
 * @author Andrew McIntosh
 */
public class Participant {
	
	private static Logger log = Logger.getLogger(Participant.class.getName());
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	public HashMap<String,Object> add(HashMap<String, Object> params) throws XmlRpcException {
		boolean success = false;
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			if (params.get("conferenceName")==null) {
				throw new XmlRpcException("no conference name or auto attendant id supplied");	
			}
			if (params.get("participantName")==null) {
				throw new XmlRpcException("no participant name supplied");	
			}
			
			//TODO: Reject unknown parameters
			
			
			try {
				success = ParticipantDB.insertParticipant(params);
			} catch (SQLException e) {
				if ("column conferenceName is not unique".equals(e.getMessage())) {
					throw new XmlRpcException("duplicate conference name");
				}
				
			}
		}
		if (!success) {
			throw new XmlRpcException("operation failed");
		}
		
		return params;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	public HashMap<String,Object> enumerate(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String, Object> data = new HashMap<String,Object>();
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			int incomingEnumerateID = -1; 
			if (params.get("enumerateID")!=null) {
				incomingEnumerateID = Integer.parseInt((String)params.get("enumerateID"));
			}
			
			String enumerateFilter = null;
			if (params.get("enumerateFilter")!=null) {
				enumerateFilter = (String)params.get("enumerateFilter");
			}
			
			if (log.isDebugEnabled()) {
				log.debug("participant.enumerate called. Input: enumerateID=" + incomingEnumerateID
						+ ", enumerateFilter=" + enumerateFilter);
			}
			
			List<HashMap<String,Object>> participants = ParticipantDB.getParticipants(""); 
			data.put("participants", participants);
			
			if (participants.size()>=Constants.PARTICIPANT_ENUMERATE_MAXRESULTS) {
				data.put("enumerateID", ""+participants.get(participants.size()-1).get("autoAttendantUniqueID"));	
			}
			
		}
		return data;
	}
}
