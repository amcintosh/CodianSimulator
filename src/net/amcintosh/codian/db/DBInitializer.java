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
	
	
	/**
	 * Creates device properties table and populates it with initial data.
	 */
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
	
	/**
	 * SQL to create conferences table.
	 * 
	 * @return String of Create Table SQL
	 */
	private static String createConferencesTableSQL() {
		String sql = "CREATE TABLE conference (conferenceName TEXT UNIQUE NOT NULL, " 
					+ "conferenceType TEXT, uniqueId INTEGER PRIMARY KEY, "
					+ "private INTEGER, joinAudioMuted INTEGER, joinVideoMuted INTEGER, " 
					+ "enforceMaximumAudioPorts INTEGER, enforceMaximumVideoPorts INTEGER, "
					+ "templateName TEXT, templateNumber INTEGER, numericId TEXT, "
					+ "guestNumericId TEXT, registerWithGatekeeper INTEGER, "
					+ "registerWithSIPRegistrar INTEGER, startTime TEXT, pin TEXT, "
					+ "guestPin TEXT, description TEXT, startLocked INTEGER, durationSeconds INTEGER, "
					+ "conferenceMeEnabled INTEGER, automaticLectureMode TEXT, "
					+ "automaticLectureModeEnabled INTEGER, automaticLectureModeTimeout INTEGER, "
					+ "multicastStreamingEnabled INTEGER, unicastStreamingEnabled INTEGER, "
					+ "contentMode TEXT, h239Enabled INTEGER, lastChairmanLeavesDisconnect INTEGER, "
					+ "cleanupTimeout INTEGER, preconfiguredParticipantsDefer INTEGER, "
					+ "contentTxCodec TEXT, contentTxMinimumBitRate TEXT, maximumAudioPorts INTEGER, "
					+ "maximumVideoPorts INTEGER, reservedAudioPorts INTEGER, reservedVideoPorts INTEGER, "
					+ "repetition TEXT, weekDay TEXT, whichWeek TEXT, weekDays TEXT, terminationType TEXT, "
					+ "numberOfRepeats INTEGER, customLayoutEnabled INTEGER, layoutControlEx TEXT, "
					+ "cameraControl TEXT, newParticipantsCustomLayout INTEGER, customLayout INTEGER, "
					+ "chairControl TEXT, suppressDtmfEx TEXT, inCallMenuControlChair TEXT, " 
					+ "inCallMenuControlGuest TEXT, encryptionRequired INTEGER, " 
					+ "contentContribution INTEGER, contentTransmitResolutions TEXT);";
		return sql;
	}

	/**
	 * SQL to create participant table.
	 * 
	 * @return String of Create Table SQL
	 */
	private static String createParticipantsTableSQL() {
		String sql = "CREATE TABLE participant ( conferenceName TEXT NOT NULL, participantName TEXT NOT NULL, "
					+ "autoAttendantUniqueID INTEGER PRIMARY KEY, connectionUniqueId INTEGER, "
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
					+ "autoAttendantUniqueID TEXT, layoutControlEnabled INTEGER, " 
					+ "UNIQUE(conferenceName, participantName));";
		return sql;
	}

}
