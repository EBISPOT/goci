package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 30/07/2015.
 *
 * @author emma
 *         <p>
 *         Related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-940. This will remove orphan regions
 *         with no locations linked.
 */
public class V2_0_1_025__Remove_orphan_regions implements SpringJdbcMigration {


    // Query for regions not found in LOCATION table
    private static final String SELECT_ORPHAN_REGIONS =
            "SELECT r.id\n" +
                    "FROM REGION r\n" +
                    "LEFT JOIN LOCATION l ON l.REGION_ID = r.id\n" +
                    "WHERE l.REGION_ID IS NULL";

    private static final String DELETE_FROM_REGION = "DELETE FROM REGION WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Query for all duplicates
        jdbcTemplate.query(SELECT_ORPHAN_REGIONS, (resultSet, i) -> {
            Long regionId = resultSet.getLong(1);

            // Delete location
            jdbcTemplate.update(DELETE_FROM_REGION, regionId);

            return null;
        });

    }

}
