package uk.ac.ebi.fgpt.goci.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.lang.UniqueID;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A data access object capable of retrieving {@link uk.ac.ebi.fgpt.goci.model.Study} objects from the GWAS database
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public class StudyDAO extends Initializable {
    private static final String STUDY_SELECT =
            "select ID, AUTHOR, PUBLISHDATE, PMID from GWASSTUDIES where PMID is not null";

    private TraitAssociationDAO traitAssociationDAO;
    private JdbcTemplate jdbcTemplate;

    private Map<String, Set<TraitAssociation>> traitAssociationMap;

    public StudyDAO() {
        this.traitAssociationMap = new HashMap<String, Set<TraitAssociation>>();
    }

    public TraitAssociationDAO getTraitAssociationDAO() {
        return traitAssociationDAO;
    }

    public void setTraitAssociationDAO(TraitAssociationDAO traitAssociationDAO) {
        this.traitAssociationDAO = traitAssociationDAO;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected void doInitialization() {
        // select all trait associations and map them
        getLog().info("Fetching Trait Associations from the database ready to map to studies...");
        Collection<TraitAssociation> traitAssociations = getTraitAssociationDAO().retrieveAllTraitAssociations();
        for (TraitAssociation ta : traitAssociations) {
            if (!getTraitAssociationMap().containsKey(ta.getPubMedID())) {
                getTraitAssociationMap().put(ta.getPubMedID(), new HashSet<TraitAssociation>());
            }
            getTraitAssociationMap().get(ta.getPubMedID()).add(ta);
        }
        // we've populated all snps, so mark that we are ready
        getLog().info("Retrieved Trait Associations, " +
                               "mapped Traits for " + getTraitAssociationMap().keySet().size() + " studies");
    }

    public Collection<Study> retrieveAllStudies() {
        try {
            waitUntilReady();
            return getJdbcTemplate().query(STUDY_SELECT, new StudyMapper());
        }
        catch (InterruptedException e) {
            throw new ObjectMappingException(
                    "Unexpectedly interrupted whilst waiting for initialization to complete", e);
        }
    }

    private Map<String, Set<TraitAssociation>> getTraitAssociationMap() {
        return this.traitAssociationMap;
    }

    private class StudyMapper implements RowMapper<Study> {
        public Study mapRow(ResultSet resultSet, int i) throws SQLException {
            String id = resultSet.getString(1);
            String author = resultSet.getString(2);
            Date publishDate = resultSet.getDate(3);
            String pubmedID = resultSet.getString(4);
            return new StudyFromDB(id, author, publishDate, pubmedID);
        }
    }

    private class StudyFromDB implements Study {
        private String id;
        private String author;
        private Date publishDate;
        private String pubmedID;

        private Set<TraitAssociation> associations;

        private StudyFromDB(String id, String author, Date publishDate, String pubmedID) {
            this.id = id;
            this.author = author;
            this.publishDate = publishDate;
            this.pubmedID = pubmedID;
        }

        private void mapTraitAssociation() {
            if (getTraitAssociationMap().containsKey(pubmedID)) {
                this.associations = getTraitAssociationMap().get(pubmedID);
            }
            else {
                this.associations = Collections.emptySet();
            }
        }

        @UniqueID
        private String getID() {
            return id;
        }

        public String getAuthorName() {
            return author;
        }

        public String getPubMedID() {
            return pubmedID;
        }

        public Date getPublishedDate() {
            return publishDate;
        }

        public Collection<TraitAssociation> getIdentifiedAssociations() {
            if (associations == null) {
                mapTraitAssociation();
            }
            return associations;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            StudyFromDB that = (StudyFromDB) o;
            return !(id != null ? !id.equals(that.id) : that.id != null);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
