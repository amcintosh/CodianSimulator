package net.amcintosh.codian.service;

import java.sql.SQLException;
import java.util.HashMap;

import net.amcintosh.codian.db.DBUtil;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

/**
 * @author Andrew McIntosh
 */
public class Conference {
	
	private static Logger log = Logger.getLogger(Device.class.getName());
	
	public HashMap<String,Object> create(HashMap<String, Object> params) throws XmlRpcException {
		boolean success = false;
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			if (params.get("conferenceName")==null) {
				throw new XmlRpcException("no conference name or auto attendant id supplied");	
			}
			
			/*
			invalid start time specified. A conference start time is not valid.
			invalid end time specified. A conference end time is not valid.
			invalid PIN specified. A PIN specified is not a valid series of digits.
			
			duplicate numeric ID. A numeric ID was given, but this ID is already in use.
			no such template. The specified template wasn't found.
			*/
			try {
				success = DBUtil.insertConference(params);
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
