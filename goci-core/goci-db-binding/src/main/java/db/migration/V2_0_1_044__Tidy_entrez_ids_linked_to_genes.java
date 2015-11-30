package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by emma on 19/11/2015.
 *
 * @author emma
 *         <p>
 *         Tidy up cases in database where an Entrez ID has more than one gene linked to it
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-956
 */
public class V2_0_1_044__Tidy_entrez_ids_linked_to_genes implements SpringJdbcMigration {

    // Query to find entrez gene ids linked to more than one gene
    private static final String SELECT_ENTREZ_GENES = "SELECT ENTREZ_GENE_ID FROM GENE_ENTREZ_GENE\n" +
            "GROUP BY ENTREZ_GENE_ID HAVING COUNT(1) > 1";

    private static final String SELECT_GENES = "SELECT GENE_ID FROM GENE_ENTREZ_GENE WHERE ENTREZ_GENE_ID = ?";

    private static final String DELETE_FROM_ENTREZ_GENE = "DELETE FROM GENE_ENTREZ_GENE WHERE ENTREZ_GENE_ID = ?";

    private static final String SELECT_GENOMIC_CONTEXT =
            "SELECT ID FROM GENOMIC_CONTEXT WHERE GENE_ID = ?";

    private static final String SELECT_AUTHOR_REPORTED_GENE =
            "SELECT REPORTED_GENE_ID FROM AUTHOR_REPORTED_GENE WHERE REPORTED_GENE_ID = ?";

    private static final String DELETE_FROM_GENE = "DELETE FROM GENE WHERE ID= ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of orphan genes
        jdbcTemplate.query(SELECT_ENTREZ_GENES, (resultSet, i) -> {
            Long entrezGeneId = resultSet.getLong(1);

            // Get a list of genes linked to entrez_gene_id
            List<Long> geneIds = jdbcTemplate.queryForList(SELECT_GENES, Long.class, entrezGeneId);

            // Delete from ENSEMBL_GENE
            jdbcTemplate.update(DELETE_FROM_ENTREZ_GENE, entrezGeneId);


            for (Long geneId : geneIds) {
                // If the gene isn't used anywhere then remove,
                // Ensure gene is not used in GENOMIC CONTEXT or AUTHOR_REPORTED_GENE
                List<Long> genomicContexts = jdbcTemplate.queryForList(SELECT_GENOMIC_CONTEXT, Long.class, geneId);
                List<Long> reportedGeneIds = jdbcTemplate.queryForList(SELECT_AUTHOR_REPORTED_GENE, Long.class, geneId);

                if (genomicContexts.isEmpty() && reportedGeneIds.isEmpty()) {
                    // Delete gene from table
                    jdbcTemplate.update(DELETE_FROM_GENE, geneId);
                }
            }

            return null;
        });
    }
}
