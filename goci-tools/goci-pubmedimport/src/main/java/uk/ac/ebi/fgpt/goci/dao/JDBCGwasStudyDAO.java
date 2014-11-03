package uk.ac.ebi.fgpt.goci.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import uk.ac.ebi.fgpt.goci.lang.ImporterProperties;
import uk.ac.ebi.fgpt.goci.model.DefaultGwasStudy;
import uk.ac.ebi.fgpt.goci.model.GwasStudy;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * A basic implementation of a StudyDAO that takes a spring jdbc template as a parameter and sends SQL queries to this
 * template.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class JDBCGwasStudyDAO implements GwasStudyDAO {

    public static final String STUDY_SELECT =
            "select ID, PMID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE from ";

    public static final String STUDY_LIMIT =
            " and ROWNUM < 2";



    public static final String STUDY_INSERT =
            "insert into ";

    public static final String INSERT_FIELDS =
                    "(PMID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE) " +
                    "values (?, ?, ?, ?, ?)";


    public static final String GWASSTUDY_INSERT_FIELDS =
            "(PMID, AUTHOR, STUDYDATE, PUBLICATION, LINKTITLE, AUTHOR_ORIGINAL, AUTHORSEARCH, LINK, CURATORID, CURATORSTATUSID, LASTUPDATEDATE) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private JdbcTemplate jdbcTemplate;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Collection<GwasStudy> getAllStudies(String table) {
        String query = STUDY_SELECT + table;
        return getJdbcTemplate().query(query, new StudyMapper());
    }


    public GwasStudy getStudyByPubMedID(String pubmedID, String table) {
        String query = STUDY_SELECT + table + " where PMID = ?" + STUDY_LIMIT;
        String[] pmid = {pubmedID};

        try {
            return getJdbcTemplate().queryForObject(query, pmid, new StudyMapper());
        }
        catch (EmptyResultDataAccessException e) {
            // if there is no study with this pubmed ID
            return null;
        }
        catch(IncorrectResultSizeDataAccessException e){
            e.printStackTrace();
            return new DefaultGwasStudy(pubmedID, null, null, null, null);

        }
    }

    public boolean studyExists(String pubmedID){
        String gwas = "gwasstudies";
        String notgwas = "notgwasstudies";
        String unclassified = "unclassifiedstudies";

        boolean existing = false;

        if(getStudyByPubMedID(pubmedID, gwas) != null){
            existing = true;
            getLog().info("Pubmed ID " + pubmedID + " already exists in table gwasstudies");

        }
        else if(getStudyByPubMedID(pubmedID, notgwas) != null){
            existing = true;
            getLog().info("Pubmed ID " + pubmedID + " already exists in table notgwasstudies");

        }
        else if(getStudyByPubMedID(pubmedID, unclassified) != null){
            existing = true;
            getLog().info("Pubmed ID " + pubmedID + " already exists in table unclassifiedstudies");

        }

        return existing;

    }

    public void saveStudy(GwasStudy study) {
        Assert.notNull(getJdbcTemplate(), "JDBC Template must not be null");

        if (study.getPubMedID() != null) {
            getLog().debug("Saving study: " + study.getPubMedID());

            if(ImporterProperties.getOutputTable().equals("gwasstudies")){
                String insertStatement = STUDY_INSERT + ImporterProperties.getOutputTable() + GWASSTUDY_INSERT_FIELDS;

                String link = "www.ncbi.nlm.nih.gov/pubmed/".concat(study.getPubMedID());
                int status = 21;
                int curator = 5;


                Date current = new Date(System.currentTimeMillis());



                try{
                    int result = getJdbcTemplate().update(insertStatement,
                            new Object[] {study.getPubMedID(),
                                    study.getAuthor(),
                                    study.getPublicationDate(),
                                    study.getPublication(),
                                    study.getTitle(),
                                    study.getAuthor(),
                                    study.getAuthor(),
                                    link,
                                    curator,
                                    status,
                                    current});
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{

                String insertStatement = STUDY_INSERT + ImporterProperties.getOutputTable() + INSERT_FIELDS;

                try{
                    int result = getJdbcTemplate().update(insertStatement,
                                         new Object[] {study.getPubMedID(),
                                         study.getAuthor(),
                                         study.getPublicationDate(),
                                         study.getPublication(),
                                         study.getTitle()});
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

    }

    private class StudyMapper implements RowMapper<GwasStudy> {

        public GwasStudy mapRow(ResultSet resultSet, int i) throws SQLException {

            int id = resultSet.getInt(1);
            String pubMedId = resultSet.getString(2);
            String author = resultSet.getString(3);
            Date studydate = resultSet.getDate(4);
            String publication = resultSet.getString(5);
            String title = resultSet.getString(6);

            DefaultGwasStudy study = new DefaultGwasStudy(pubMedId, author, studydate, publication, title);
            study.setId(id);

            getLog().trace(id + "\t" + pubMedId + "\t" + author + "\t" + studydate + "\t" + publication  + "\t" + title);

            return study;
        }
    }
}
