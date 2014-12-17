package uk.ac.ebi.spot.goci.dataloader;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
  * Created with IntelliJ IDEA.
  * User: dwelter
  * Date: 22/04/13
  * Time: 11:53
  * To change this template use File | Settings | File Templates.
  */
 public class DBLoader {

    private static final String QUERY = "select * from GWASEFOTRAITS";

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<DBEntry> retrieveAllEntries() {
        String query = QUERY;
        return getJdbcTemplate().query(query, new DataMapper());
    }


    private class DataMapper implements RowMapper<DBEntry> {
        public DBEntry mapRow(ResultSet resultSet, int i) throws SQLException {
            int dbid = resultSet.getInt(1);
            String efotrait = resultSet.getString(2);
            String efouri = resultSet.getString(3);

            return new DBEntry(dbid, efotrait, efouri);
        }
    }

}
