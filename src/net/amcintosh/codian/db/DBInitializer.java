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
	 * Check if a database exists at the default location.
	 * 
	 * @return True if database exists
	 */
	public static boolean dbExists() {
		File file = new File(Config.getConfig().getProperty("app_root")+DBManager.DB_FILE);
		return file.exists();
	}

	/**
	 * If database already exists, do nothing. Otherwise create tables and initialize device properties.
	 */
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
	
	
	/**
	 * Creates device properties table and populates it with initial data.
	 * 
	 * @param stat
	 * @throws SQLException
	 */
	private static void createCodianDataTableSQL(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE TABLE device_properties (property TEXT, value TEXT);");
		stat.executeUpdate("INSERT INTO device_properties values(\"model\", \"" 
				+ Config.getConfig().getProperty("device.model") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"serial\", \""
				+ Config.getConfig().getProperty("device.serial") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"softwareVersion\", \""
				+ Config.getConfig().getProperty("device.softwareVersion") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"buildVersion\", \""
				+ Config.getConfig().getProperty("device.buildVersion") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"apiVersion\", \""
				+ Constants.API_VERSION + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"activatedFeatures\", \"\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalVideoPorts\", \""
				+ Config.getConfig().getProperty("device.totalVideoPorts") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"totalAudioOnlyPorts\", \""
				+ Config.getConfig().getProperty("device.totalAudioOnlyPorts") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"portReservationMode\", \""
				+ Config.getConfig().getProperty("device.portReservationMode") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"maxVideoResolution\", \""
				+ Config.getConfig().getProperty("device.maxVideoResolution") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"isdnPorts\", \""
				+ Config.getConfig().getProperty("device.isdnPorts") + "\");");
		stat.executeUpdate("INSERT INTO device_properties values(\"restartTime\", null);");
	}
	
	public static void setDBStartupTime() {
		//restartTime=20106036T19:15:00
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
	
		
	/**
	 * SQL to create conferences table.
	 * 
	 * @return String of Create Table SQL
	 */
	private static String createConferencesTableSQL() {
		String sql = "CREATE TABLE conference (conferenceName TEXT UNIQUE NOT NULL, " 
					+ "conferenceType TEXT DEFAULT 'scheduled', uniqueId INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "private INTEGER, joinAudioMuted INTEGER, joinVideoMuted INTEGER, " 
					+ "enforceMaximumAudioPorts INTEGER, enforceMaximumVideoPorts INTEGER, "
					+ "templateName TEXT, templateNumber INTEGER, numericId TEXT, "
					+ "guestNumericId TEXT, registerWithGatekeeper INTEGER, pin TEXT, "
					+ "registerWithSIPRegistrar INTEGER, startTime DATETIME DEFAULT current_timestamp, "
					+ "durationSeconds INTEGER, endTime DATETIME, guestPin TEXT, description TEXT, "
					+ "startLocked INTEGER, conferenceMeEnabled INTEGER DEFAULT 1, "
					+ "automaticLectureMode TEXT DEFAULT 'disabled', "
					+ "automaticLectureModeEnabled INTEGER, automaticLectureModeTimeout INTEGER, "
					+ "multicastStreamingEnabled INTEGER, unicastStreamingEnabled INTEGER DEFAULT 1, "
					+ "contentMode TEXT DEFAULT 'transcoded', h239Enabled INTEGER DEFAULT 1, "
					+ "lastChairmanLeavesDisconnect INTEGER DEFAULT 1, "
					+ "cleanupTimeout INTEGER, preconfiguredParticipantsDefer INTEGER, "
					+ "contentTxCodec TEXT DEFAULT 'automatic', contentTxMinimumBitRate TEXT DEFAULT '0', " 
					+ "maximumAudioPorts INTEGER, maximumVideoPorts INTEGER, reservedAudioPorts INTEGER, "
					+ "reservedVideoPorts INTEGER, repetition TEXT DEFAULT 'none', weekDay TEXT, "
					+ "whichWeek TEXT, weekDays TEXT, terminationType TEXT, numberOfRepeats INTEGER, "
					+ "customLayoutEnabled INTEGER, layoutControlEx TEXT DEFAULT 'feccWithDtmfFallback', "
					+ "cameraControl TEXT DEFAULT 'feccWithDtmfFallback', "
					+ "floorStatus TEXT DEFAULT 'inactive', "
					+ "newParticipantsCustomLayout INTEGER, customLayout INTEGER DEFAULT 8, "
					+ "chairControl TEXT DEFAULT 'none', suppressDtmfEx TEXT DEFAULT 'fecc', " 
					+ "inCallMenuControlChair TEXT DEFAULT 'local', contentImportant INTEGER, "
					+ "inCallMenuControlGuest TEXT DEFAULT 'local', encryptionRequired INTEGER, " 
					+ "contentContribution INTEGER DEFAULT 1, contentTransmitResolutions TEXT DEFAULT '4to3Only');";
		return sql;
	}

	/**
	 * SQL to create participant table.
	 * 
	 * @return String of Create Table SQL
	 */
	private static String createParticipantsTableSQL() {
		String sql = "CREATE TABLE participant ( conferenceName TEXT NOT NULL, participantName TEXT NOT NULL, "
					+ "autoAttendantUniqueID INTEGER PRIMARY KEY AUTOINCREMENT, connectionUniqueId INTEGER, "
					+ "addResponse INTEGER, participantProtocol TEXT, participantType TEXT, "
					+ "address TEXT, gatewayAddress TEXT, useSIPRegistrar INTEGER, " 
					+ "transportProtocol TEXT, password TEXT, deferConnection INTEGER, " 
					+ "addAsGuest INTEGER, actAsRecorder INTEGER, maxBitRateToMCU INTEGER, " 
					+ "maxBitRateFromMCU INTEGER, motionSharpnessTradeoff TEXT, " 
					+ "displayNameOverrideStatus INTEGER, displayNameOverrideValue TEXT, " 
					+ "cpLayout TEXT, layoutControlEx TEXT, audioRxMuted INTEGER, " 
					+ "audioRxGainMode TEXT, audioRxGainMillidB INTEGER, videoRxMuted INTEGER, " 
					+ "videoTxWidescreen INTEGER, videoTxMaxResolution TEXT, " 
					+ "videoRxMaxResolution TEXT, autoConnect INTEGER, autoDisconnect INTEGER, " 
					+ "borderWidth INTEGER, dtmfSequence TEXT, linkType TEXT, h239Negotiation TEXT, " 
					+ "layoutControlEnabled INTEGER, " 
					+ "UNIQUE(conferenceName, participantName));";
		return sql;
	}

}
