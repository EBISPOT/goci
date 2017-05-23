package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Remove whitespace from DESCRIPTION in ASSOCIATION table.
 */
public class V2_1_2_004__Trim_association_description implements SpringJdbcMigration {

    // Query to find descriptions with leading or trailing whitespace
    private static final String SELECT_ASSOCIATIONS = "SELECT ID , DESCRIPTION \n" +
            "FROM ASSOCIATION WHERE REGEXP_LIKE(DESCRIPTION, '^[ ]+.*') \n" +
            "OR REGEXP_LIKE(DESCRIPTION, '.*[ ]+$')";

    private static final String UPDATE_ASSOCIATION = "UPDATE ASSOCIATION \n" +
            "SET DESCRIPTION = ?" +
            "WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            String description = resultSet.getString(2);

            if (!description.isEmpty()) {

                // Trim name
                String updatedDescription = description.trim();

                // Update
                jdbcTemplate.update(UPDATE_ASSOCIATION, updatedDescription, id);
            }
            return null;
        });
    }
}
