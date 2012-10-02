package net.amcintosh.codian.db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import net.amcintosh.codian.Config;
import net.amcintosh.codian.Constants;

/**
 * @author Andrew McIntosh
 */
public class DBInitializer {

	private static Logger log = Logger.getLogger(DBInitializer.class.getName());

	/**
	 * 
	 * @return
	 */
	public static boolean dbExists() {
		File file = new File(Config.getConfig().getProperty("app_root")+DBManager.DB_FILE);
		return file.exists();
	}

	public static void createDb() {
		if (dbExists()) {
			log.info("DB Exists");
			return;
		}
		log.info("No DB. Creating a new one");
		Connection con = null;

		try {
			con = DBManager.getInstance().getConnection();
			con.setAutoCommit(false);

			Statement stat = con.createStatement();
			
			createCodianDataTableSQL(stat);
			con.commit();
			
			//populateCodianDataTable(stat);
			//con.commit();

			stat.executeUpdate(createConferencesTableSQL());
			con.commit();
			
			stat.executeUpdate(createParticipantsTableSQL());
			con.commit();
			
			stat.close();
			con.close();
		} catch (SQLException e) {
			log.error("createDb",e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	private static void createCodianDataTableSQL(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE TABLE device_properties (property TEXT, value TEXT);");
		stat.executeUpdate("INSERT INTO device_properties values(\"model\", \"Codian MCU 4505\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"serial\", \"SM001B0D\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"softwareVersion\", \"4.3(2.18)\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"buildVersion\", \"6.18(2.18)\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"apiVersion\", \"2.9\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"activatedFeatures\", \"odian MCU 4505\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalVideoPorts\", \"12\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalAudioOnlyPorts\", \"12\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"portReservationMode\", \"disabled\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"maxVideoResolution\", \"4cif\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"isdnPorts\", \"-1\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"restartTime\", null);");


	}
	
	public static void setDBStartupTime() {
		//restartTime=Wed Jul 18 10:04:01 EDT 2012
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.CODIAN_DATE_FORMAT);
		try {
			Connection con = DBManager.getInstance().getConnection();
			Statement stat = con.createStatement();
			stat.executeUpdate("UPDATE device_properties SET value = \"" +sdf.format(new Date())+"\" WHERE property = \"restartTime\";");
			stat.close();
			con.close();
		} catch (SQLException e) {
			log.error("setDBStartupTime",e);
		}
	}
	
	private static void populateCodianDataTable(Statement stat) throws SQLException {

		stat.executeUpdate("INSERT INTO device_properties values(\"model\", \"Codian MCU 4505\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"serial\", \"SM001B0D\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"softwareVersion\", \"4.3(2.18)\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"buildVersion\", \"6.18(2.18)\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"apiVersion\", \"2.9\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"activatedFeatures\", \"odian MCU 4505\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalVideoPorts\", \"12\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalAudioOnlyPorts\", \"12\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"portReservationMode\", \"disabled\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"maxVideoResolution\", \"4cif\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"isdnPorts\", \"-1\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"restartTime\", null);");
	}
	
	private static String createConferencesTableSQL() {
		String sql = "create table people (name, occupation);";
		return sql;
	}
	
	private static String createParticipantsTableSQL() {
		String sql = "create table mo_people (name, occupation);";
		return sql;
	}

}
