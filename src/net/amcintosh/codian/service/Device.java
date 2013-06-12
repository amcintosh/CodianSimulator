package net.amcintosh.codian.service;

import java.util.Date;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.amcintosh.codian.db.DBUtil;

/**
 * @author Andrew McIntosh
 */
public class Device {

	private static Logger log = LoggerFactory.getLogger(Device.class);
	
	public HashMap<String,Object> query(HashMap<String, Object> params) throws XmlRpcException {
		if (log.isDebugEnabled()) {
			log.debug("In: Device.query");
		}
		HashMap<String,Object> data = new HashMap<String,Object>();
		if (ServiceUtil.authenticateUser(params.get("authenticationUser"),params.get("authenticationPassword"))) {
			data = DBUtil.getDeviceProperties();
			data.put("currentTime", new Date());
		}
		return data;
	}
}
