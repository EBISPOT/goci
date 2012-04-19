package uk.ac.ebi.fgpt.goci.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import uk.ac.ebi.fgpt.goci.model.DatabaseRecoveredGociStudy;
import uk.ac.ebi.fgpt.goci.model.GociStudy;
import uk.ac.ebi.fgpt.goci.model.GociUser;
import uk.ac.ebi.fgpt.goci.model.UserCreatedGociStudy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic implementation of a StudyDAO that takes a spring jdbc template as a parameter and sends SQL queries to this
 * template.
 *
 * @author Tony Burdett
 * Date 27/10/11
 */
public class JDBCGociStudyDAO implements GociStudyDAO {
    public static final String SEQUENCE_SELECT =
            "call next value for SEQ_GOCI";

    public static final String STUDY_SELECT =
            "select ID, PUBMED_ID, TITLE, ABSTRACT, USER_ID, STATE, GWAS_ELIGIBILITY " +
                    "from STUDY";
    
    public static final String STUDY_SELECT_TODO_FILTER = STUDY_SELECT + " " +     
    		"where (STATE <> 'Published_to_catalog') and (GWAS_ELIGIBILITY <> 'Not_GWAS')";     

    public static final String STUDY_SELECT_BY_ID = STUDY_SELECT + " " +
            "where ID = ?";
    public static final String STUDY_SELECT_BY_STATE = STUDY_SELECT + " " +
            "where STATE = ?";
    public static final String STUDY_SELECT_BY_USER_ID = STUDY_SELECT + " " +
            "where USER_ID = ?";
    public static final String STUDY_SELECT_BY_PUBMED_ID = STUDY_SELECT + " " +
            "where PUBMED_ID = ?";

    public static final String STUDY_INSERT =
            "insert into STUDY ( " +
                    "ID, PUBMED_ID, TITLE, ABSTRACT, USER_ID, STATE, GWAS_ELIGIBILITY) " +
                    "values (?, ?, ?, ?, ?, ?, ?)";
    public static final String STUDY_UPDATE =
            "update STUDY set " +
                    "PUBMED_ID = ?, TITLE = ?, ABSTRACT = ?, USER_ID = ?, STATE = ?, GWAS_ELIGIBILITY = ? " +
                    "where ID = ?";

    private final GociStudyListener listener;

    private JdbcTemplate jdbcTemplate;
    private GociUserDAO userDAO;

    private Logger log = LoggerFactory.getLogger(getClass());

    public JDBCGociStudyDAO() {
        listener = new GociStudyListener() {
            public void studyUpdated(GociEvent evt) {
                getLog().debug("Listener event triggered from " + evt.getStudy());
                saveStudy(evt.getStudy());
                getLog().debug("Study saved!");
            }
        };
    }

    protected Logger getLog() {
        return log;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public GociUserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(GociUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public GociStudy getStudy(String studyID) {
        return getJdbcTemplate().queryForObject(STUDY_SELECT_BY_ID, new StudyMapper(), studyID);
    }

    public Collection<GociStudy> getAllStudies() {
        return getJdbcTemplate().query(STUDY_SELECT, new StudyMapper());
    }
    
    public Collection<GociStudy> getProcessableStudies() {
    	return getJdbcTemplate().query(STUDY_SELECT_TODO_FILTER, new StudyMapper());
    }

    public Collection<GociStudy> getStudiesByState(GociStudy.State studyState) {
        return getJdbcTemplate().query(STUDY_SELECT_BY_STATE, new StudyMapper(), studyState.toString());
    }

    public Collection<GociStudy> getStudiesByUser(GociUser user) {
        return getJdbcTemplate().query(STUDY_SELECT_BY_USER_ID, new StudyMapper(), user.getId());
    }

    public GociStudy getStudyByPubMedID(String pubmedID) {
        try {
            return getJdbcTemplate().queryForObject(STUDY_SELECT_BY_PUBMED_ID, new StudyMapper(), pubmedID);
        }
        catch (EmptyResultDataAccessException e) {
            // if there is no study with this pubmed ID
            return null;
        }
    }

    public void saveStudy(GociStudy study) {
        Assert.notNull(getJdbcTemplate(), "JDBC Template must not be null");
        String ownerID = (study.getOwner() == null ? null : study.getOwner().getId());

        if (study.getID() == null) {
            if (study instanceof UserCreatedGociStudy) {
                int studyID = getJdbcTemplate().queryForInt(SEQUENCE_SELECT);
                ((UserCreatedGociStudy) study).setID(Integer.toString(studyID));
                getLog().debug("Saving study:\n" + study.toString());
                getJdbcTemplate().update(STUDY_INSERT,
                                         study.getID(),
                                         study.getPubMedID(),
                                         study.getTitle(),
                                         study.getPaperAbstract(),
                                         ownerID,
                                         study.getState().toString(),
                                         study.getGwasEligibility().toString());
            }
            else {
                getLog().error("Unable to save this study: it was not user created " +
                                       "(actually " + study.getClass() + ")");
                throw new RuntimeException("Unable to save this study: it was not user created " +
                                                   "(actually " + study.getClass() + ")");
            }
        }
        else {
            getLog().debug("Updating study:\n" + study.toString());
            int result = getJdbcTemplate().update(STUDY_UPDATE,
                                                  study.getPubMedID(),
                                                  study.getTitle(),
                                                  study.getPaperAbstract(),
                                                  ownerID,
                                                  study.getState().toString(),
                                                  study.getGwasEligibility().toString(),
                                                  study.getID());
            getLog().debug("Updated complete: " + result + " rows were affected");
        }
    }

    private class StudyMapper implements RowMapper<GociStudy> {
        private Map<String, GociUser> userCache = new HashMap<String, GociUser>();

        public GociStudy mapRow(ResultSet resultSet, int i) throws SQLException {
            GociUser owner;
            String userID = resultSet.getString(5);
            if (userCache.containsKey(userID)) {
                owner = userCache.get(userID);
            }
            else {
                if (userID == null) {
                    owner = null;
                }
                else {
                    owner = getUserDAO().getUser(userID);
                    userCache.put(userID, owner);
                }
            }

            DatabaseRecoveredGociStudy study = new DatabaseRecoveredGociStudy();
            study.setID(resultSet.getString(1));
            study.setPubMedID(resultSet.getString(2));
            study.setTitle(resultSet.getString(3));
            study.setPaperAbstract(resultSet.getString(4));
            study.setOwner(owner);
            study.setState(GociStudy.State.valueOf(resultSet.getString(6)));
            study.setGwasEligibility(GociStudy.Eligibility.valueOf(resultSet.getString(7)));
            study.addListener(listener);

            return study;
        }
    }
}
