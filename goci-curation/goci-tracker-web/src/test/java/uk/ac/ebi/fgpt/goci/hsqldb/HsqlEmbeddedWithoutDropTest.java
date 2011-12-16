package uk.ac.ebi.fgpt.goci.hsqldb;

import junit.framework.TestCase;

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
public class HsqlEmbeddedWithoutDropTest extends TestCase {
    public void setUp() {
        try {
            // load driver
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            // get connection and create table
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:target/hsql/goci_test3;hsqldb.write_delay=false");
            connection.prepareStatement("create table if not exists testtable (" +
                                                "id INTEGER, " +
                                                "name VARCHAR(100) ," +
                                                "CONSTRAINT TEST_PK PRIMARY KEY (id));").execute();
            try {
                connection.prepareStatement("insert into testtable(id, name) values (1, 'testvalue');").execute();
            }
            catch (SQLException e) {
                // probably a constraint violation cos the cache isn't cleared
                System.err.println("Constraint violation, cache not cleared, ignoring");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void tearDown() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:target/hsql/goci_test3", "sa", "");
            connection.prepareStatement("SHUTDOWN;").execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testQuery() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:file:target/hsql/goci_test3", "sa", "");
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
