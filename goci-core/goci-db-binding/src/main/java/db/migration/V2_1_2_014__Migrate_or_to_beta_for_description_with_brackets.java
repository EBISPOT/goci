package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 06/04/2016.
 *
 * @author emma *         <p> Copy OR and DESCRIPTION values to BETA_NUM, BETA_UNIT and BETA_DIRECTION in ASSOCIATION
 *         table
 */
public class V2_1_2_014__Migrate_or_to_beta_for_description_with_brackets implements SpringJdbcMigration {

    // Query to find beta associations with a description with a bracket.
    private static final String SELECT_ASSOCIATIONS =
            "SELECT ID, OR_PER_COPY_NUM, DESCRIPTION FROM ASSOCIATION \n" +
                    "WHERE OR_TYPE = 0 \n" +
                    "AND DESCRIPTION IS NOT NULL  \n" +
                    "AND OR_PER_COPY_NUM IS NOT NULL \n" +
                    "AND DESCRIPTION LIKE '(unit%'\n";

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
            String unit = description.substring(1, lastIndexOfSpace);
            String direction = description.substring(lastIndexOfSpace, description.length() - 1);

            // Run update
            jdbcTemplate.update(UPDATE_ASSOCIATION, betaNum, unit.trim(), direction.trim(), null, null, id);
            return null;
        });
    }
}