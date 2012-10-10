package net.amcintosh.codian.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import net.amcintosh.codian.db.ConferenceDB;
import net.amcintosh.codian.db.DBUtil;
import net.amcintosh.codian.Constants;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

/**
 * @author Andrew McIntosh
 */
public class Conference {
	
	private static Logger log = Logger.getLogger(Device.class.getName());
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws XmlRpcException
	 */
	public HashMap<String,Object> create(HashMap<String, Object> params) throws XmlRpcException {
		boolean success = false;
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			if (params.get("conferenceName")==null) {
				throw new XmlRpcException("no conference name or auto attendant id supplied");	
			}

			try {
				@SuppressWarnings("unused")
				Date time = (Date) params.get("startTime");
			} catch (ClassCastException cce) {
				throw new XmlRpcException("malformed parameter: startTime");
			}

			try {
				@SuppressWarnings("unused")
				Date time = (Date) params.get("endTime");
			} catch (ClassCastException cce) {
				throw new XmlRpcException("malformed parameter: endTime");
			}

	
			/*
			TODO: 
			invalid start time specified
			invalid end time specified
			invalid PIN specified. A PIN specified is not a valid series of digits.
			duplicate numeric ID. A numeric ID was given, but this ID is already in use.
			no such template. The specified template wasn't found.
			*/
			try {
				success = ConferenceDB.insertConference(params);
			} catch (SQLException sqle) {
				if ("column conferenceName is not unique".equals(sqle.getMessage())) {
					throw new XmlRpcException("duplicate conference name");
				}
				
			}
		}
		if (!success) {
			throw new XmlRpcException("operation failed");
		}
		
		return params;
	}


	public HashMap<String,Object> enumerate(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String, Object> data = new HashMap<String,Object>();
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			data.put("enumerateID","");
			data.put("conferences",ConferenceDB.getConferences(false, true));
			data.put("currentRevision","");
		}
		return data;
	}

}
