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
 * Created by dwelter on 24/04/17.
 */
public class V2_2_0_030__Migrate_ancestry_countries implements SpringJdbcMigration {

    private static final String SELECT_COUNTRY_OF_ORIGIN =
            "SELECT DISTINCT ID, COUNTRY_OF_ORIGIN FROM ANCESTRY WHERE COUNTRY_OF_ORIGIN IS NOT NULL";

    private static final String SELECT_COUNTRY_OF_RECRUITMENT =
            "SELECT DISTINCT ID, COUNTRY_OF_RECRUITMENT FROM ANCESTRY WHERE COUNTRY_OF_RECRUITMENT IS NOT NULL";

    private static final String SELECT_COUNTRY_NAMES =
            "SELECT DISTINCT ID, COUNTRY_NAME FROM COUNTRY";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        IdAndStringRowHandler
                countryOfOriginHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_COUNTRY_OF_ORIGIN, countryOfOriginHandler);
        final Map<Long, String> ancestryIdToCountryOfOrigin = countryOfOriginHandler.getIdToStringMap();

        IdAndStringRowHandler
                countryOfRecruitmentHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_COUNTRY_OF_RECRUITMENT, countryOfRecruitmentHandler);
        final Map<Long, String> ancestryIdToCountryOfRecruitment = countryOfRecruitmentHandler.getIdToStringMap();

        IdAndStringRowHandler
                countryNameHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_COUNTRY_NAMES, countryNameHandler);
        final Map<Long, String> countryIdToCountry = countryNameHandler.getIdToStringMap();

        final Map<String, Long> countries = new HashMap<>();
        for(Long id : countryIdToCountry.keySet()){
            String c = countryIdToCountry.get(id);
            countries.put(c, id);
        }

        SimpleJdbcInsert insertAncestryCountryOfOrigin =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("ANCESTRY_COUNTRY_OF_ORIGIN")
                        .usingColumns("ANCESTRY_ID", "COUNTRY_ID");


        for(Long ancestryId : ancestryIdToCountryOfOrigin.keySet()){
            String ancestry= ancestryIdToCountryOfOrigin.get(ancestryId);

            if(ancestry != null){
                List<String> coos = new ArrayList<>();


                for(String co : countries.keySet()){
                    if(ancestry.contains(co)){
                        coos.add(co);
                    }
                }

                for(String c : coos){
                    Long countryId = countries.get(c);

                    Map<String, Object> ancestryCountryOfOriginArgs = new HashMap<>();
                    ancestryCountryOfOriginArgs.put("ANCESTRY_ID", ancestryId);
                    ancestryCountryOfOriginArgs.put("COUNTRY_ID", countryId);
                    insertAncestryCountryOfOrigin.execute(ancestryCountryOfOriginArgs);
                }
            }
        }

        SimpleJdbcInsert insertAncestryCountryOfRecruitment =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("ANCESTRY_COUNTRY_RECRUITMENT")
                        .usingColumns("ANCESTRY_ID", "COUNTRY_ID");

        for(Long ancestryId : ancestryIdToCountryOfRecruitment.keySet()){
            String ancestry= ancestryIdToCountryOfRecruitment.get(ancestryId);

            if(ancestry != null){
                List<String> cors = new ArrayList<>();


                for(String cor : countries.keySet()){
                    if(ancestry.contains(cor)){
                        cors.add(cor);
                    }
                }

                for(String c : cors){
                    Long countryId = countries.get(c);

                    Map<String, Object> ancestryCountryOfRecruitmentArgs = new HashMap<>();
                    ancestryCountryOfRecruitmentArgs.put("ANCESTRY_ID", ancestryId);
                    ancestryCountryOfRecruitmentArgs.put("COUNTRY_ID", countryId);
                    insertAncestryCountryOfRecruitment.execute(ancestryCountryOfRecruitmentArgs);
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
