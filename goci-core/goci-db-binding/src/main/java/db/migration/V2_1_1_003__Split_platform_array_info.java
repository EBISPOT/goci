package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by dwelter on 11/03/16.
 */
public class V2_1_1_003__Split_platform_array_info implements SpringJdbcMigration {

    private static final String DBQUERY =
            "SELECT DISTINCT ID, PLATFORM FROM STUDY";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

    }
}
