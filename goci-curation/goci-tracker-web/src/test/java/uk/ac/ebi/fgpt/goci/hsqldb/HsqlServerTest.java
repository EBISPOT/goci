package uk.ac.ebi.fgpt.goci.hsqldb;

import junit.framework.TestCase;
import org.hsqldb.Server;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 27/10/11
 */
public class HsqlServerTest extends TestCase {
    private Server server;

    public void setUp() {
        try {
            // create a new HSQLDB server
            server = new Server();
            server.setLogWriter(new PrintWriter(System.out));

            // create database name and path
            server.setDatabaseName(0, "goci_test1");
            server.setDatabasePath(0, "file:target/hsql/goci_test1");

            // Start the database!
            server.start();

            // load driver and get connection
            Class.forName("org.hsqldb.jdbcDriver");
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/goci_test1", "sa", "");

            connection.prepareStatement("create table testtable (" +
                                                "id INTEGER, " +
                                                "name VARCHAR(100));").execute();
            connection.prepareStatement("insert into testtable(id, name) values (1, 'testvalue');").execute();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/goci_test1", "sa", "");
            connection.prepareStatement("drop table testtable;").execute();
            if (server != null) {
                server.stop();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testQuery() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/goci_test1", "sa", "");
            ResultSet rs = connection.prepareStatement("select * from testtable;").executeQuery();
            // Checking if the data is correct
            int i = 0;
            while (rs.next()) {
                i++;
                assertEquals("Id was not 1", 1, rs.getInt(1));
                assertEquals("Name was not testvalue", "testvalue", rs.getString(2));
            }
            assertEquals("Too many results", 1, i);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        finally {
            // Closing the connection
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        }
    }
}
