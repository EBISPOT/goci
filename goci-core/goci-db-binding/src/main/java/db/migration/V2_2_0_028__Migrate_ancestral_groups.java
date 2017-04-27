package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 13/04/2017.
 */
public class V2_2_0_028__Migrate_ancestral_groups implements SpringJdbcMigration {

    private static final String SELECT_ANCESTRAL_GROUP =
            "SELECT DISTINCT ID, ANCESTRAL_GROUP FROM ANCESTRY WHERE ANCESTRAL_GROUP IS NOT NULL";

    private static final String SELECT_ANCESTRAL_GROUP_NAMES =
            "SELECT DISTINCT * FROM ANCESTRAL_GROUP";
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        IdAndStringRowHandler ancestralGroupHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_ANCESTRAL_GROUP, ancestralGroupHandler);
        final Map<Long, String> ancestryIdToAncestralGroup = ancestralGroupHandler.getIdToStringMap();

        IdAndStringRowHandler ancestralGroupNameHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_ANCESTRAL_GROUP_NAMES, ancestralGroupNameHandler);
        final Map<Long, String> agIdToAg = ancestralGroupNameHandler.getIdToStringMap();

        final Map<String, Long> ancestral_groups = new HashMap<>();
        for(Long id : agIdToAg.keySet()){
            String ag = agIdToAg.get(id);
            ancestral_groups.put(ag, id);
        }

        SimpleJdbcInsert insertStudyAncestry =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("ANCESTRY_ANCESTRAL_GROUP")
                        .usingColumns("ANCESTRY_ID", "ANCESTRAL_GROUP_ID");


        for(Long ancestryId : ancestryIdToAncestralGroup.keySet()){
            String ancestry= ancestryIdToAncestralGroup.get(ancestryId);

            if(ancestry != null){
                List<String> ags = new ArrayList<>();


                for(String ag : ancestral_groups.keySet()){
                    if(ancestry.contains(ag)){
                        ags.add(ag);
                    }
                }

                for(String ag : ags){
                    Long aId = ancestral_groups.get(ag);

                    Map<String, Object> ancestryAncestralGroupArgs = new HashMap<>();
                    ancestryAncestralGroupArgs.put("ANCESTRY_ID", ancestryId);
                    ancestryAncestralGroupArgs.put("ANCESTRAL_GROUP_ID", aId);
                    insertStudyAncestry.execute(ancestryAncestralGroupArgs);
                }
            }
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

