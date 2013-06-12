package net.amcintosh.codian;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew McIntosh
 */
public class Config {
	private final static String PROPERTIES_FILE = "codian.properties";
	
	private static Config ref;
	private Properties props = new Properties();

	private static Logger log = LoggerFactory.getLogger(Config.class);

	public static Config getConfig() {
		if (ref == null)
			ref = new Config();
		return ref;
	}

	/**
	 * Add the specifed properties into the global properties
	 * 
	 * @param prop_
	 */
	public void addProprties(Properties prop_) {
		props.putAll(prop_);
	}

	/**
	 * Accessor get method returns all the properties
	 * 
	 * @return Properties all the properties
	 */
	public Properties getAllProperties() {
		return (Properties) props.clone();
	}

	/**
	 * Accessor get method returns the specific property by name. Helper
	 * constants are provided that maps to the file property key
	 * 
	 * @return String required property
	 */
	public String getProperty(String key) {
		return props.getProperty(key);
	}

	/**
	 * Load a Java properties file.
	 * 
	 * @param fileName
	 * @param doingEnvSpecific
	 * @return
	 */
	public Properties loadProperties() {

		InputStream inputStream = null;

		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
			props.load(inputStream);

		} catch (Throwable t) {
			String msg = "Error loading " + PROPERTIES_FILE;
			log.error(msg, t);
			throw new RuntimeException(msg, t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}

		return props;
	}

	/**
	 * Overridden toString method to return state of this object in the form of
	 * string for debugging purposes
	 * 
	 * @return String Config state as string
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		Enumeration<?> enum_ = props.propertyNames();
		while (enum_.hasMoreElements()) {
			String propName = (String) enum_.nextElement();
			result.append(propName + "=" + props.getProperty(propName) + "\r\n");
		}
		return result.toString();
	}

}
