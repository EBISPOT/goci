package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by emma on 23/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove duplicate SNPs from database https://www.ebi.ac.uk/panda/jira/browse/GOCI-493
 */
public class V2_0_1_046__Remove_duplciate_snps implements SpringJdbcMigration {

    private static final String SELECT_DUPLICATE_SNPS = "SELECT RS_ID \n" +
            "FROM SINGLE_NUCLEOTIDE_POLYMORPHISM\n" +
            "GROUP BY RS_ID HAVING COUNT(1) > 1";

    private static final String SELECT_SNPS_WITH_RSID =
            "SELECT ID FROM SINGLE_NUCLEOTIDE_POLYMORPHISM WHERE RS_ID = ? ORDER BY ID";

    private static final String DELETE_FROM_GENOMIC_CONTEXT =
            "DELETE FROM GENOMIC_CONTEXT WHERE SNP_ID = ?";

    private static final String DELETE_FROM_LOCATION =
            "DELETE FROM SNP_LOCATION WHERE SNP_ID = ?";

    private static final String UPDATE_STUDY_SNP =
            "UPDATE STUDY_SNP SET SNP_ID = ? WHERE SNP_ID = ?";

    private static final String UPDATE_RISK_ALLELE_PROXY_SNP =
            "UPDATE RISK_ALLELE_PROXY_SNP SET SNP_ID = ? WHERE SNP_ID = ?";

    private static final String UPDATE_RISK_ALLELE_SNP =
            "UPDATE RISK_ALLELE_SNP SET SNP_ID = ? WHERE SNP_ID = ?";

    private static final String DELETE_FROM_SNP =
            "DELETE FROM SINGLE_NUCLEOTIDE_POLYMORPHISM WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of duplicate genes
        jdbcTemplate.query(SELECT_DUPLICATE_SNPS, (resultSet, i) -> {
            String rsId = resultSet.getString(1);

            if (rsId != null) {

                List<Long> snpsWithRsId = jdbcTemplate.queryForList(SELECT_SNPS_WITH_RSID, Long.class, rsId);

                if (!snpsWithRsId.isEmpty()) {
                    // Save the first ID and then remove it from list
                    Long snpIdToKeep = snpsWithRsId.get(0);
                    snpsWithRsId.remove(0);

                    // Remove everything else in list
                    for (Long idToRemove : snpsWithRsId) {

                        // Delete from GENOMIC_CONTEXT
                        jdbcTemplate.update(DELETE_FROM_GENOMIC_CONTEXT, idToRemove);

                        // Delete from SNP_LOCATION
                        jdbcTemplate.update(DELETE_FROM_LOCATION, idToRemove);

                        // Update STUDY_SNP
                        jdbcTemplate.update(UPDATE_STUDY_SNP, snpIdToKeep, idToRemove);

                        // Update RISK_ALLELE_PROXY_SNP
                        jdbcTemplate.update(UPDATE_RISK_ALLELE_PROXY_SNP, snpIdToKeep, idToRemove);

                        // Update RISK_ALLELE_SNP
                        jdbcTemplate.update(UPDATE_RISK_ALLELE_SNP, snpIdToKeep, idToRemove);

                        // Finally delete from SNP table
                        jdbcTemplate.update(DELETE_FROM_SNP, idToRemove);

                    }
                }
            }
            return null;
        });
    }
}
