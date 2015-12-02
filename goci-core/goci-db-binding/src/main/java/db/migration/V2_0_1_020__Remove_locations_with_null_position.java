package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emma on 28/07/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-927. Aim is to remove locations
 *         from the LOCATION table that have a null chromosome position.
 */
public class V2_0_1_020__Remove_locations_with_null_position implements SpringJdbcMigration {

    private static final String SELECT_LOCATIONS = "SELECT l.id, s.id\n" +
            "FROM LOCATION l, SINGLE_NUCLEOTIDE_POLYMORPHISM s, SNP_LOCATION sl, REGION r\n" +
            "WHERE (l.CHROMOSOME_NAME IS NULL\n" +
            "OR l.CHROMOSOME_POSITION IS NULL)\n" +
            "AND l.ID = sl.LOCATION_ID\n" +
            "AND s.ID = sl.SNP_ID\n" +
            "AND l.REGION_ID = r.ID";

    private static final String SELECT_GENOMIC_CONTEXTS_LINKED_TO_LOCATION =
            "SELECT ID from GENOMIC_CONTEXT WHERE LOCATION_ID = ?";

    private static final String DELETE_FROM_SNP_LOCATION = "DELETE FROM SNP_LOCATION WHERE SNP_ID =? AND LOCATION_ID=?";

    private static final String UPDATE_GENOMIC_CONTEXT = "UPDATE GENOMIC_CONTEXT \n" +
            "SET LOCATION_ID = null\n" +
            "WHERE ID = ?";

    private static final String DELETE_FROM_LOCATION = "DELETE FROM LOCATION WHERE ID =?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        final List<Long> locationIds = new ArrayList<>();

        // Query for locations
        jdbcTemplate.query(SELECT_LOCATIONS, (resultSet, i) -> {
            Long locationId = resultSet.getLong(1);
            Long snpId = resultSet.getLong(2);

            // Keep record of locations to be deleted
            locationIds.add(locationId);

            // Possibility that genomic context is linked to location
            List<Long> genomicContextIds = jdbcTemplate.queryForList(SELECT_GENOMIC_CONTEXTS_LINKED_TO_LOCATION,
                                                                     Long.class,
                                                                     locationId);

            // Delete the link between the SNP and location
            jdbcTemplate.update(DELETE_FROM_SNP_LOCATION, snpId, locationId);

            // Remove location from any linked genomic contexts
            if (!genomicContextIds.isEmpty()) {
                for (Long genomicContextId : genomicContextIds) {
                    jdbcTemplate.update(UPDATE_GENOMIC_CONTEXT, genomicContextId);
                }
            }
            return null;
        });

        for (Long id : locationIds) {
            // Delete locations after all records referring to it have been removed
            jdbcTemplate.update(DELETE_FROM_LOCATION, id);
        }
    }
}
