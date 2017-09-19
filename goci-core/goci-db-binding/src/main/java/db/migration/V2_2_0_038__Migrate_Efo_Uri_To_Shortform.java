package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwelter on 18/09/17.
 */
public class V2_2_0_038__Migrate_Efo_Uri_To_Shortform implements SpringJdbcMigration {


    private static final String SELECT_URI =
            "SELECT DISTINCT ID, URI FROM EFO_TRAIT";

    private static final String UPDATE_EFO_TRAIT_SHORTFORM =
            "UPDATE EFO_TRAIT SET SHORT_FORM = ? WHERE ID = ?";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        IdAndStringRowHandler uriHandler = new IdAndStringRowHandler();

        jdbcTemplate.query(SELECT_URI, uriHandler);

        final Map<Long, String> efoIdToUri = uriHandler.getIdToStringMap();

        for(Long id : efoIdToUri.keySet()){
            String uri = efoIdToUri.get(id);

            String[] elements = uri.split("/");

            int last = elements.length-1;

            String shortForm = elements[last];

            jdbcTemplate.update(UPDATE_EFO_TRAIT_SHORTFORM,
                                shortForm,
                                id);
        }


    }

    public class IdAndStringRowHandler implements RowCallbackHandler {
        private Map<Long, String> idToStringMap;

        public IdAndStringRowHandler() {
            this.idToStringMap = new HashMap<>();
        }

        @Override public void processRow(ResultSet resultSet) throws SQLException {
            idToStringMap.put(resultSet.getLong(1), resultSet.getString(2).trim());
        }

        public Map<Long, String> getIdToStringMap() {
            return idToStringMap;
        }
    }
}
