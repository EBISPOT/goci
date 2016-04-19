package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove whitespace from GENE_NAME in GENE table.
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-1020
 */
public class V2_0_1_039__Trim_gene_names implements SpringJdbcMigration {

    // Query to find gene names with leading or trailing whitespace
    private static final String SELECT_GENES =
            "SELECT * FROM GENE \n" +
                    "WHERE REGEXP_LIKE(GENE_NAME, '^[ ]+.*')\n" +
                    "OR REGEXP_LIKE(GENE_NAME, '.*[ ]+$')\n" +
                    "ORDER BY ID";

    private static final String UPDATE_GENE = "UPDATE GENE \n" +
            "SET GENE_NAME = ?" +
            "WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_GENES, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);
            String geneName = resultSet.getString(2);

            if (!geneName.isEmpty()) {

                // Trim name
                String updatedGeneName = geneName.trim();

                // Update gene name
                jdbcTemplate.update(UPDATE_GENE, updatedGeneName, geneId);
            }
            return null;
        });
    }
}
