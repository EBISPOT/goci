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
public class V2_1_2_012__Migrate_or_to_beta_for_non_standard_unit_case_02 implements SpringJdbcMigration {

    // Query to find beta associations with a description that can be fit into a beta unit and direction
    private static final String SELECT_ASSOCIATIONS =
            "SELECT ID, OR_PER_COPY_NUM, DESCRIPTION FROM ASSOCIATION \n" +
                    "WHERE OR_TYPE = 0 \n" +
                    "AND DESCRIPTION IS NOT NULL  \n" +
                    "AND OR_PER_COPY_NUM IS NOT NULL \n" +
                    "AND (DESCRIPTION  LIKE 'increase' OR DESCRIPTION LIKE 'decrease')";

    private static final String UPDATE_ASSOCIATION =
            "UPDATE ASSOCIATION SET BETA_NUM = ?, BETA_UNIT = ? , BETA_DIRECTION= ? , OR_PER_COPY_NUM=?, DESCRIPTION=? WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            float betaNum = resultSet.getFloat(2);
            String direction = resultSet.getString(3).trim();

            // Run update
            jdbcTemplate.update(UPDATE_ASSOCIATION, betaNum, null, direction, null, null, id);
            return null;
        });
    }
}