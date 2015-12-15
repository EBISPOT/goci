package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove duplicate genes from GENE table.
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-1020
 */
public class V2_0_1_041__Remove_duplicate_genes implements SpringJdbcMigration {

    // Query to find gene names with new lines
    private static final String SELECT_DUPLICATE_GENES =
            "SELECT GENE_NAME FROM GENE GROUP BY GENE_NAME HAVING COUNT(1) > 1";

    private static final String SELECT_GENE_IDS =
            "SELECT ID FROM GENE WHERE GENE_NAME = ? ORDER BY ID";

    private static final String UPDATE_AUTHOR_REPORTED_GENE =
            "UPDATE AUTHOR_REPORTED_GENE SET REPORTED_GENE_ID = ? WHERE REPORTED_GENE_ID = ?";

    private static final String DELETE_FROM_GENOMIC_CONTEXT =
            "DELETE FROM GENOMIC_CONTEXT WHERE GENE_ID= ?";

    private static final String DELETE_FROM_GENE_ENSEMBL_GENE =
            "DELETE FROM GENE_ENSEMBL_GENE WHERE GENE_ID= ?";

    private static final String DELETE_FROM_GENE_ENTREZ_GENE =
            "DELETE FROM GENE_ENTREZ_GENE WHERE GENE_ID= ?";


    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of duplicate genes
        jdbcTemplate.query(SELECT_DUPLICATE_GENES, (resultSet, i) -> {
            String geneName = resultSet.getString(1);

            if (geneName != null && !geneName.isEmpty()) {
                List<Long> geneIdsLinkedToGeneName = jdbcTemplate.queryForList(SELECT_GENE_IDS, Long.class, geneName);

                // Save the first ID as the gene ID we want to keep and then remove it from list
                Long geneIdToKeep = geneIdsLinkedToGeneName.get(0);
                geneIdsLinkedToGeneName.remove(0);

                for (Long geneIdToRemove : geneIdsLinkedToGeneName) {

                    // Update locus author reported gene links
                    jdbcTemplate.update(UPDATE_AUTHOR_REPORTED_GENE, geneIdToKeep, geneIdToRemove);

                    // Remove genomic context
                    jdbcTemplate.update(DELETE_FROM_GENOMIC_CONTEXT, geneIdToRemove);

                    // Remove links from GENE_ENSEMBL_GENE and GENE_ENTREZ_GENE
                    jdbcTemplate.update(DELETE_FROM_GENE_ENSEMBL_GENE, geneIdToRemove);
                    jdbcTemplate.update(DELETE_FROM_GENE_ENTREZ_GENE, geneIdToRemove);
                }
            }

            return null;
        });
    }
}
