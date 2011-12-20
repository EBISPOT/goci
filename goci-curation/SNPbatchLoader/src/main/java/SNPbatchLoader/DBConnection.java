package SNPbatchLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
  private static final Logger log = LoggerFactory.getLogger(DBConnection.class);

  protected static Logger getLog() {
    return log;
  }

  /*establish a connection to the database*/
	public static Connection getConnection() throws SQLException, IOException, Exception{
		
		Properties configFile = new Properties();
		getLog().debug("Loading database connection properties...");
		configFile.load(DBConnection.class.getClassLoader().getResourceAsStream("dbcon.properties"));
		getLog().debug("Loaded connection properties OK!");

		String driver = configFile.getProperty("DB_DRIVER");
		String dburl = configFile.getProperty("DB_URL");
		String user = configFile.getProperty("USER");;
		String password = configFile.getProperty("PASSWORD");;
		
		try{
			Class.forName(driver).newInstance();
		}
		catch (Exception ex){
			getLog().error("Database driver not found" , ex);
			throw new Exception("Database driver not found" , ex);
		}

		// @//machineName:port:SID,   userid,  password
		return DriverManager.getConnection(dburl, user, password);
	}
}
