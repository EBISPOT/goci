package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.SingleNucleotidePolymorphismDAO;
import uk.ac.ebi.fgpt.goci.dao.StudyDAO;
import uk.ac.ebi.fgpt.goci.dao.TraitAssociationDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.util.Collection;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class DefaultGWASOWLPublisher implements GWASOWLPublisher {
    private StudyDAO studyDAO;
    private TraitAssociationDAO traitAssociationDAO;
    private SingleNucleotidePolymorphismDAO singleNucleotidePolymorphismDAO;
    private GWASOWLConverter converter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public StudyDAO getStudyDAO() {
        return studyDAO;
    }

    public void setStudyDAO(StudyDAO studyDAO) {
        this.studyDAO = studyDAO;
    }

    public TraitAssociationDAO getTraitAssociationDAO() {
        return traitAssociationDAO;
    }

    public void setTraitAssociationDAO(TraitAssociationDAO traitAssociationDAO) {
        this.traitAssociationDAO = traitAssociationDAO;
    }

    public SingleNucleotidePolymorphismDAO getSingleNucleotidePolymorphismDAO() {
        return singleNucleotidePolymorphismDAO;
    }

    public void setSingleNucleotidePolymorphismDAO(SingleNucleotidePolymorphismDAO singleNucleotidePolymorphismDAO) {
        this.singleNucleotidePolymorphismDAO = singleNucleotidePolymorphismDAO;
    }

    public GWASOWLConverter getConverter() {
        return converter;
    }

    public void setConverter(GWASOWLConverter converter) {
        this.converter = converter;
    }

    public OWLOntology publishGWASData() throws OWLConversionException {
        try {
            // create a new ontology to represent our data dump
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology conversion = manager.createOntology();

            // grab all studies from the DAO
            getLog().debug("Fetching studies that require conversion to OWL using StudyDAO...");
            Collection<Study> studies = getStudyDAO().retrieveAllStudies();
            getLog().debug("Query complete, got " + studies.size() + " studies");
            validateAndReportOnStudies(studies);

            // grab all other data from the DAO
            getLog().debug("Fetching traits that require conversion to OWL using TraitAssociationDAO...");
            Collection<TraitAssociation> traitAssociations = getTraitAssociationDAO().retrieveAllTraitAssociations();
            getLog().debug("Fetching SNPs that require conversion to OWL using SingleNucleotidePolymorphismDAO...");
            Collection<SingleNucleotidePolymorphism> snps = getSingleNucleotidePolymorphismDAO().retrieveAllSNPs();
            getLog().debug("All data fetched");

            // convert this data, starting with SNPs (no dependencies) and working up to studies
            getConverter().addSNPsToOntology(snps, conversion);
            getConverter().addAssociationsToOntology(traitAssociations, conversion);
            getConverter().addStudiesToOntology(studies, conversion);

            return conversion;
        }
        catch (OWLOntologyCreationException e) {
            throw new OWLConversionException("Failed to create new ontology", e);
        }
    }

    public void validateAndReportOnStudies(Collection<Study> studies) {
        // now check a random assortment of 5 studies for trait associations, abandoning broken ones
        int count = 0;
        int noAssocCount = 0;
        int termMismatches = 0;
        for (Study study : studies) {
            try {
                Collection<TraitAssociation> associations = study.getIdentifiedAssociations();
                getLog().debug("Study (PubMed ID '" + study.getPubMedID() + "') had " + associations.size() +
                                       " associations");
                if (associations.size() > 0) {
                    for (TraitAssociation association : associations) {
                        getLog().debug(
                                "    Association: SNP '" + association.getAssociatedSNP().getRSID() +
                                        "' <-> Trait '" +
                                        association.getAssociatedTrait().getIRI() + "'");
                    }
                    count++;
                }
                else {
                    noAssocCount++;
                }
            }
            catch (ObjectMappingException e) {
                if (e instanceof OntologyTermException) {
                    termMismatches++;
                    getLog().error("EFO term mapping failed: " + e.getMessage());
                }
                else {
                    getLog().warn("Excluding Study (PubMed ID '" + study.getPubMedID() + "'), " + e.getMessage());
                }
            }
        }
        int eligCount = studies.size() - noAssocCount;
        int correctCount = count + termMismatches;
        getLog().info("\n\nREPORT:\n" +
                              eligCount + "/" + studies.size() +
                              " declared associations and therefore could usefully be mapped.\n" +
                              (eligCount - count - termMismatches) + "/" + eligCount +
                              " failed due to data integrity concerns.\n" +
                              count + "/" + correctCount +
                              " studies could be completely mapped after passing all checks.\n" +
                              termMismatches + "/" + correctCount +
                              " failed due to missing or duplicated terms in EFO");
    }
}
