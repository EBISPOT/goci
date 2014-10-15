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

    public VirtuosoConnectionPoolDataSourceFactory setInitialPoolSize(int poolSize) {
        try {
            virtuosoSource.setInitialPoolSize(poolSize);
            return this;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to set initial pool size " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public VirtuosoConnectionPoolDataSourceFactory setMinPoolSize (int minPoolSize) {
        try {
            virtuosoSource.setMinPoolSize(minPoolSize);
            return this;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to set minimum pool size " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public VirtuosoConnectionPoolDataSourceFactory setMaxPoolSize (int maxPoolSize) {
        try {
            virtuosoSource.setMaxPoolSize(maxPoolSize);
            return this;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to set max pool size " + getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    public VirtuosoConnectionPoolDataSourceFactory setCharset (String charset) {
        virtuosoSource.setCharset(charset);
        return this;
    }

    public VirtuosoConnectionPoolDataSourceFactory setUser (String user) {
        virtuosoSource.setUser(user);
        return this;
    }

    public VirtuosoConnectionPoolDataSourceFactory setPassword (String password) {
        virtuosoSource.setPassword(password);
        return this;
    }

    public DataSource getDataSource() throws SQLException {
        return virtuosoSource;
    }
}
