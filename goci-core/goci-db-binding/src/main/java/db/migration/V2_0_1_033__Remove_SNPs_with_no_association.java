package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 23/09/2015.
 *
 * @author emma
 *         <p>
 *         In the GENOMIC_CONTEXT table we have SNPs that are not used in any associations. When data is remapped it
 *         happens per association and thus these never get removed/updated in the GENOMIC_CONTEXT table.
 *         <p>
 *         JIRA: https://www.ebi.ac.uk/panda/jira/browse/GOCI-1037
 */
public class V2_0_1_033__Remove_SNPs_with_no_association implements SpringJdbcMigration {

    // Query for SNPs not used in any associations
    private static final String SELECT_SNPS = "SELECT DISTINCT SNP_ID FROM GENOMIC_CONTEXT\n" +
            "WHERE SNP_ID NOT IN (SELECT DISTINCT S.ID\n" +
            "FROM ASSOCIATION A, ASSOCIATION_LOCUS AL, \n" +
            "LOCUS L, LOCUS_RISK_ALLELE LRA, RISK_ALLELE R,\n" +
            "RISK_ALLELE_SNP RAS, SINGLE_NUCLEOTIDE_POLYMORPHISM S\n" +
            "WHERE AL.ASSOCIATION_ID = A.ID AND \n" +
            "AL.LOCUS_ID=L.ID AND \n" +
            "LRA.LOCUS_ID = L.ID AND \n" +
            "LRA.RISK_ALLELE_ID=R.ID AND \n" +
            "S.ID = RAS.SNP_ID AND\n" +
            "R.ID = RAS.RISK_ALLELE_ID)";

    private static final String DELETE_FROM_GENOMIC_CONTEXT = "DELETE FROM GENOMIC_CONTEXT WHERE SNP_ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_SNPS, (resultSet, i) -> {
            Long snpId = resultSet.getLong(1);

            // Delete SNP from genomic context
            jdbcTemplate.update(DELETE_FROM_GENOMIC_CONTEXT, snpId);

            return null;
        });
    }
}
