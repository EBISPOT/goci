package db.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import uk.ac.ebi.spot.goci.model.Study;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwelter on 18/09/17.
 */
public class V2_2_0_041__Study_To_Publication implements SpringJdbcMigration {


    private static final String SELECT_STUDY =
            "SELECT DISTINCT PUBMED_ID,PUBLICATION,TITLE,PUBLICATION_DATE FROM STUDY WHERE PUBMED_ID is not NULL  ORDER BY PUBMED_ID";

    private static final String UPDATE_STUDY_PUBLICATION_ID =
            "UPDATE STUDY SET PUBLICATION_ID = ? WHERE PUBMED_ID = ?";


    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        IdAndStringRowHandler uriHandler = new IdAndStringRowHandler();

        jdbcTemplate.query(SELECT_STUDY, uriHandler);

        final Map<String, Map<String, Object>> pubmedIdStudies = uriHandler.getListStudyMap();
        SimpleJdbcInsert insertPublication =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("PUBLICATION")
                        .usingColumns("PUBMED_ID","PUBLICATION","TITLE","PUBLICATION_DATE")
                        .usingGeneratedKeyColumns("ID");

        for(String pubmedId: pubmedIdStudies.keySet()){
            System.out.print(".");
            pubmedId = pubmedId.trim();
            try {
                Map<String, Object> newPublication = pubmedIdStudies.get(pubmedId);
                Number publicationID = insertPublication.executeAndReturnKey(newPublication);
                jdbcTemplate.update(UPDATE_STUDY_PUBLICATION_ID,
                        publicationID,
                        pubmedId);
            } catch (Exception exception) {
                System.out.println("");
                System.out.print("Exception raised:");
                System.out.println(pubmedId);
            }
        }


    }

    public class IdAndStringRowHandler implements RowCallbackHandler {
        private Map<String, Map<String, Object>> listStudyMap;
        //private Map<String, ArrayList<Object>> pubmedIdToStringMap;

        public IdAndStringRowHandler() {
            //this.pubmedIdToStringMap = new HashMap<>();
            this.listStudyMap = new HashMap<>();
        }

        @Override public void processRow(ResultSet resultSet) throws SQLException {
                HashMap row = new HashMap<>();
                row.put("PUBLICATION", resultSet.getString("PUBLICATION"));
                row.put("PUBMED_ID",resultSet.getString("PUBMED_ID"));
                row.put("TITLE",resultSet.getString("TITLE"));
                row.put("PUBLICATION_DATE",resultSet.getDate("PUBLICATION_DATE"));
                listStudyMap.put(resultSet.getString("PUBMED_ID"),row);
        }


        public Map<String, Map<String, Object>> getListStudyMap() {
            return listStudyMap;
        }

    }
}
