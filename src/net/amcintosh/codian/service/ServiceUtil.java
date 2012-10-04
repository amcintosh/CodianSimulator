package net.amcintosh.codian.service;

import org.apache.xmlrpc.XmlRpcException;

public class ServiceUtil {

	public static boolean authenticateUser(Object userName, Object userPassword) throws XmlRpcException {
		//TODO: Implement
		if (true) {
			return true;	
		}
		throw new XmlRpcException("authorization failed");
	}
	
}
