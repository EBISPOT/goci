package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 23/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove SNPs from database not used in any associations: https://www.ebi.ac.uk/panda/jira/browse/GOCI-1055
 */
public class V2_0_1_047__Remove_snps_not_used_in_association implements SpringJdbcMigration {

    private static final String SELECT_SNPS = "SELECT ID FROM\n" +
            "SINGLE_NUCLEOTIDE_POLYMORPHISM\n" +
            "WHERE ID NOT IN (SELECT S.ID FROM ASSOCIATION A, ASSOCIATION_LOCUS AL,\n" +
            "LOCUS L, LOCUS_RISK_ALLELE LRA, RISK_ALLELE R,\n" +
            "RISK_ALLELE_SNP RAS, SINGLE_NUCLEOTIDE_POLYMORPHISM S\n" +
            "WHERE AL.ASSOCIATION_ID = A.ID AND\n" +
            "AL.LOCUS_ID=L.ID AND\n" +
            "LRA.LOCUS_ID = L.ID AND\n" +
            "LRA.RISK_ALLELE_ID=R.ID AND\n" +
            "S.ID = RAS.SNP_ID AND\n" +
            "R.ID = RAS.RISK_ALLELE_ID)\n" +
            "AND ID NOT IN\n" +
            "(SELECT SNP_ID FROM RISK_ALLELE_PROXY_SNP)\n" +
            "AND ID NOT IN\n" +
            "(SELECT SNP_ID FROM RISK_ALLELE_SNP)";

    private static final String DELETE_FROM_LOCATION =
            "DELETE FROM SNP_LOCATION WHERE SNP_ID = ?";

    private static final String DELETE_FROM_STUDY_SNP =
            "DELETE FROM STUDY_SNP WHERE SNP_ID = ?";

    private static final String DELETE_FROM_SNP =
            "DELETE FROM SINGLE_NUCLEOTIDE_POLYMORPHISM WHERE ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of duplicate genes
        jdbcTemplate.query(SELECT_SNPS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);

            // Tidy up
            jdbcTemplate.update(DELETE_FROM_LOCATION, id);
            jdbcTemplate.update(DELETE_FROM_STUDY_SNP, id);
            jdbcTemplate.update(DELETE_FROM_SNP, id);

            return null;
        });
    }
}
