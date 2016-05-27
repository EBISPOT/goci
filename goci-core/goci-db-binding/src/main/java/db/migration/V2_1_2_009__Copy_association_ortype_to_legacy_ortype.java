package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by emma on 17/11/2015.
 *
 * @author emma
 *         <p>
 *         Copy OR_TYPE in ASSOCIATION table to LEGACY_OR_TYPE.
 */
public class V2_1_2_009__Copy_association_ortype_to_legacy_ortype implements SpringJdbcMigration {

    // Query to find descriptions with leading or trailing whitespace
    private static final String SELECT_ASSOCIATIONS = "SELECT ID , OR_TYPE FROM ASSOCIATION";

    private static final String UPDATE_ASSOCIATION = "UPDATE ASSOCIATION \n" +
            "SET LEGACY_OR_TYPE = ?" +
            "WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        jdbcTemplate.query(SELECT_ASSOCIATIONS, (resultSet, i) -> {
            Long id = resultSet.getLong(1);
            Integer orType = resultSet.getInt(2);
            // Update
            jdbcTemplate.update(UPDATE_ASSOCIATION, orType, id);
            return null;
        });
    }
}