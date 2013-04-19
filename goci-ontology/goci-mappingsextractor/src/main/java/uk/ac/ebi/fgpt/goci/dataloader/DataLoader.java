package uk.ac.ebi.fgpt.goci.dataloader;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.lang.FilterProperties;
import uk.ac.ebi.fgpt.goci.processor.Mapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

/**
   * Created with IntelliJ IDEA.
   * User: dwelter
   * Date: 17/04/13
   * Time: 16:11
   * To change this template use File | Settings | File Templates.
   */
  public class DataLoader {
 
     private static final String SELECT_MAIN =
             "select distinct DISEASETRAIT, EFOTRAIT, EFOURI, PUBMEDID, AUTHOR, PUBDATE, JOURNAL from (" +
                        "SELECT distinct t.diseasetrait, e.efotrait, e.efouri, st.pmid as PUBMEDID, st.author, st.studydate AS PUBDATE, st.publication AS JOURNAL " +
                             "from gwasdiseasetraits t " +
                             "join gwasstudies st on st.diseaseid = t.id " +
                             "join gwasstudiessnp s on s.gwasid = st.id " +
                             "join gwasefosnpxref sx on sx.gwasstudiessnpid = s.id " +
                             "join gwasefotraits e on e.id = sx.traitid " +
                             "where st.publish = 1 ";

     private static final String SELECT_END = "order by st.pmid)";
 
     private JdbcTemplate jdbcTemplate;
 
     public JdbcTemplate getJdbcTemplate() {
         return jdbcTemplate;
     }
 
     public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
         this.jdbcTemplate = jdbcTemplate;
     }

    public Collection<Mapping> retrieveAllMappings() {
        String query = SELECT_MAIN;
        if(FilterProperties.getPvalueFilter() != null){
            String pval_filter = "and s.PVALUEFLOAT <= " + FilterProperties.getPvalueFilter() + " ";
            query = query.concat(pval_filter);
        }
        if (FilterProperties.getDateFilter() != null) {
            String date_filter = "and st.studydate < to_date('" + FilterProperties.getDateFilter()  + "', 'YYYY-MM-DD') ";
            query = query.concat(date_filter);
        }

        query = query.concat(SELECT_END);
        return getJdbcTemplate().query(query, new DataMapper());
    }


    private class DataMapper implements RowMapper<Mapping> {
        public Mapping mapRow(ResultSet resultSet, int i) throws SQLException {
            String diseasetrait = resultSet.getString(1);
            String efotrait = resultSet.getString(2);
            String efouri = resultSet.getString(3);
            String pmid = resultSet.getString(4);
            String author = resultSet.getString(5);
            Date pubdate = resultSet.getDate(6);
            String journal = resultSet.getString(7);

            return new Mapping(diseasetrait, efotrait, efouri, pmid, author, pubdate, journal);
        }
    }

}
