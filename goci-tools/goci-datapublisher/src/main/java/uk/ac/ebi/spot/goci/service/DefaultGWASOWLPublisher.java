package uk.ac.ebi.spot.goci.service;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;
import uk.ac.ebi.spot.goci.utils.FilterProperties;
import uk.ac.ebi.spot.goci.owl.OntologyLoader;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.Association;
//import uk.ac.ebi.spot.goci.utils.OntologyUtils;

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
@Service
public class DefaultGWASOWLPublisher implements GWASOWLPublisher {
    private OntologyLoader ontologyLoader;
    private int studiesLimit = -1;

    private StudyRepository studyRepository;
    private StudyService studyService;

    private AssociationRepository associationRepository;
    private AssociationService associationService;

    private SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    private SingleNucleotidePolymorphismService singleNucleotidePolymorphismService;

    private GWASOWLConverter converter;




    @Autowired
    public DefaultGWASOWLPublisher(StudyService studyService,
                                   AssociationService associationService,
                                   SingleNucleotidePolymorphismService singleNucleotidePolymorphismService,
                                   GWASOWLConverter converter,
                                   OntologyLoader ontologyLoader){
        this.studyService = studyService;
        this.associationService = associationService;
        this.singleNucleotidePolymorphismService = singleNucleotidePolymorphismService;
        this.converter = converter;
        this.ontologyLoader = ontologyLoader;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public void setOntologyLoader(OntologyLoader ontologyLoader) {
        this.ontologyLoader = ontologyLoader;
    }

    public int getStudiesLimit() {
        return studiesLimit;
    }

    public void setStudiesLimit(int studiesLimit) {
        this.studiesLimit = studiesLimit;
    }

    public StudyRepository getStudyRepository() {
        return studyRepository;
    }

    public StudyService getStudyService(){
        return studyService;
    }


    public AssociationRepository getAssociationRepository() {
        return associationRepository;
    }
    public AssociationService getAssociationService() {
        return associationService;
    }



    public SingleNucleotidePolymorphismRepository getSingleNucleotidePolymorphismRepository() {
        return singleNucleotidePolymorphismRepository;
    }
    public SingleNucleotidePolymorphismService getSingleNucleotidePolymorphismService() {
        return singleNucleotidePolymorphismService;
    }



    public GWASOWLConverter getConverter() {
        return converter;
    }



    public OWLOntologyManager getManager() {
        return ontologyLoader.getOntology().getOWLOntologyManager();
    }

    public OWLOntology publishGWASData() throws OWLConversionException {
        // create new ontology
        OWLOntology conversion = getConverter().createConversionOntology();

        // grab all studies from the DAO
        getLog().debug("Fetching studies that require conversion to OWL using StudyRepository...");

        Collection<Study> studies = getStudyService().findReallyAll();

        //TODO : check with Tony probably better to do it at the Repository/Service level
        //Discard studies which are not associated with a disease trait and those which haven't been published yet
        //by the GWAS catalog.
        Iterator<Study> iterator = studies.iterator();
        while(iterator.hasNext()){
            Study study = iterator.next();
            //Remove study which have no diseaseTrait.
            if(study.getDiseaseTrait() == null) {
                iterator.remove();
            }else if( study.getHousekeeping().getCatalogPublishDate() == null) {
                iterator.remove();
            }
            else if(study.getHousekeeping().getCatalogUnpublishDate() != null){
                iterator.remove();
            }
//            }else {
//
//                //Remove study which have no associations where pvalue is not null.
//                Collection<Association> associations = study.getAssociations();
//                Iterator<Association> associationIterator = associations.iterator();
//                int associationCount = 0;
//                while (associationIterator.hasNext()) {
//                    Association association = associationIterator.next();
//
//                    if (association.getPvalueExponent() != null && association.getPvalueMantissa() != null) {
//                        associationCount++;
//                    }
//                }
//                if (associationCount == 0) {
//                    iterator.remove();
//                }
//            }
        }

        getLog().debug("Query complete, got " + studies.size() + " studies");

        // if studies limit is not set, convert all data, else filter to first n studies and associated data
        if (getStudiesLimit() == -1 &&
                FilterProperties.getDateFilter() == null &&
                FilterProperties.getPvalueFilter() == null) {
            // grab all other data from the DAO
            getLog().debug("Fetching traits that require conversion to OWL using AssociationRepository...");
            Collection<Association> traitAssociations = getAssociationService().findReallyAll();

            //TODO check with Tony how to do that in a better way from service or repository (how to not get associations linked to study with no trait.
            //Discard all the associations which are linked to study which are not linked to a disease trait or haven't
            //been published yet in the GWAS catalog.
            Iterator<Association> associationIterator = traitAssociations.iterator();
            while(associationIterator.hasNext()){
                Association association = associationIterator.next();
                if(association.getStudy().getDiseaseTrait() == null) {
                    associationIterator.remove();
                }else if(association.getStudy().getHousekeeping().getCatalogPublishDate() == null){
                    associationIterator.remove();

                }
                else if(association.getStudy().getHousekeeping().getCatalogUnpublishDate() != null){
                    iterator.remove();
                }
            }
            getLog().debug("Fetching SNPs that require conversion to OWL using SingleNucleotidePolymorphismRepository...");
            Collection<SingleNucleotidePolymorphism> snps = getSingleNucleotidePolymorphismService().findAll();
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

        //TODO : check with tony : Discard studies which are not yet associated with a trait.
        //Discard studies which are not associated with a disease trait and those which haven't been published yet
        //by the GWAS catalog.
        Iterator<Study> iterator = studies.iterator();
        while(iterator.hasNext()){
            Study study = iterator.next();
            if(study.getDiseaseTrait() == null) {
                iterator.remove();
            }else if( study.getHousekeeping().getCatalogPublishDate() == null) {
                iterator.remove();
            }
            else if(study.getHousekeeping().getCatalogUnpublishDate() != null){
                iterator.remove();
            }
        }


        Collection<Study> filteredStudies = new ArrayList<Study>();
        Collection<Association> filteredTraitAssociations = new ArrayList<Association>();
        Collection<SingleNucleotidePolymorphism> filteredSNPs = new ArrayList<SingleNucleotidePolymorphism>();

        int count = 0;
        int studyLimit = getStudiesLimit() == -1 ? Integer.MAX_VALUE : getStudiesLimit();
        Iterator<Study> studyIterator = studies.iterator();
        while (count < studyLimit && studyIterator.hasNext()) {
            Study nextStudy = studyIterator.next();
            for (Association nextTA : nextStudy.getAssociations()) {
                filteredTraitAssociations.add(nextTA);
                for (Locus locus : nextTA.getLoci()) {
                    for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                        filteredSNPs.add(riskAllele.getSnp());
                    }
                }
            }
            filteredStudies.add(nextStudy);
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
            getLog().debug("Loading any missing imports...");
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

    public void saveGWASData(OWLOntology ontology, File outputFile) throws OWLConversionException {
        try {
            getLog().info("Saving GWAS catalog data...");
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
//            try {
                Collection<Association> associations = study.getAssociations();
                getLog().debug("Study (PubMed ID '" + study.getPubmedId() + "') had " + associations.size() +
                                       " associations");
                if (associations.size() > 0) {
                    for (Association association : associations) {
                        String efoTraitsDashSepList="";
                        for(EfoTrait efoTrait : association.getEfoTraits()){
                            if("".equals(efoTraitsDashSepList)){
                                efoTraitsDashSepList.concat(efoTrait.getTrait());

                            }else {
                                efoTraitsDashSepList.concat(", " + efoTrait.getTrait());

                            }
                        }
                        for(Locus locus : association.getLoci()){
                            for(RiskAllele riskAllele : locus.getStrongestRiskAlleles()){
                                getLog().debug(
                                        //                                "    Association: SNP '" + association.getAssociatedSNP().getRSID() +
                                        "    Association: SNP '" + riskAllele.getSnp().getRsId() +
                                                "' <-> Trait '" +
                                                efoTraitsDashSepList.toString() + "'");
                            }
                        }
                    }
                    count++;
                }
                else {
                    noAssocCount++;
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
