package uk.ac.ebi.spot.goci.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by dwelter on 12/02/16.
 */
public class MappingsExportRepository {

    private static final String DBQUERY = 
            "SELECT DISTINCT D.TRAIT, E.TRAIT, E.URI  " +
            " FROM DISEASE_TRAIT D " +
            " JOIN STUDY_DISEASE_TRAIT SD ON SD.DISEASE_TRAIT_ID = D.ID " +
            " JOIN STUDY_EFO_TRAIT SE ON SE.STUDY_ID = SD.STUDY_ID " +
            " JOIN EFO_TRAIT E ON E.ID = SE.EFO_TRAIT_ID " +
            " UNION " +
            " SELECT DISTINCT D.TRAIT, E.TRAIT, E.URI " +
            " FROM DISEASE_TRAIT D " +
            " JOIN STUDY_DISEASE_TRAIT SD ON SD.DISEASE_TRAIT_ID = D.ID " +
            " JOIN ASSOICATION A ON A.STUDY_ID = SD.STUDY_ID " +
            " JOIN ASSOCIATION_EFO_TRAIT AE ON AE.ASSOCIATION_ID = A.ID " +
            " JOIN EFO_TRAIT E ON E.ID = AE.EFO_TRAIT_ID";

    private String[][] mappings;

    private JdbcTemplate jdbcTemplate;


    @Autowired
    public MappingsExportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public String[][] getMappings() {
        List<Trait> allTraits = retrieveData();

        for(Trait trait : allTraits){

        }
        return mappings;

    }


    public List<Trait> retrieveData(){
        return getJdbcTemplate().query(DBQUERY, new TraitMapper());
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    private class TraitMapper implements RowMapper<Trait> {
        public Trait mapRow(ResultSet rs, int i) throws SQLException {
            String trait = rs.getString(1).trim();
            String efoTerm = rs.getString(2).trim();
            String uri = rs.getString(3).trim();

            return  new Trait(trait, efoTerm, uri);
        }
    }

    private class Trait {
        private String trait;
        private String efoTerm;
        private String uri;

        private Trait(String trait, String efoTerm, String uri){
            this.trait = trait;
            this.efoTerm = efoTerm;
            this.uri = uri;
        }

        public String getTrait() {
            return trait;
        }

        public String getEfoTerm() {
            return efoTerm;
        }

        public String getUri() {
            return uri;
        }
    }
}


