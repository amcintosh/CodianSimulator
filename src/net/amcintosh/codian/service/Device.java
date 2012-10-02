package net.amcintosh.codian.service;

import java.util.Date;
import java.util.HashMap;

import net.amcintosh.codian.db.DBUtil;

/**
 * @author Andrew McIntosh
 */
public class Device {

	public HashMap<String,Object> query(HashMap params) {
		HashMap<String,Object> data = DBUtil.getDeviceProperties();
		data.put("currentTime", new Date());
		return data;
	}
}
