package uk.ac.ebi.fgpt.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dwelter
 * Date: 07/06/13
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class PMIDAcquisitionService {
    private JdbcTemplate jdbcTemplate;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    public static final String PMID_SELECT =
            "select distinct PMID from ";


    public List<String> getPMIDs(String tablename){
        String query = PMID_SELECT + tablename;

        try{
            return getJdbcTemplate().queryForList(query, String.class);
        }
        catch (EmptyResultDataAccessException e){
            return null;
        }
    }

}




