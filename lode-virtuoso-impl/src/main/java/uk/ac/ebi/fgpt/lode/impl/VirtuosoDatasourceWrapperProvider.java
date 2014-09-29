package uk.ac.ebi.fgpt.lode.impl;

import uk.ac.ebi.fgpt.lode.utils.DatasourceProvider;
import virtuoso.jdbc4.VirtuosoDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 29/09/14
 */
public class VirtuosoDatasourceWrapperProvider implements DatasourceProvider {
    private VirtuosoDataSource virtuosoDataSource;

    public void setVirtuosoDatasource(VirtuosoDataSource vds) {
        this.virtuosoDataSource = vds;
    }

    @Override public DataSource getDataSource() throws SQLException {
        return virtuosoDataSource;
    }
}
