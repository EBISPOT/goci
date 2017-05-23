package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;

/**
 * Created by emma on 12/06/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-529. Aim is to clean-up the
 *         region table by removing semi-colon separated regions and null values e.g. 15q21.3[rs3204689];
 *         15q21.3[rs4238326].
 */
public class V2_0_1_001__Region_table_cleanup implements SpringJdbcMigration {

    private static final String SELECT_REGIONS_FOR_UPDATE = "SELECT ID, NAME FROM REGION WHERE NAME LIKE '%;%'";

    private static final String SELECT_REGION = "SELECT ID FROM REGION WHERE NAME = ?";

    private static final String SELECT_SNP_REGION =
            "SELECT SNP_ID FROM SNP_REGION WHERE REGION_ID = ?";

    private static final String UPDATE_SNP_REGION = "UPDATE SNP_REGION SET REGION_ID =? WHERE SNP_ID = ?";

    private static final String SELECT_NULL_REGIONS = "SELECT ID FROM REGION WHERE NAME IS NULL";

    private static final String DELETE_FROM_REGION =
            "DELETE FROM REGION WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Go through list of malformed region names and link to correct region
        jdbcTemplate.query(SELECT_REGIONS_FOR_UPDATE, (resultSet, i) -> {
            Long malformedRegionId = resultSet.getLong(1);
            String malformedRegionName = resultSet.getString(2);

            // Split string in two to retrieve part before '['
            String[] malformedRegionNameSplit = malformedRegionName.split("\\[", 2);
            String regionElementOfMalformedName = malformedRegionNameSplit[0].trim();

            // Find ID of region that matched region element of malformed string
            Long regionId = jdbcTemplate.queryForObject(SELECT_REGION, Long.class, regionElementOfMalformedName);

            if (regionId != null) {
                Collection<Long> snpsLinkedToMalformedRegion =
                        jdbcTemplate.queryForList(SELECT_SNP_REGION, Long.class, malformedRegionId);

                // For each SNP link it to new region
                for (Long snpId : snpsLinkedToMalformedRegion) {
                    jdbcTemplate.update(UPDATE_SNP_REGION, regionId, snpId);
                }
            }

            // Delete old malformed region
            jdbcTemplate.update(DELETE_FROM_REGION, malformedRegionId);

            return null;
        });

        // Remove regions will null name
        jdbcTemplate.query(SELECT_NULL_REGIONS, (resultSet, i) -> {
            Long nullRegionId = resultSet.getLong(1);

            // Delete NULL region
            jdbcTemplate.update(DELETE_FROM_REGION, nullRegionId);

            return null;
        });


    }
}
