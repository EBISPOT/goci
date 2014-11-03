package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.dao.SingleNucleotidePolymorphismDAO;
import uk.ac.ebi.fgpt.goci.dao.StudyDAO;
import uk.ac.ebi.fgpt.goci.dao.TraitAssociationDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.lang.FilterProperties;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;
import uk.ac.ebi.fgpt.goci.utils.OntologyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett Date 26/01/12
 */
public class DefaultGWASOWLPublisher implements GWASOWLPublisher {
    private OntologyConfiguration configuration;
    private int studiesLimit = -1;

    private StudyDAO studyDAO;
    private TraitAssociationDAO traitAssociationDAO;
    private SingleNucleotidePolymorphismDAO singleNucleotidePolymorphismDAO;
    private GWASOWLConverter converter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setConfiguration(OntologyConfiguration configuration) {
        this.configuration = configuration;
    }

    public int getStudiesLimit() {
        return studiesLimit;
    }

    public void setStudiesLimit(int studiesLimit) {
        this.studiesLimit = studiesLimit;
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

    public OWLOntologyManager getManager() {
        return configuration.getOWLOntologyManager();
    }

    public OWLOntology publishGWASData() throws OWLConversionException {
        // create new ontology
        OWLOntology conversion = getConverter().createConversionOntology();

        // grab all studies from the DAO
        getLog().debug("Fetching studies that require conversion to OWL using StudyDAO...");
        Collection<Study> studies = getStudyDAO().retrieveAllStudies();
        getLog().debug("Query complete, got " + studies.size() + " studies");
//            validateGWASData(studies);

        // if studies limit is not set, convert all data, else filter to first n studies and associated data
        if (getStudiesLimit() == -1 &&
                FilterProperties.getDateFilter() == null &&
                FilterProperties.getPvalueFilter() == null) {
            // grab all other data from the DAO
            getLog().debug("Fetching traits that require conversion to OWL using TraitAssociationDAO...");
            Collection<TraitAssociation> traitAssociations = getTraitAssociationDAO().retrieveAllTraitAssociations();
            getLog().debug("Fetching SNPs that require conversion to OWL using SingleNucleotidePolymorphismDAO...");
            Collection<SingleNucleotidePolymorphism> snps = getSingleNucleotidePolymorphismDAO().retrieveAllSNPs();
            getLog().debug("All data fetched");

            // convert this data, starting with SNPs (no dependencies) and working up to studies
            getLog().debug("Starting conversion to OWL...");
            getLog().debug("Converting SNPs...");
            getConverter().addSNPsToOntology(snps, conversion);
            getLog().debug("Converting Trait Associations...");
            getConverter().addAssociationsToOntology(traitAssociations, conversion);
            getLog().debug("Converting Studies...");
            getConverter().addStudiesToOntology(studies, conversion);
            getLog().debug("All conversion done!");

            return conversion;
        }
        else {
            return filterAndPublishGWASData(conversion, studies);
        }
    }

    private OWLOntology filterAndPublishGWASData(OWLOntology conversion, Collection<Study> studies)
            throws OWLConversionException {
        Collection<Study> filteredStudies = new ArrayList<Study>();
        Collection<TraitAssociation> filteredTraitAssociations = new ArrayList<TraitAssociation>();
        Collection<SingleNucleotidePolymorphism> filteredSNPs = new ArrayList<SingleNucleotidePolymorphism>();

        int count = 0;
        int studyLimit = getStudiesLimit() == -1 ? Integer.MAX_VALUE : getStudiesLimit();
        Iterator<Study> studyIterator = studies.iterator();
        while (count < studyLimit && studyIterator.hasNext()) {
            Study nextStudy = studyIterator.next();
            filteredStudies.add(nextStudy);
            for (TraitAssociation nextTA : nextStudy.getIdentifiedAssociations()) {
                filteredTraitAssociations.add(nextTA);
                try {
                    filteredSNPs.add(nextTA.getAssociatedSNP());
                }
                catch (ObjectMappingException e) {
                    // we can safely ignore this, a warning will be issued when we add each trait association
                }
            }
            count++;
        }

        // convert this data, starting with SNPs (no dependencies) and working up to studies
        getLog().debug("Starting conversion to OWL...");
        getLog().debug("Converting " + filteredSNPs.size() + " filtered SNPs...");
        getConverter().addSNPsToOntology(filteredSNPs, conversion);
        getLog().debug("Converting " + filteredTraitAssociations.size() + " filtered Trait Associations...");
        getConverter().addAssociationsToOntology(filteredTraitAssociations, conversion);
        getLog().debug("Converting " + filteredStudies.size() + " filtered Studies...");
        getConverter().addStudiesToOntology(filteredStudies, conversion);
        getLog().debug("All conversion done!");

        return conversion;
    }

    public OWLReasoner publishGWASDataInferredView(OWLOntology ontology) throws OWLConversionException {
        try {
            getLog().debug("Loading any missing imports...");
            OntologyUtils.loadImports(ontology.getOWLOntologyManager(), ontology);
            StringBuilder loadedOntologies = new StringBuilder();
            int n = 1;
            for (OWLOntology o : ontology.getOWLOntologyManager().getOntologies()) {
                loadedOntologies.append("\t")
                        .append(n++)
                        .append(") ")
                        .append(o.getOntologyID().getOntologyIRI())
                        .append("\n");
            }
            getLog().debug("Imports collected: the following ontologies have been loaded in this session:\n" +
                                   loadedOntologies.toString());
            getLog().info("Classifying ontology from " + ontology.getOntologyID().getOntologyIRI());

            getLog().debug("Creating reasoner... ");
            OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
            OWLReasoner reasoner = factory.createReasoner(ontology, config);

            getLog().debug("Precomputing inferences...");
            reasoner.precomputeInferences();

            getLog().debug("Checking ontology consistency...");
            reasoner.isConsistent();

            getLog().debug("Checking for unsatisfiable classes...");
            if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().size() > 0) {
                throw new OWLConversionException("Once classified, unsatisfiable classes were detected");
            }
            else {
                getLog().info("Reasoning complete! ");
                return reasoner;
            }
        }
        catch (UnloadableImportException e) {
            throw new OWLConversionException("Failed to load imports", e);
        }
    }

    public void saveGWASData(OWLOntology ontology, File outputFile) throws OWLConversionException {
        try {
            getLog().info("Saving GWAS catalog data...");
//            OWLXMLOntologyFormat format = new OWLXMLOntologyFormat();
            OWLOntologyFormat format = new RDFXMLOntologyFormat();
            getManager().saveOntology(ontology,
                                      format,
                                      IRI.create(outputFile));
            getLog().info("GWAS catalog data saved ok");
            getLog().info("Resulting ontology contains " + ontology.getAxiomCount() + " axioms " +
                                  "and is saved at " + outputFile.getAbsolutePath());
        }
        catch (OWLOntologyStorageException e) {
            throw new OWLConversionException("Failed to save GWAS data", e);
        }
    }

    public void saveGWASDataInferredView(OWLReasoner reasoner, File outputFile) throws OWLConversionException {
        try {
            // create new ontology to hold inferred axioms
            OWLOntology inferredOntology = getConverter().createConversionOntology();

            getLog().info("Saving inferred view...");
            List<InferredAxiomGenerator<? extends OWLAxiom>> gens =
                    new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
            // we require all inferred stuff except for disjoints...
            gens.add(new InferredClassAssertionAxiomGenerator());
            gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
            gens.add(new InferredEquivalentClassAxiomGenerator());
            gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
            gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
            gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
            gens.add(new InferredPropertyAssertionGenerator());
            gens.add(new InferredSubClassAxiomGenerator());
            gens.add(new InferredSubDataPropertyAxiomGenerator());
            gens.add(new InferredSubObjectPropertyAxiomGenerator());

            // now create the target ontology and save
            OWLOntologyManager inferredManager = inferredOntology.getOWLOntologyManager();
            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
            iog.fillOntology(inferredManager, inferredOntology);
            inferredManager.saveOntology(inferredOntology, IRI.create(outputFile));
            getLog().info("Inferred view saved ok");
        }
        catch (OWLOntologyStorageException e) {
            throw new OWLConversionException("Failed to save GWAS data (inferred view)", e);
        }
    }

    /**
     * Validates the data obtained from the GWAS catalog (prior to converting to OWL)
     *
     * @param studies the set of studies to validate
     */
    protected void validateGWASData(Collection<Study> studies) {
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
                                        association.getAssociatedTrait().toString() + "'");
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
