package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import uk.ac.ebi.fgpt.goci.dao.SingleNucleotidePolymorphismDAO;
import uk.ac.ebi.fgpt.goci.dao.StudyDAO;
import uk.ac.ebi.fgpt.goci.dao.TraitAssociationDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class DefaultGWASOWLPublisher implements GWASOWLPublisher {
    private Resource efoResource;
    private Resource gwasDiagramSchemaResource;

    private StudyDAO studyDAO;
    private TraitAssociationDAO traitAssociationDAO;
    private SingleNucleotidePolymorphismDAO singleNucleotidePolymorphismDAO;
    private GWASOWLConverter converter;

    private OWLOntologyManager manager;
    private OWLDataFactory factory;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLDataFactory getDataFactory() {
        return factory;
    }

    public Resource getEfoResource() {
        return efoResource;
    }

    /**
     * Sets the location from which to load EFO, if required.  Setting this property creates a mapper that prompts the
     * OWL API to load EFO from the given location, instead of attempting to resolve to the URL corresponding to the
     * ontology IRI.  This property is optional.
     *
     * @param efoResource the resource at which EFO can be found, using spring configuration syntax (URLs,
     *                    classpath:...)
     */
    public void setEfoResource(Resource efoResource) {
        this.efoResource = efoResource;
    }

    public Resource getGwasDiagramSchemaResource() {
        return gwasDiagramSchemaResource;
    }

    /**
     * Sets the location from which to load the gwas diagram schema, if required.  Setting this property creates a
     * mapper that prompts the OWL API to load the gwas diagram schema from the given location, instead of attempting to
     * resolve to the URL corresponding to the ontology IRI.  This property is optional.
     *
     * @param gwasDiagramSchemaResource the resource at which the gwas diagram schema can be found, using spring
     *                                  configuration syntax (URLs, classpath:...)
     */
    public void setGwasDiagramSchemaResource(Resource gwasDiagramSchemaResource) {
        this.gwasDiagramSchemaResource = gwasDiagramSchemaResource;
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

    public void init() throws IOException {
        this.manager = OWLManager.createOWLOntologyManager();
        if (getEfoResource() != null) {
            getLog().info("Mapping EFO to " + getEfoResource().getURI());
            this.manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI),
                                                          IRI.create(getEfoResource().getURI())));
        }
        if (getGwasDiagramSchemaResource() != null) {
            getLog().info("Mapping GWAS schema to " + getGwasDiagramSchemaResource().getURI());
            this.manager.addIRIMapper(new SimpleIRIMapper(IRI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI),
                                                          IRI.create(getEfoResource().getURI())));
        }
        this.factory = manager.getOWLDataFactory();
    }

    public OWLOntology publishGWASData() throws OWLConversionException {
        // create new ontology
        OWLOntology conversion = getConverter().createConversionOntology();

        // grab all studies from the DAO
        getLog().debug("Fetching studies that require conversion to OWL using StudyDAO...");
        Collection<Study> studies = getStudyDAO().retrieveAllStudies();
        getLog().debug("Query complete, got " + studies.size() + " studies");
//            validateGWASData(studies);

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

    public OWLReasoner publishGWASDataInferredView(IRI ontologyIRI) throws OWLConversionException {
        try {
            // reload the supplied ontology
            getLog().info("Classifying ontology from " + ontologyIRI);
            getLog().debug("Reloading " + ontologyIRI);
            OWLOntology ontology = getManager().loadOntology(ontologyIRI);
            getLog().info("Loaded " + ontology.getOntologyID().getOntologyIRI() + " ok");
            OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
            getLog().debug("Creating reasoner...");
            OWLReasoner reasoner = factory.createReasoner(ontology, config);
            getLog().debug("Precomputing inferences...");
            reasoner.precomputeInferences();
            getLog().debug("Checking ontology consistency...");
            if (reasoner.isConsistent()) {
                getLog().debug("Checking for unsatisfiable classes...");
                if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().size() > 0) {
                    throw new OWLConversionException("Once classified, unsatisfiable classes were detected");
                }
                else {
                    getLog().info("Reasoning complete!");
                    return reasoner;
                }
            }
            else {
                throw new OWLConversionException("Ontology is not consistent!");
            }
        }
        catch (OWLOntologyCreationException e) {
            throw new OWLConversionException("Failed to load imported ontology", e);
        }
    }

//    public OWLReasoner publishGWASDataInferredView(OWLOntology ontology) throws OWLConversionException {
//        try {
//            // load any missing imports
//            for (OWLImportsDeclaration importsDeclaration : ontology.getImportsDeclarations()) {
//                getLog().debug("Doing import of " + importsDeclaration.getIRI());
//                getManager().makeLoadImportRequest(importsDeclaration);
//                getLog().debug("Imported " + importsDeclaration.getIRI() + " ok");
//            }
//            getLog().info("Classifying ontology from " + ontology.getOntologyID().getOntologyIRI());
//            OWLReasonerFactory factory = new Reasoner.ReasonerFactory();
//            ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
//            OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
//            getLog().debug("Creating reasoner...");
//            OWLReasoner reasoner = factory.createReasoner(ontology, config);
//            getLog().debug("Precomputing inferences...");
//            reasoner.precomputeInferences();
//            getLog().debug("Checking ontology consistency...");
//            reasoner.isConsistent();
////            getLog().debug("Checking for unsatisfiable classes...");
////            if (reasoner.getUnsatisfiableClasses().getEntitiesMinusBottom().size() > 0) {
////                throw new OWLConversionException("Once classified, unsatisfiable classes were detected");
////            }
////            else {
//                getLog().info("Reasoning complete!");
//                return reasoner;
////            }
//        }
//        catch (OWLOntologyCreationException e) {
//            throw new OWLConversionException("Failed to load imported ontology", e);
//        }
//    }

    public void saveGWASData(OWLOntology ontology, File outputFile) throws OWLConversionException {
        try {
            getLog().info("Saving GWAS catalog data...");
            OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
            getManager().saveOntology(ontology,
                                      owlxmlFormat,
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
            getLog().info("Saving inferred view...");
            List<InferredAxiomGenerator<? extends OWLAxiom>> gens =
                    new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
            gens.add(new InferredSubClassAxiomGenerator());
            OWLOntology inferredOntology = getManager().createOntology();
            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
            iog.fillOntology(getManager(), inferredOntology);
            getManager().saveOntology(inferredOntology, IRI.create(outputFile));
            getLog().info("Inferred view saved ok");
        }
        catch (OWLOntologyStorageException e) {
            throw new OWLConversionException("Failed to save GWAS data (inferred view)", e);
        }
        catch (OWLOntologyCreationException e) {
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
