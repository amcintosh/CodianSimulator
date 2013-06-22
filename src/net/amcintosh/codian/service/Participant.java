package net.amcintosh.codian.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import net.amcintosh.codian.Constants;
import net.amcintosh.codian.db.ParticipantDB;

import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew McIntosh
 */
public class Participant {
	
	private static Logger log = LoggerFactory.getLogger(Participant.class);
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	public HashMap<String,Object> add(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String,Object> result = new HashMap<String,Object>();
		boolean success = false;
		boolean addResponse = params.get("addResponse")!=null ? (boolean)params.get("addResponse") : false;
		params.remove("addResponse");
		
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
		if (success) {
			result.put(Constants.STATUS_KEY, Constants.SUCCESS_MESSAGE);
			
			if (addResponse) {
				HashMap<String,Object> participantData = new HashMap<String,Object>();
				participantData.put("participantName", params.get("participantName"));
				participantData.put("conferenceName", params.get("conferenceName"));
				participantData.put("participantType", params.get("participantType")==null ? "by_address" : params.get("participantType"));
				participantData.put("participantProtocol", params.get("participantProtocol")==null ? "h323" : params.get("participantProtocol"));
				
				result.put("participant", participantData);	
			}
		} else {
			throw new XmlRpcException("operation failed");
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	public HashMap<String,Object> modify(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String,Object> result = new HashMap<String,Object>();
		boolean success = false;
		
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			if (params.get("conferenceName")==null) {
				throw new XmlRpcException("no conference name or auto attendant id supplied");	
			}
			if (params.get("participantName")==null) {
				throw new XmlRpcException("no participant name supplied");	
			}
			
			try {
				success = ParticipantDB.updateParticipant(params);
			} catch (SQLException e) {
				throw new XmlRpcException("operation failed");
			}
		}
		if (success) {
			result.put(Constants.STATUS_KEY, Constants.SUCCESS_MESSAGE);
			
	

		} else {
			throw new XmlRpcException("operation failed");
		}
		
		return result;
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
			boolean currentState = false;
			boolean configuredState = false;
			
			Object[] operationScope = (Object[]) params.get("operationScope");
			if (operationScope!=null 
					&& ((operationScope.length>0 && "currentState".equals(operationScope[0])) 
						|| (operationScope.length>1 && "currentState".equals(operationScope[1])))) {
				currentState = true;
			}
			if (operationScope!=null 
					&& ((operationScope.length>0 && "configuredState".equals(operationScope[0])) 
						|| (operationScope.length>1 && "configuredState".equals(operationScope[1])))) {
				configuredState = true;
			}
			
			//addResponse boolean true to return the details of the added participant.
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
			
			List<HashMap<String,Object>> participants = ParticipantDB.getParticipantsForEnumerate("", -1, currentState, configuredState); 
			data.put("participants", participants);
			
			if (participants.size()>=Constants.PARTICIPANT_ENUMERATE_MAXRESULTS) {
				data.put("enumerateID", ""+participants.get(participants.size()-1).get("autoAttendantUniqueID"));	
			}
			
			data.put("currentRevision","");
		}
		return data;
	}
}
