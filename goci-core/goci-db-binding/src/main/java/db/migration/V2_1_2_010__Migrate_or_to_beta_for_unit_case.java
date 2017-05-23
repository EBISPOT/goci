package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Copy OR and DESCRIPTION values to BETA_NUM, BETA_UNIT and BETA_DIRECTION in ASSOCIATION table
 */
public class V2_1_2_010__Migrate_or_to_beta_for_unit_case implements SpringJdbcMigration {

    // Query to find beta associations with a description that can be fit into a beta unit and direction
    private static final String SELECT_ASSOCIATIONS =
            "SELECT ID, OR_PER_COPY_NUM, DESCRIPTION FROM ASSOCIATION " +
                    "WHERE OR_TYPE = 0 " +
                    "AND DESCRIPTION IS NOT NULL " +
                    "AND OR_PER_COPY_NUM IS NOT NULL " +
                    "AND (DESCRIPTION  LIKE '% increase' OR DESCRIPTION LIKE '% decrease')";

    private static final String UPDATE_ASSOCIATION =
            "UPDATE ASSOCIATION SET BETA_NUM = ?, BETA_UNIT = ? , BETA_DIRECTION= ? , OR_PER_COPY_NUM=?, DESCRIPTION=? WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            float betaNum = resultSet.getFloat(2);
            String description = resultSet.getString(3).trim();

            // Split description
            int lastIndexOfSpace = description.lastIndexOf(" ");
            String unit = description.substring(0, lastIndexOfSpace);
            String direction = description.substring(lastIndexOfSpace + 1);

            // Run update
            jdbcTemplate.update(UPDATE_ASSOCIATION, betaNum, unit.trim(), direction.trim(), null, null, id);
            return null;
        });
    }
}