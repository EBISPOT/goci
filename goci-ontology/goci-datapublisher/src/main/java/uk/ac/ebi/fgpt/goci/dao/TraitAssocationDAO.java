package uk.ac.ebi.fgpt.goci.dao;

import org.semanticweb.owlapi.model.OWLClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.fgpt.goci.exception.AmbiguousOntologyTermException;
import uk.ac.ebi.fgpt.goci.exception.MissingOntologyTermException;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.lang.Initializable;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A data access object capable of retrieving {@link uk.ac.ebi.fgpt.goci.model.TraitAssociation} objects from the GWAS
 * database
 *
 * @author Tony Burdett
 * @date 24/01/12
 */
public class TraitAssocationDAO extends Initializable {
    private static final String TRAIT_SELECT =
            "select g.ID, st.ID as STUDY, s.SNP, t.DISEASETRAIT, g.PVALUEFLOAT from GWASSNP s " +
                    "join GWASSNPXREF sx on s.ID=sx.SNPID " +
                    "join GWASSTUDIESSNP g on sx.GWASSTUDIESSNPID=g.ID " +
                    "join GWASSTUDIES st on g.GWASID=st.ID " +
                    "join GWASDISEASETRAITS t on st.DISEASEID=t.ID " +
                    "where g.ID is not null and s.SNP is not null " +
                    "and t.DISEASETRAIT is not null and g.PVALUEFLOAT is not null " +
                    "order by g.ID";

    private SingleNucleotidePolymorphismDAO snpDAO;
    private OntologyDAO ontologyDAO;
    private JdbcTemplate jdbcTemplate;

    private Map<String, Set<SingleNucleotidePolymorphism>> snpMap;

    public TraitAssocationDAO() {
        this.snpMap = new HashMap<String, Set<SingleNucleotidePolymorphism>>();
    }

    public SingleNucleotidePolymorphismDAO getSNPDAO() {
        return snpDAO;
    }

    public void setSNPDAO(SingleNucleotidePolymorphismDAO snpDAO) {
        this.snpDAO = snpDAO;
    }

    public OntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    public void setOntologyDAO(OntologyDAO ontologyDAO) {
        this.ontologyDAO = ontologyDAO;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void doInitialization() {
        // select all SNPs and map them
        getLog().debug("Fetching SNPs from the database ready to map to Trait Associations...");
        Collection<SingleNucleotidePolymorphism> snps = getSNPDAO().retrieveAllSNPs();
        for (SingleNucleotidePolymorphism snp : snps) {
            if (!getSnpMap().containsKey(snp.getRSID())) {
                getSnpMap().put(snp.getRSID(), new HashSet<SingleNucleotidePolymorphism>());
            }
            getSnpMap().get(snp.getRSID()).add(snp);
        }
        // we've populated all snps, so mark that we are ready
        getLog().debug("Retrieved " + getSnpMap().keySet().size() + " SNP ids ready to map to Trait Associations");
    }

    public Collection<TraitAssociation> retrieveAllTraitAssociations() {
        try {
            waitUntilReady();
            return getJdbcTemplate().query(TRAIT_SELECT, new TraitAssociationMapper());
        }
        catch (InterruptedException e) {
            throw new ObjectMappingException(
                    "Unexpectedly interrupted whilst waiting for initialization to complete", e);
        }
    }

    private Map<String, Set<SingleNucleotidePolymorphism>> getSnpMap() {
        return snpMap;
    }

    private class TraitAssociationMapper implements RowMapper<TraitAssociation> {
        public TraitAssociation mapRow(ResultSet resultSet, int i) throws SQLException {
            String associationID = resultSet.getString(1);
            String studyID = resultSet.getString(2);
            String rsID = resultSet.getString(3);
            String traitName = resultSet.getString(4);
            float pValue = resultSet.getFloat(5);
            return new TraitAssocationFromDB(associationID, studyID, rsID, traitName, pValue);
        }
    }

    private class TraitAssocationFromDB implements TraitAssociation {
        private String id;
        private String studyID;
        private String rsID;
        private String traitName;
        private float pValue;

        private SingleNucleotidePolymorphism snp;
        private OWLClass trait;

        private TraitAssocationFromDB(String id, String studyID, String rsID, String traitName, float pValue) {
            this.id = id;
            this.studyID = studyID;
            this.rsID = rsID;
            this.traitName = traitName;
            this.pValue = pValue;
        }

        private void mapSNP() {
            if (getSnpMap().containsKey(rsID)) {
                Set<SingleNucleotidePolymorphism> snps = getSnpMap().get(rsID);
                if (snps.size() > 1) {
                    throw new ObjectMappingException(
                            "Inconsistent SNP data: there are several different SNPs with rsID '" + rsID + "' present");
                }

                if (snps.size() == 0) {
                    throw new ObjectMappingException(
                            "SNP '" + rsID + "' was not found in the database so could not be mapped");
                }

                // if we got to here, trait mapped ok
                this.snp = snps.iterator().next();
            }
            else {
                throw new ObjectMappingException(
                        "Inconsistent data: a trait association was found for SNP rsID '" + rsID + "', " +
                                "but this SNP was not found");
            }
        }

        private void mapTrait() throws ObjectMappingException {
            Collection<OWLClass> traitClasses = getOntologyDAO().getOWLClassesByLabel(traitName);
            if (traitClasses.size() > 1) {
                throw new AmbiguousOntologyTermException(
                        "Trait label is ambiguous - multiple classes in EFO have the name '" + traitName + "'");
            }

            if (traitClasses.size() == 0) {
                throw new MissingOntologyTermException(
                        "Trait '" + traitName + "' was not found in EFO so could not be mapped");
            }

            // if we got to here, trait mapped ok
            this.trait = traitClasses.iterator().next();
        }

        public String getStudyID() {
            return studyID;
        }

        public SingleNucleotidePolymorphism getAssociatedSNP() {
            if (snp == null) {
                mapSNP();
            }
            return snp;
        }

        public OWLClass getAssociatedTrait() {
            if (trait == null) {
                mapTrait();
            }
            return trait;
        }

        public float getPValue() {
            return pValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TraitAssocationFromDB that = (TraitAssocationFromDB) o;

            return !(id != null ? !id.equals(that.id) : that.id != null);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
