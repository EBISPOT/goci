package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Objects;

/**
 * Created by emma on 08/07/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket:https://www.ebi.ac.uk/panda/jira/browse/GOCI-924. Aim is to remove duplicates
 *         from the LOCATION table that were created as a result of V2_0_1_005__Populate_locations_and_snp_location. The
 *         SQL query in V2_0_1_005__Populate_locations_and_snp_location did not take account of duplicate SNPs. This
 *         script is to specifically deal witH locations that have null chromosome names and positions.
 */
public class V2_0_1_007__Remove_duplicate_locations_where_chromosome_name_and_position_null implements
        SpringJdbcMigration {

    private static final String SELECT_LOCATION_DUPLICATES =
            "SELECT REGION_ID, CHROMOSOME_NAME, CHROMOSOME_POSITION, COUNT(1) as CNT\n" +
                    "FROM LOCATION\n" +
                    "WHERE CHROMOSOME_NAME IS NULL\n" +
                    "AND CHROMOSOME_POSITION IS NULL\n" +
                    "GROUP BY REGION_ID, CHROMOSOME_NAME, CHROMOSOME_POSITION\n" +
                    "HAVING COUNT(1) > 1\n" +
                    "ORDER BY CNT DESC";

    private static final String SELECT_MATCHING_LOCATIONS =
            "SELECT ID from LOCATION WHERE REGION_ID = ? AND CHROMOSOME_NAME IS NULL AND CHROMOSOME_POSITION IS NULL ORDER BY ID";

    private static final String SELECT_SNPS_LINKED_TO_LOCATION =
            "SELECT SNP_ID from SNP_LOCATION WHERE LOCATION_ID = ?";

    private static final String UPDATE_SNP_LOCATION =
            "UPDATE SNP_LOCATION SET LOCATION_ID  = ? WHERE LOCATION_ID = ? AND SNP_ID = ?";

    private static final String DELETE_FROM_LOCATION = "DELETE FROM LOCATION WHERE ID =?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Query for all duplicates
        jdbcTemplate.query(SELECT_LOCATION_DUPLICATES, (resultSet, i) -> {
            Long regionId = resultSet.getLong(1);

            // Get a list of all location ids that have duplicate region_id, chromosome name and chromosome positions
            List<Long> duplicateLocationIds =
                    jdbcTemplate.queryForList(SELECT_MATCHING_LOCATIONS,
                                              Long.class,
                                              regionId);

            // Keep the first id, this will be our only remaining location
            if (duplicateLocationIds.size() > 0) {
                Long locationIdToKeep = duplicateLocationIds.get(0);

                for (Long locationId : duplicateLocationIds) {
                    if (!Objects.equals(locationId, locationIdToKeep)) {
                        // Get all SNP linked to this location
                        List<Long> snpIds = jdbcTemplate.queryForList(SELECT_SNPS_LINKED_TO_LOCATION,
                                                                      Long.class,
                                                                      locationId);

                        // Link SNP to remaining location
                        for (Long snpId : snpIds) {
                            jdbcTemplate.update(UPDATE_SNP_LOCATION, locationIdToKeep, locationId, snpId);
                        }

                        // Delete location
                        jdbcTemplate.update(DELETE_FROM_LOCATION, locationId);
                    }
                }
            }
            return null;
        });
    }
}
