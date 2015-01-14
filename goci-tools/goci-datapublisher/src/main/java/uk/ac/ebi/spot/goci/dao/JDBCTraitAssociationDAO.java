package uk.ac.ebi.spot.goci.dao;

import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.ebi.spot.goci.exception.AmbiguousOntologyTermException;
import uk.ac.ebi.spot.goci.exception.MissingOntologyTermException;
import uk.ac.ebi.spot.goci.exception.ObjectMappingException;
import uk.ac.ebi.spot.goci.lang.FilterProperties;
import uk.ac.ebi.spot.goci.lang.Initializable;
import uk.ac.ebi.spot.goci.lang.UniqueID;
import uk.ac.ebi.spot.goci.ui.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.ui.model.TraitAssociation;

import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A data access object capable of retrieving {@link uk.ac.ebi.spot.goci.ui.model.TraitAssociation} objects from the GWAS
 * database
 *
 * @author Tony Burdett Date 24/01/12
 */
public class JDBCTraitAssociationDAO extends Initializable implements TraitAssociationDAO {
//    private static final String TRAIT_SELECT_MAIN =
//            "SELECT ROWNUM, ID, STUDYID, STUDY, SNP, DISEASETRAIT, PVALUEFLOAT, EFOURI FROM " +
//                    "(select distinct g.ID, st.ID as STUDYID, st.PMID as STUDY, s.SNP, t.DISEASETRAIT, g.PVALUEFLOAT, e.EFOURI from GWASSNP s " +
//                    "join GWASSNPXREF sx on s.ID=sx.SNPID " +
//                    "join GWASSTUDIESSNP g on sx.GWASSTUDIESSNPID=g.ID " +
//                    "join GWASSTUDIES st on g.GWASID=st.ID " +
//                    "join GWASDISEASETRAITS t on st.DISEASEID=t.ID " +
//                    "join GWASEFOXREF ex on ex.STUDYID = st.ID " +
//                    "join GWASEFOTRAITS e on e.ID = ex.TRAITID " +
//                    "where g.ID is not null and s.SNP is not null " +
//                    "and t.DISEASETRAIT is not null and g.PVALUEFLOAT is not null ";

    private static final String TRAIT_SELECT_MAIN =
            "SELECT ROWNUM, ID, STUDYID, STUDY, SNP, DISEASETRAIT, PVALUEFLOAT, EFOURI FROM " +
                    "(select distinct g.ID, st.ID as STUDYID, st.PMID as STUDY, s.SNP, t.DISEASETRAIT, g.PVALUEFLOAT, e.EFOURI from GWASSNP s " +
                    "join GWASSNPXREF sx on s.ID=sx.SNPID " +
                    "join GWASSTUDIESSNP g on sx.GWASSTUDIESSNPID=g.ID " +
                    "join GWASSTUDIES st on g.GWASID=st.ID " +
                    "join GWASDISEASETRAITS t on st.DISEASEID=t.ID " +
                    "join GWASEFOSNPXREF ex on ex.GWASSTUDIESSNPID = g.ID " +
                    "join GWASEFOTRAITS e on e.ID = ex.TRAITID " +
                    "where g.ID is not null and s.SNP is not null " +
                    "and t.DISEASETRAIT is not null and g.PVALUEFLOAT is not null and st.publish = 1 ";

    private static final String TRAIT_SELECT_ORDER = " order by g.ID";

    private SingleNucleotidePolymorphismDAO snpDAO;
    private DefaultOntologyDAO ontologyDAO;
    private JdbcTemplate jdbcTemplate;

    private Map<String, Set<SingleNucleotidePolymorphism>> snpMap;

    private Logger snpLogger = LoggerFactory.getLogger("unmapped.snp.log");
    private Logger traitLogger = LoggerFactory.getLogger("unmapped.trait.log");

    public JDBCTraitAssociationDAO() {
        this.snpMap = new HashMap<String, Set<SingleNucleotidePolymorphism>>();
    }

    public SingleNucleotidePolymorphismDAO getSNPDAO() {
        return snpDAO;
    }

    public void setSNPDAO(SingleNucleotidePolymorphismDAO snpDAO) {
        this.snpDAO = snpDAO;
    }

    public DefaultOntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    public void setOntologyDAO(DefaultOntologyDAO ontologyDAO) {
        this.ontologyDAO = ontologyDAO;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected void doInitialization() {
        // select all SNPs and map them
        getLog().info("Fetching SNPs from the database ready to map to Trait Associations...");
        Collection<SingleNucleotidePolymorphism> snps = getSNPDAO().retrieveAllSNPs();
        for (SingleNucleotidePolymorphism snp : snps) {
            if (!getSnpMap().containsKey(snp.getRSID())) {
                getSnpMap().put(snp.getRSID(), new HashSet<SingleNucleotidePolymorphism>());
            }
            getSnpMap().get(snp.getRSID()).add(snp);
        }
        // we've populated all snps, so mark that we are ready
        getLog().info("Retrieved " + getSnpMap().keySet().size() + " SNP ids ready to map to Trait Associations");
    }

    public Collection<TraitAssociation> retrieveAllTraitAssociations() {
        try {
            waitUntilReady();
            if (FilterProperties.getPvalueFilter() == null) {
                String full_query = TRAIT_SELECT_MAIN.concat(TRAIT_SELECT_ORDER).concat(")");
                return getJdbcTemplate().query(full_query, new TraitAssociationMapper());
            }
            else {
                String pval_filter = "and g.PVALUEFLOAT <= " + FilterProperties.getPvalueFilter();

                String filtered_query;
                if (FilterProperties.getDateFilter() == null) {
                    filtered_query = TRAIT_SELECT_MAIN.concat(pval_filter).concat(TRAIT_SELECT_ORDER).concat(")");
                }
                else {
                    String date_filter =
                            " and st.STUDYDATE < to_date('" + FilterProperties.getDateFilter() + "','yyyy-mm-dd')";
                    filtered_query = TRAIT_SELECT_MAIN.concat(pval_filter)
                            .concat(date_filter)
                            .concat(TRAIT_SELECT_ORDER)
                            .concat(")");
                }
                return getJdbcTemplate().query(filtered_query, new TraitAssociationMapper());
            }
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
            String rownum = resultSet.getString(1).trim();
            String associationID = resultSet.getString(2).trim();
            String pubMedID = resultSet.getString(4).trim();
            String rsID = resultSet.getString(5).trim();
            String traitName = resultSet.getString(6).trim();
            float pValue = resultSet.getFloat(7);
            String efoURI = resultSet.getString(8);
            return new TraitAssocationFromDB(rownum, associationID, pubMedID, rsID, traitName, pValue, efoURI);
        }

    }

    private class TraitAssocationFromDB implements TraitAssociation {
        private String id;
        private String pubMedID;
        private String rsID;
        private String traitName;
        private float pValue;
        private String efoURI;

        private SingleNucleotidePolymorphism snp;
        private URI trait;

        private TraitAssocationFromDB(String rowNum,
                                      String id,
                                      String pubMedID,
                                      String rsID,
                                      String traitName,
                                      float pValue,
                                      String efoURI) {
            this.id = rowNum.concat("_").concat(id);
            this.pubMedID = pubMedID;
            this.rsID = rsID;
            this.traitName = traitName;
            this.pValue = pValue;
            this.efoURI = efoURI;

            getLog().debug(id + "\t" + traitName + "\t" + efoURI);
        }

        private void mapSNP() {
            if (getSnpMap().containsKey(rsID)) {
                Set<SingleNucleotidePolymorphism> snps = getSnpMap().get(rsID);
                if (snps.size() > 1) {
                    snpLogger.warn(rsID + "\t[multiple copies]");
                    throw new ObjectMappingException(
                            "Multiple distinct SNPs were defined with rsID '" + rsID + "'; " +
                                    "there is inconsistent data in the database");
                }

                if (snps.size() == 0) {
                    snpLogger.warn(rsID + "\t[no SNP]");
                    throw new ObjectMappingException(
                            "SNP '" + rsID + "' was not found in the database so could not be mapped");
                }

                // if we got to here, trait mapped ok
                this.snp = snps.iterator().next();
            }
            else {
                snpLogger.warn(rsID + "\t[missing location data]");
                throw new ObjectMappingException(
                        "Inconsistent data: a trait association was found for SNP rsID '" + rsID + "', " +
                                "but this SNP was not fully specified (missing chromosome/region data)");
            }
        }

        private void mapTrait() throws ObjectMappingException {
            // if there is no mapped efoURI found the catalogue database
            if (efoURI == null) {
                Collection<OWLClass> traitClasses = getOntologyDAO().getOWLClassesByLabel(traitName);

                if (traitClasses.size() == 0) {
                    traitLogger.warn(traitName + "\t[not in EFO]");
                    throw new MissingOntologyTermException(
                            "Trait '" + traitName + "' was not found in EFO so could not be accurately mapped");
                }
                else if (traitClasses.size() > 1) {
                    // ambiguous term - multiple classes have the same synonym
                    traitLogger.warn(traitName + "\t[ambiguous names/synonyms in EFO]");

                    // but we can still try a few hacks...

                    // workaround for cancer/neoplasm duplications...
                    if (traitName.toLowerCase().contains("cancer")) {
                        for (OWLClass cls : traitClasses) {
                            List<String> clsNames = getOntologyDAO().getClassNames(cls);
                            boolean isCarcinoma = false;
                            for (String clsName : clsNames) {
                                if (clsName.toLowerCase().contains("carcinoma")) {
                                    isCarcinoma = true;
                                    break;
                                }
                            }
                            if (isCarcinoma) {
                                this.trait = cls.getIRI().toURI();
                                break;
                            }
                        }
                    }

                    // workaround for obesity/morbid obesity
                    if (traitName.toLowerCase().contains("obesity")) {
                        for (OWLClass cls : traitClasses) {
                            if (cls.getIRI().toURI().toString().equals("http://www.ebi.ac.uk/efo/EFO_0001073")) {
                                this.trait = cls.getIRI().toURI();
                                break;
                            }
                        }
                    }

                    // workaround for Coronary heart disease
                    if (traitName.toLowerCase().contains("coronary heart disease")) {
                        for (OWLClass cls : traitClasses) {
                            if (cls.getIRI().toURI().toString().equals("http://www.ebi.ac.uk/efo/EFO_0001645")) {
                                this.trait = cls.getIRI().toURI();
                                break;
                            }
                        }
                    }

                    // if none of these hacks worked, throw an exception
                    if (trait == null) {
                        throw new AmbiguousOntologyTermException(
                                "Trait label is ambiguous - multiple classes in EFO have the name '" + traitName + "'");
                    }
                }
                else {
                    // exactly one label <-> EFO mapping
                    this.trait = traitClasses.iterator().next().getIRI().toURI();
                }
            }
            // if there is a specified URI, get it
            else {
                this.trait = getOntologyDAO().getOWLClassByURI(efoURI).getIRI().toURI();
                getLog().debug("Trait " + traitName + " has a mapping in the catalogue database");
            }
        }

        @UniqueID
        private String getID() {
            return id;
        }

        public String getPubMedID() {
            return pubMedID;
        }

        public String getAssociatedSNPReferenceId() {
            return rsID;
        }

        public SingleNucleotidePolymorphism getAssociatedSNP() {
            if (snp == null) {
                mapSNP();
            }
            return snp;
        }

        public URI getAssociatedTrait() {
            if (trait == null) {
                mapTrait();
            }
            return trait;
        }

        public float getPValue() {
            return pValue;
        }

        public String getGWASCuratorLabel() {
            return traitName;
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
