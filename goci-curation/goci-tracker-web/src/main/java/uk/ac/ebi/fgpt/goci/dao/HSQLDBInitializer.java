package uk.ac.ebi.fgpt.goci.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A specialization of the basic JDBC study DAO that controls creation, startup and shutdown of a HSQL DB instance.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class HSQLDBInitializer {
    // select ID, PUBMED_ID, TITLE, ABSTRACT, USER_ID, STATE, GWAS_ELIGIBILITY from STUDY
    // select ID, USER_NAME, FIRST_NAME, LAST_NAME, EMAIL, RESTAPIKEY, PERMISSIONS from CONAN_USERS
    public static final String CREATE_SEQUENCE =
            "create sequence SEQ_GOCI increment by 1";
    public static final String CREATE_USER =
            "create table if not exists USER (" +
                    "id INTEGER NOT NULL, " +
                    "user_name VARCHAR (100), " +
                    "first_name VARCHAR (100), " +
                    "last_name VARCHAR (100), " +
                    "email VARCHAR (100), " +
                    "restapikey VARCHAR (150)," +
                    "permissions VARCHAR (100)," +
                    "constraint GOCI_USER_PK primary key (id) )";
    public static final String CREATE_STUDY =
            "create table if not exists STUDY (" +
                    "id INTEGER NOT NULL, " +
                    "pubmed_id VARCHAR (50), " +
                    "title VARCHAR (400)," +
                    "abstract CLOB, " +
                    "user_id INTEGER, " +
                    "state VARCHAR (50)," +
                    "GWAS_ELIGIBILITY VARCHAR (50), " +
                    "constraint GOCI_STUDY_PK primary key (id)," +
                    "constraint GOCI_STUDY_USER_FK foreign key (user_id) references USER (id) )";

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void init() {
        // create study table in the HSQL database if they do not already exist
        try {
            // load driver
            Class.forName(getDriverClassName());

            // get connection and create tables
            getLog().debug("Initializing database...");
            Connection connection = DriverManager.getConnection(getUrl() + ";hsqldb.write_delay=false");
            // create sequence
            getLog().debug("Creating sequences...");
            try {
                connection.prepareStatement(CREATE_SEQUENCE).execute();
            }
            catch (SQLException e) {
                // sequence already exists
                getLog().debug("GOCI_SEQUENCE already exists, skipping sequence creation");
            }
            // create user table
            getLog().debug("Creating tables...");
            connection.prepareStatement(CREATE_USER).execute();
            // create study table
            connection.prepareStatement(CREATE_STUDY).execute();
            getLog().debug("...database initialization complete");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load HSQL driver", e);
        }
        catch (SQLException e) {
            throw new CannotGetJdbcConnectionException("Unable to connect to " + getUrl(), e);
        }
    }

    public void destroy() {
        // send shutdown signal to HSQL database
        try {
            Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
            connection.prepareStatement("SHUTDOWN;").execute();
        }
        catch (SQLException e) {
            throw new CannotGetJdbcConnectionException("Unable to connect to " + getUrl(), e);
        }
    }
}
