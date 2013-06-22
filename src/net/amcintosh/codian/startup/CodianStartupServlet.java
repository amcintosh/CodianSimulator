package net.amcintosh.codian.startup;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;

import net.amcintosh.codian.Config;
import net.amcintosh.codian.db.DBInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrew McIntosh
 */
public class CodianStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 5430223026527766316L;
	private static Logger log = LoggerFactory.getLogger(CodianStartupServlet.class);

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		log.info("Codian Simulator Startup.");
		log.info("Java Runtime Version: "
				+ System.getProperty("java.version") + " ("
				+ System.getProperty("java.home") + ")");
		
		//Properties
		Config.getConfig().loadProperties();
		String app_root = getServletContext().getRealPath("/");
		Properties props = new Properties();
		props.put("app_root", app_root);
		Config.getConfig().addProprties(props);
		
		//DB Setup
		DBInitializer.createDb();
		DBInitializer.setDBStartupTime();
		
		log.info("Codian Simulator started");
	}

}
