package net.amcintosh.codian.db;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;

import net.amcintosh.codian.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Andrew McIntosh
 */
public class DBManager {

	private static Logger log = LoggerFactory.getLogger(DBManager.class);
	
    private static Object _doubleCheckLockingPattern = new Object();
    private static DBManager instance = null;
    
    protected static final String DB_FILE = "data.db";
    
    /**
     * 
     * @return
     * @throws Exception
     */
    static public DBManager getInstance() {
        // Synchronized to be sure that two thread cannot create this
        // classe in the same time
        synchronized (_doubleCheckLockingPattern) {
            if (instance == null) {
                instance = new DBManager();
            }
        }
        return instance;
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
    	try {
    		Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.error("getConnection", e);
		}
		Connection con = DriverManager.getConnection("jdbc:sqlite:"+Config.getConfig().getProperty("app_root")+DB_FILE, "", "");
        return con;
    }


}
