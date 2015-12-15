package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove IDs from ENSEMBL_GENE and ENTREZ_GENE that have no links to a gene
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-1020
 */
public class V2_0_1_043__Remove_orphan_external_gene_ids implements SpringJdbcMigration {

    // Query to find gene names with new lines
    private static final String SELECT_ENSEMBL_GENES =
            "SELECT ID FROM ENSEMBL_GENE WHERE ID NOT IN (SELECT ENSEMBL_GENE_ID FROM GENE_ENSEMBL_GENE)";

    private static final String SELECT_ENTREZ_GENES =
            "SELECT ID FROM ENTREZ_GENE WHERE ID NOT IN (SELECT ENTREZ_GENE_ID FROM GENE_ENTREZ_GENE)";

    private static final String DELETE_FROM_ENSEMBL_GENE = "DELETE FROM ENSEMBL_GENE WHERE ID = ?";

    private static final String DELETE_FROM_ENTREZ_GENE = "DELETE FROM ENTREZ_GENE WHERE ID = ?";


    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Get list of orphan genes
        jdbcTemplate.query(SELECT_ENSEMBL_GENES, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);

            // Delete from ENSEMBL_GENE
            jdbcTemplate.update(DELETE_FROM_ENSEMBL_GENE, geneId);

            return null;
        });

        // Get list of orphan genes
        jdbcTemplate.query(SELECT_ENTREZ_GENES, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);

            // Delete from ENTREZ_GENE
            jdbcTemplate.update(DELETE_FROM_ENTREZ_GENE, geneId);

            return null;
        });

    }
}
