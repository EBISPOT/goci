package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 12/06/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-529. After
 *         V2_0_1_001__Region_table_cleanup script exact duplicates exist in SNP_REGION table. These will be deleted and
 *         a single value recreated.
 */
public class V2_0_1_002__Remove_duplicates_from_snp_region_table implements SpringJdbcMigration {

    private static final String SELECT_DUPLICATES = "SELECT SNP_ID, REGION_ID, count(*)\n" +
            "FROM SNP_REGION\n" +
            "GROUP BY SNP_ID, REGION_ID\n" +
            "HAVING count(*) > 1";

    private static final String DELETE_FROM_SNP_REGION = "DELETE FROM SNP_REGION WHERE SNP_ID =? AND REGION_ID= ? ";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        final Map<Long, Long> snpIdToRegionId = new HashMap<>();

        // Go through list of exact duplicates in SNP_REGION table
        jdbcTemplate.query(SELECT_DUPLICATES, (resultSet, i) -> {

            Long snpId = resultSet.getLong(1);
            Long regionId = resultSet.getLong(2);

            // Store entry so we can recreate
            snpIdToRegionId.put(snpId, regionId);

            // Delete all entries
            jdbcTemplate.update(DELETE_FROM_SNP_REGION, snpId, regionId);

            return null;
        });

        // Insert statements
        SimpleJdbcInsert insertSnpRegion =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("SNP_REGION")
                        .usingColumns("SNP_ID", "REGION_ID");

        for (Long snpId : snpIdToRegionId.keySet()) {
            Long regionId = snpIdToRegionId.get(snpId);

            Map<String, Object> snpRegionArgs = new HashMap<>();
            snpRegionArgs.put("SNP_ID", snpId);
            snpRegionArgs.put("REGION_ID", regionId);
            insertSnpRegion.execute(snpRegionArgs);
        }
    }
}
