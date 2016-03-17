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
 * Created by dwelter on 11/03/16.
 */
public class V2_1_1_003__Split_platform_array_info implements SpringJdbcMigration {

    private static final String SELECT_PLATFORM =
            "SELECT DISTINCT ID, PLATFORM FROM STUDY WHERE PLATFORM IS NOT NULL";

    private static final String SELECT_MANUFACTURERS =
            "SELECT DISTINCT * FROM PLATFORM";

    private static final String UPDATE_STUDY_POOLED =
            "UPDATE STUDY SET POOLED = ?, SNP_COUNT = ?, QUALIFIER = ?, IMPUTED = ?, STUDY_DESIGN_COMMENT = ? WHERE ID = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        IdAndStringRowHandler platformHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_PLATFORM, platformHandler);
        final Map<Long, String> studyIdToPlatform = platformHandler.getIdToStringMap();

        IdAndStringRowHandler manHandler = new IdAndStringRowHandler();
        jdbcTemplate.query(SELECT_MANUFACTURERS, manHandler);
        final Map<Long, String> manIdToMan = manHandler.getIdToStringMap();
        final Map<String, Long> manufacturers = new HashMap<>();

        for(Long id : manIdToMan.keySet()){
            String man = manIdToMan.get(id);
            manufacturers.put(man, id);
        }

        List<String> qualifiers = new ArrayList<>();
        qualifiers.add("up to");
        qualifiers.add("at least");
        qualifiers.add("~");
        qualifiers.add(">");


        SimpleJdbcInsert insertStudyPlatform =
                new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("STUDY_PLATFORM")
                    .usingColumns("STUDY_ID", "PLATFORM_ID");


        for(Long studyId : studyIdToPlatform.keySet()){
            String platform = studyIdToPlatform.get(studyId);

            if(platform != null){
                List<String> manufacturer = new ArrayList<>();
                int imputed = 0;
                int pooled = 0;
                Integer snpCount = null;
                List<String> qualifier = new ArrayList<>();
                String qual = null;
                String studyDesignComment = null;

                if(platform.equals("NR")){
                    studyDesignComment = platform;
                }
                else {
                    for(String man : manufacturers.keySet()){
                        if(platform.contains(man)){
                            manufacturer.add(man);
                        }
                    }

                    if(manufacturer.size() == 0){
                        studyDesignComment = platform;
                    }

                    if(platform.contains("imputed")){
                        imputed = 1;
                    }
                    if(platform.contains("pooled")){
                        pooled = 1;
                    }

                    if(platform.contains("SNP") || platform.contains("unsure") || platform.contains("UNSURE") || platform.contains("CNV") || platform.contains("aplotype")){
                        studyDesignComment = platform;
                    }
                    else{
                        if(platform.contains("[") && platform.indexOf("[") == platform.lastIndexOf("[")) {
                            int start = platform.indexOf("[") +1;
                            int finish = platform.indexOf("]");

                            String count = platform.substring(start, finish).trim();

                            if(!count.equals("NR")) {
                                for (String q : qualifiers) {
                                    if (count.contains(q)) {
                                        qualifier.add(q);

                                        count = count.replace(q, "").trim();
                                    }
                                }

                                if (count.contains("million")) {
                                    count = count.replace("million", "").trim();

                                    if (count.contains(",")) {
                                        count = count.replace(",", "").trim();
                                    }

                                    double c = Double.parseDouble(count);

                                    snpCount = (int) (c * 1000000);
                                }

                                else if (count.contains(",") || count.contains(".")) {
                                    count = count.replace(",", "").trim();
                                    count = count.replace(".", "").trim();
                                    snpCount = Integer.parseInt(count);
                                }
                                else if (!count.equals("")){
                                    snpCount = Integer.parseInt(count);
                                }
                                else{
                                    studyDesignComment = platform;
                                }
                            }
                        }
                        else {
                            studyDesignComment = platform;
                        }
                    }
                }

                if(qualifier.size() > 1){
                    studyDesignComment = platform;
                }
                else if(qualifier.size() == 1){
                    qual = qualifier.get(0);
                }


                jdbcTemplate.update(UPDATE_STUDY_POOLED,
                                    pooled,
                                    snpCount,
                                    qual,
                                    imputed,
                                    studyDesignComment,
                                    studyId);

                for(String man : manufacturer){
                    Long pId = manufacturers.get(man);

                    Map<String, Object> studyPlatformArgs = new HashMap<>();
                    studyPlatformArgs.put("STUDY_ID", studyId);
                    studyPlatformArgs.put("PLATFORM_ID", pId);
                    insertStudyPlatform.execute(studyPlatformArgs);
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
