package net.amcintosh.codian.service;

import java.sql.SQLException;
import java.util.HashMap;

import net.amcintosh.codian.db.DBUtil;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

/**
 * @author Andrew McIntosh
 */
public class Participant {
	
	private static Logger log = Logger.getLogger(Participant.class.getName());
	
	public HashMap<String,Object> add(HashMap<String, Object> params) throws XmlRpcException {
		boolean success = false;
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			if (params.get("conferenceName")==null) {
				throw new XmlRpcException("no conference name or auto attendant id supplied");	
			}
			if (params.get("participantName")==null) {
				throw new XmlRpcException("no participant name supplied");	
			}

			try {
				success = DBUtil.insertParticipant(params);
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
}
