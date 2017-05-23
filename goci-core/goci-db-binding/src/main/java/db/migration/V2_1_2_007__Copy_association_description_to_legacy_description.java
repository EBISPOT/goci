package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Copy DESCRIPTION in ASSOCIATION table to LEGACY_DESCRIPTION.
 */
public class V2_1_2_007__Copy_association_description_to_legacy_description implements SpringJdbcMigration {

    // Query to find descriptions with leading or trailing whitespace
    private static final String SELECT_ASSOCIATIONS = "SELECT ID , DESCRIPTION FROM ASSOCIATION";

    private static final String UPDATE_ASSOCIATION = "UPDATE ASSOCIATION \n" +
            "SET LEGACY_DESCRIPTION = ?" +
            "WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            String description = resultSet.getString(2);

            if (description != null) {
                if (!description.isEmpty()) {
                    // Trim name
                    String legacyDescription = description.trim();

                    // Update
                    jdbcTemplate.update(UPDATE_ASSOCIATION, legacyDescription, id);
                }
            }
            return null;
        });
    }
}