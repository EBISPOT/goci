package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 06/04/2016.
 *
 * @author emma
 *         <p>
 *         Copy OR and DESCRIPTION values to BETA_NUM, BETA_UNIT and BETA_DIRECTION in ASSOCIATION table
 */
public class V2_1_2_015__Migrate_remaining_beta_values implements SpringJdbcMigration {

    // Migrate all remaining beta values
    private static final String SELECT_ASSOCIATIONS =
            "SELECT ID, OR_PER_COPY_NUM FROM ASSOCIATION WHERE OR_TYPE = 0 AND OR_PER_COPY_NUM IS NOT NULL";

    private static final String UPDATE_ASSOCIATION =
            "UPDATE ASSOCIATION SET BETA_NUM = ?, OR_PER_COPY_NUM=? WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            float betaNum = resultSet.getFloat(2);

            // Run update
            jdbcTemplate.update(UPDATE_ASSOCIATION, betaNum, null, id);
            return null;
        });
    }
}