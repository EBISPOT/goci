package uk.ac.ebi.fgpt.lode.impl;

import uk.ac.ebi.fgpt.lode.utils.DatasourceProvider;
import virtuoso.jdbc4.VirtuosoConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/09/14
 */
public class VirtuosoConnectionPoolDataSourceFactory implements DatasourceProvider {
    private final VirtuosoConnectionPoolDataSource virtuosoSource;

    public VirtuosoConnectionPoolDataSourceFactory() {
        try {
            VirtuosoConnectionPoolDataSource ds = new VirtuosoConnectionPoolDataSource();
            ds.setInitialPoolSize(0);
            ds.setMinPoolSize(0);
            ds.setMaxPoolSize(4);
            ds.setCharset("UTF-8");
            ds.setUser("dba");
            ds.setPassword("dba");
            virtuosoSource = ds;
        }
        catch (SQLException e) {
            throw new IllegalStateException("Failed to create " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public VirtuosoConnectionPoolDataSourceFactory(String endpointUrl, int port) {
        this();
        // now override server name and port number
        virtuosoSource.setServerName(endpointUrl);
        virtuosoSource.setPortNumber(port);
    }


    public DataSource getDataSource() throws SQLException {
        return virtuosoSource;
    }
}
