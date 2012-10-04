package net.amcintosh.codian.service;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;

import net.amcintosh.codian.db.DBUtil;

/**
 * @author Andrew McIntosh
 */
public class Device {

	private static Logger log = Logger.getLogger(Device.class.getName());
	
	public HashMap<String,Object> query(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String,Object> data = new HashMap<String,Object>();
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			data = DBUtil.getDeviceProperties();
			data.put("currentTime", new Date());
		}
		return data;
	}
}
