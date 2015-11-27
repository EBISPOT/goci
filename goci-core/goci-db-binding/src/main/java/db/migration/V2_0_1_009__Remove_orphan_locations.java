package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 14/07/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-941. Aim is to remove orphan
 *         locations from  the LOCATION table. This ensures locations only exist that link to a SNP.
 */
public class V2_0_1_009__Remove_orphan_locations implements SpringJdbcMigration {

    // Query for locations not found in SNP_LOCATION table
    private static final String SELECT_ORPHAN_LOCATIONS =
            "SELECT l.id\n" +
                    "FROM LOCATION l\n" +
                    "LEFT JOIN SNP_LOCATION sl ON sl.location_ID = l.id\n" +
                    "WHERE sl.location_id IS NULL";

    private static final String DELETE_FROM_LOCATION = "DELETE FROM LOCATION WHERE ID =?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Query for all duplicates
        jdbcTemplate.query(SELECT_ORPHAN_LOCATIONS, (resultSet, i) -> {
            Long locationId = resultSet.getLong(1);

            // Delete location
            jdbcTemplate.update(DELETE_FROM_LOCATION, locationId);

            return null;
        });
    }
}
