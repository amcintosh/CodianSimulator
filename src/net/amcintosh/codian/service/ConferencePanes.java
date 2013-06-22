package net.amcintosh.codian.service;

import java.util.HashMap;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for: conference.paneplacement.*
 * 
 * @author Andrew McIntosh
 */
public class ConferencePanes {
	
	private static Logger log = LoggerFactory.getLogger(ConferencePanes.class);
		
	public HashMap<String,Object> modify(HashMap<String, Object> params) throws XmlRpcException {
		HashMap<String, Object> result = new HashMap<String,Object>();	
		result.put("panesModified", 0);
		return result;
	}
}
	