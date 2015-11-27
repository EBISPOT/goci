package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 12/06/2015.
 *
 * @author emma
 *         <p>
 *         Script related to JIRA ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-529. After
 *         V2_0_1_001__Region_table_cleanup script and V2_0_1_002__Remove_duplicates_from_snp_region_table SNPs with
 *         more than one region exist and it cannot be consistently determined which is the most up-to-date region. Thus
 *         delete duplicates and assume NCBI pipeline will remap.
 */
public class V2_0_1_003__Remove_duplicates_from_snp_region_table implements SpringJdbcMigration {

    private static final String SELECT_DUPLICATES = "SELECT\n" +
            "SNP_ID , count(*)\n" +
            "from SNP_REGION\n" +
            "group by SNP_ID\n" +
            "HAVING count(*) > 1";

    private static final String DELETE_FROM_SNP_REGION = "DELETE FROM SNP_REGION WHERE SNP_ID =?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Go through list of exact duplicates in SNP_REGION table
        jdbcTemplate.query(SELECT_DUPLICATES, (resultSet, i) -> {

            Long snpId = resultSet.getLong(1);

            // Delete all entries
            jdbcTemplate.update(DELETE_FROM_SNP_REGION, snpId);

            return null;
        });

    }
}
