package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         Jira ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-951. Populate GENE_ENTREZ_GENE table with details
 *         from GENE table.
 */
public class V2_0_1_016__Populate_gene_entrez_gene_table implements SpringJdbcMigration {

    private static final String SELECT_GENE_ID_AND_ENTREZ_ID = "SELECT ID, ENTREZ_GENE_ID FROM GENE\n" +
            "WHERE ENTREZ_GENE_ID IS NOT NULL\n" +
            "ORDER BY ID";


    private static final String SELECT_ENTREZ_GENE_ID = "SELECT ID FROM ENTREZ_GENE \n" +
            "WHERE ENTREZ_GENE_ID = ?";

    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Query for gene IDs and its linked entrezGeneId
        jdbcTemplate.query(SELECT_GENE_ID_AND_ENTREZ_ID, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);
            String entrezIdinGeneTable = resultSet.getString(2);

            // Find ID of entrez gene in ENTREZ_GENE table linked to that ID
            Long idInEntrezGeneTable =
                    jdbcTemplate.queryForObject(SELECT_ENTREZ_GENE_ID, Long.class, entrezIdinGeneTable);

            // Insert into new table
            SimpleJdbcInsert insertGeneEntrezGene =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("GENE_ENTREZ_GENE")
                            .usingColumns("GENE_ID",
                                          "ENTREZ_GENE_ID");

            Map<String, Object> insertArgs = new HashMap<>();
            insertArgs.put("GENE_ID", geneId);
            insertArgs.put("ENTREZ_GENE_ID", idInEntrezGeneTable);
            insertGeneEntrezGene.execute(insertArgs);

            return null;
        });
    }
}
