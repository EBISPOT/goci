package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove new lines from GENE_NAME in GENE table.
 *         <p>
 *         https://www.ebi.ac.uk/panda/jira/browse/GOCI-1020
 */
public class V2_0_1_040__Remove_new_lines_from_gene_names implements SpringJdbcMigration {

    // Query to find gene names with new lines
    private static final String SELECT_GENES =
            "SELECT * FROM GENE WHERE INSTR(GENE_NAME, CHR(10)) > 0";

    private static final String UPDATE_GENE = "UPDATE GENE \n" +
            "SET GENE_NAME = ?" +
            "WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_GENES, (resultSet, i) -> {
            Long geneId = resultSet.getLong(1);
            String geneName = resultSet.getString(2);

            String newline = System.getProperty("line.separator");

            if (geneName.contains(newline)) {

                // Remove new line
                String updatedGeneName = geneName.replace(newline, "");

                // Update gene name
                jdbcTemplate.update(UPDATE_GENE, updatedGeneName, geneId);
            }
            return null;
        });
    }
}
