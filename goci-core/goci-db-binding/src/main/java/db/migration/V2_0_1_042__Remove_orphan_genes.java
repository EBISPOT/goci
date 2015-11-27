package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove gene that are not author reported genes or used in genomic context table. This should remove any
 *         duplicates still lingering in GENE table
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-1020
 */
public class V2_0_1_042__Remove_orphan_genes implements SpringJdbcMigration {

    // Query to find gene names with new lines
    private static final String SELECT_GENES =
            "SELECT ID FROM GENE \n" +
                    "WHERE ID NOT IN (SELECT REPORTED_GENE_ID  \n" +
                    "FROM AUTHOR_REPORTED_GENE) \n" +
                    "AND ID NOT IN (SELECT GENE_ID FROM GENOMIC_CONTEXT)";

    private static final String SELECT_GENOMIC_CONTEXT =
            "SELECT ID FROM GENOMIC_CONTEXT WHERE GENE_ID = ?";

    private static final String SELECT_AUTHOR_REPORTED_GENE =
            "SELECT REPORTED_GENE_ID FROM AUTHOR_REPORTED_GENE WHERE REPORTED_GENE_ID = ?";

    private static final String DELETE_FROM_GENE_ENSEMBL_GENE = "DELETE FROM GENE_ENSEMBL_GENE WHERE GENE_ID = ?";

    private static final String DELETE_FROM_GENE_ENTREZ_GENE = "DELETE FROM GENE_ENTREZ_GENE WHERE GENE_ID = ?";

    private static final String DELETE_FROM_GENE = "DELETE FROM GENE WHERE ID= ?";


    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of orphan genes
        jdbcTemplate.query(SELECT_GENES, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);

            // Ensure gene is not used in GENOMIC CONTEXT or AUTHOR_REPORTED_GENE
            List<Long> genomicContexts = jdbcTemplate.queryForList(SELECT_GENOMIC_CONTEXT, Long.class, geneId);
            List<Long> reportedGeneIds = jdbcTemplate.queryForList(SELECT_AUTHOR_REPORTED_GENE, Long.class, geneId);

            if (genomicContexts.isEmpty() && reportedGeneIds.isEmpty()) {

                // Delete from GENE_ENSEMBL_GENE
                jdbcTemplate.update(DELETE_FROM_GENE_ENSEMBL_GENE, geneId);

                // Delete from GENE_ENTREZ_GENE
                jdbcTemplate.update(DELETE_FROM_GENE_ENTREZ_GENE, geneId);

                // Delete gene from table
                jdbcTemplate.update(DELETE_FROM_GENE, geneId);
            }

            return null;
        });
    }
}
