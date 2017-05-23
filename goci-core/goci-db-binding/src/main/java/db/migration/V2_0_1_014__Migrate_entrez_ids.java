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
 *         Jira ticket: https://www.ebi.ac.uk/panda/jira/browse/GOCI-951. Migrate Entrez gene IDs from GENE table to new
 *         ENTREZ_GENE table
 */
public class V2_0_1_014__Migrate_entrez_ids implements SpringJdbcMigration {

    private static final String SELECT_ENTREZ_GENE_IDS = "SELECT DISTINCT ENTREZ_GENE_ID \n" +
            "FROM GENE\n" +
            "WHERE ENTREZ_GENE_ID IS NOT NULL";


    @Override public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // Query for gene IDs
        jdbcTemplate.query(SELECT_ENTREZ_GENE_IDS, (resultSet, i) -> {
            String geneId = resultSet.getString(1);

            // Insert into new table
            SimpleJdbcInsert insertEntrezId =
                    new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName("ENTREZ_GENE")
                            .usingColumns("ENTREZ_GENE_ID");

            Map<String, Object> insertArgs = new HashMap<>();
            insertArgs.put("ENTREZ_GENE_ID", geneId);
            insertEntrezId.execute(insertArgs);

            return null;
        });
    }
}
