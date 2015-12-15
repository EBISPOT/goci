package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 23/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove any newline chars from RSIDs in database
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-493
 */
public class V2_0_1_045__Remove_newlines_from_RSID implements SpringJdbcMigration {

    // Query to find SNPs
    private static final String SELECT_SNPS =
            "SELECT ID, RS_ID FROM SINGLE_NUCLEOTIDE_POLYMORPHISM WHERE INSTR(RS_ID, CHR(10)) > 0";

    // Delete SNPs
    private static final String UPDATE_SNPS = "UPDATE SINGLE_NUCLEOTIDE_POLYMORPHISM SET RS_ID = ? WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of SNPs
        jdbcTemplate.query(SELECT_SNPS, (resultSet, i) -> {
            Long snpId = resultSet.getLong(1);
            String rsId = resultSet.getString(2);

            String newline = System.getProperty("line.separator");

            // Update RS_ID
            if (rsId.contains(newline)) {
                // Remove new line
                String updatedRsId = rsId.replace(newline, "");

                // Update gene name
                jdbcTemplate.update(UPDATE_SNPS, updatedRsId, snpId);
            }
            return null;
        });
    }
}
