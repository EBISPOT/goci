package uk.ac.ebi.fgpt.goci.service;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.exception.ObjectMappingException;
import uk.ac.ebi.fgpt.goci.exception.OntologyTermException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.fgpt.goci.model.Study;
import uk.ac.ebi.fgpt.goci.model.TraitAssociation;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of {@link GWASOWLConverter} that fetches data from the GWAS catalog using a {@link
 * uk.ac.ebi.fgpt.goci.dao.StudyDAO} and converts all obtained {@link Study} objects to OWL.
 *
 * @author Tony Burdett
 * @date 26/01/12
 */
public class DefaultGWASOWLConverter implements GWASOWLConverter {
    private OWLOntologyManager manager;
    private OWLDataFactory factory;

    private ReflexiveIRIMinter minter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public DefaultGWASOWLConverter() {
        this.manager = OWLManager.createOWLOntologyManager();
        this.factory = manager.getOWLDataFactory();
        this.minter = new ReflexiveIRIMinter();
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLDataFactory getDataFactory() {
        return factory;
    }

    public ReflexiveIRIMinter getMinter() {
        return minter;
    }

    public OWLOntology createConversionOntology() throws OWLConversionException {
        try {
            // create a new ontology to represent our data dump
            String iri = "http://www.ebi.ac.uk/efo/gwas-diagram/" +
                    new SimpleDateFormat("yyyy/MM/dd").format(new Date()) +
                    "/data";
            OWLOntology conversion = getManager().createOntology(IRI.create(iri));

            // import the gwas ontology schema
            OWLImportsDeclaration importDecl = getDataFactory().getOWLImportsDeclaration(
                    IRI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI));
            ImportChange change = new AddImport(conversion, importDecl);
            getManager().applyChange(change);

            return conversion;
        }
        catch (OWLOntologyCreationException e) {
            throw new OWLConversionException("Failed to create new ontology", e);
        }
    }

    public void addStudiesToOntology(Collection<Study> studies, OWLOntology ontology) {
        for (Study study : studies) {
            convertStudy(study, ontology);
        }
    }

    public void addSNPsToOntology(Collection<SingleNucleotidePolymorphism> snps, OWLOntology ontology) {
        for (SingleNucleotidePolymorphism snp : snps) {
            convertSNP(snp, ontology);
        }
    }

    public void addAssociationsToOntology(Collection<TraitAssociation> associations, OWLOntology ontology) {
        // the set of warnings that were issued during mappings
        Set<String> issuedWarnings = new HashSet<String>();
        for (TraitAssociation association : associations) {
            convertAssociation(association, ontology, issuedWarnings);
        }
    }

    protected void convertStudy(Study study, OWLOntology ontology) {
        // get the study class
        OWLClass studyCls = getDataFactory().getOWLClass(IRI.create(OntologyConstants.STUDY_CLASS_IRI));

        // create a new study instance
        OWLNamedIndividual studyIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, study));

        // assert class membership
        OWLClassAssertionAxiom classAssertion = getDataFactory().getOWLClassAssertionAxiom(studyCls, studyIndiv);
        getManager().addAxiom(ontology, classAssertion);

        // add datatype properties...

        // get datatype relations
        OWLDataProperty has_author = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_AUTHOR_PROPERTY_IRI));
        OWLDataProperty has_publication_date = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_PUBLICATION_DATE_PROPERTY_IRI));
        OWLDataProperty has_pubmed_id = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_PUBMED_ID_PROPERTY_IRI));

        // assert author relation
        OWLLiteral author = getDataFactory().getOWLLiteral(study.getAuthorName());
        OWLDataPropertyAssertionAxiom author_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_author, studyIndiv, author);
        AddAxiom add_author = new AddAxiom(ontology, author_relation);
        getManager().applyChange(add_author);

        //2002-10-10T12:00:00-05:00
        //2011-05-20T00:00:00+0100

        // assert publication_date relation
        if (study.getPublishedDate() != null) {
            String rfcTimezone =
                    new SimpleDateFormat("Z").format(study.getPublishedDate());
            String xsdTimezone =
                    rfcTimezone.substring(0, 3).concat(":").concat(rfcTimezone.substring(3, rfcTimezone.length()));
            String xmlDatetimeStr =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(study.getPublishedDate()) + xsdTimezone;
//            String xmlDatetimeStr = new SimpleDateFormat("yyyy-MM-dd").format(study.getPublishedDate());
            OWLLiteral publication_date = getDataFactory().getOWLLiteral(xmlDatetimeStr, OWL2Datatype.XSD_DATE_TIME);
            OWLDataPropertyAssertionAxiom publication_date_relation =
                    getDataFactory().getOWLDataPropertyAssertionAxiom(has_publication_date,
                                                                      studyIndiv,
                                                                      publication_date);
            AddAxiom add_publication_date = new AddAxiom(ontology, publication_date_relation);
            getManager().applyChange(add_publication_date);
        }

        // assert pubmed_id relation
        OWLLiteral pubmed_id = getDataFactory().getOWLLiteral(study.getPubMedID());
        OWLDataPropertyAssertionAxiom pubmed_id_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_pubmed_id, studyIndiv, pubmed_id);
        AddAxiom add_pubmed_id = new AddAxiom(ontology, pubmed_id_relation);
        getManager().applyChange(add_pubmed_id);

        // add object properties...

        // get the has_part relation
        OWLObjectProperty has_part = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.HAS_PART_PROPERTY_IRI));

        // for this study, get all trait associations 
        Collection<TraitAssociation> associations = study.getIdentifiedAssociations();
        // and create an study has_part association assertion for each one
        for (TraitAssociation association : associations) {
            // get the trait association instance for this association
            OWLNamedIndividual taIndiv = getDataFactory().getOWLNamedIndividual(
                    getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, association));

            // assert relation
            OWLObjectPropertyAssertionAxiom relation =
                    getDataFactory().getOWLObjectPropertyAssertionAxiom(has_part, studyIndiv, taIndiv);
            AddAxiom addAxiomChange = new AddAxiom(ontology, relation);
            getManager().applyChange(addAxiomChange);
        }
    }

    protected void convertSNP(SingleNucleotidePolymorphism snp, OWLOntology ontology) {
        // get the snp class
        OWLClass snpClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));

        // create a new snp instance
        OWLNamedIndividual snpIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, snp));

        // assert class membership
        OWLClassAssertionAxiom classAssertion = getDataFactory().getOWLClassAssertionAxiom(snpClass, snpIndiv);
        getManager().addAxiom(ontology, classAssertion);

        // add datatype properties...

        // get datatype relations
        OWLDataProperty has_snp_rsid = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_SNP_REFERENCE_ID_PROPERTY_IRI));
        OWLDataProperty has_bp_pos = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_BP_POSITION_PROPERTY_IRI));

        // assert rsid relation
        OWLLiteral rsid = getDataFactory().getOWLLiteral(snp.getRSID());
        OWLDataPropertyAssertionAxiom rsid_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_snp_rsid, snpIndiv, rsid);
        AddAxiom add_rsid = new AddAxiom(ontology, rsid_relation);
        getManager().applyChange(add_rsid);

        // assert bp_pos relation
        OWLLiteral bp_pos = getDataFactory().getOWLLiteral(snp.getSNPLocation());
        OWLDataPropertyAssertionAxiom bp_pos_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_bp_pos, snpIndiv, bp_pos);
        AddAxiom add_bp_pos = new AddAxiom(ontology, bp_pos_relation);
        getManager().applyChange(add_bp_pos);

        // get the band class
        OWLClass bandClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));

        // create a new band individual
        OWLNamedIndividual bandIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, "band", snp));

        // assert class membership
        OWLClassAssertionAxiom bandClassAssertion = getDataFactory().getOWLClassAssertionAxiom(bandClass, bandIndiv);
        getManager().addAxiom(ontology, bandClassAssertion);

        // get datatype relations
        OWLDataProperty has_name = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

        // assert name relation
        OWLLiteral name = getDataFactory().getOWLLiteral(snp.getCytogeneticBandName());
        OWLDataPropertyAssertionAxiom name_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_name, bandIndiv, name);
        AddAxiom add_name = new AddAxiom(ontology, name_relation);
        getManager().applyChange(add_name);

        // get object properties
        OWLObjectProperty located_in = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));

        // assert relation
        OWLObjectPropertyAssertionAxiom located_in_relation =
                getDataFactory().getOWLObjectPropertyAssertionAxiom(located_in, snpIndiv, bandIndiv);
        AddAxiom add_located_in = new AddAxiom(ontology, located_in_relation);
        getManager().applyChange(add_located_in);

        // get the chromosome class
        OWLClass chrClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));

        // create a new chromosome individual
        OWLNamedIndividual chrIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, "chromosome", snp));

        // assert class membership
        OWLClassAssertionAxiom chrClassAssertion = getDataFactory().getOWLClassAssertionAxiom(chrClass, chrIndiv);
        getManager().addAxiom(ontology, chrClassAssertion);

        // get datatype relations
        OWLDataProperty has_chr_name = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

        // assert chr_name relation
        OWLLiteral chr_name = getDataFactory().getOWLLiteral(snp.getChromosomeName());
        OWLDataPropertyAssertionAxiom chr_name_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_chr_name, chrIndiv, chr_name);
        AddAxiom add_chr_name = new AddAxiom(ontology, chr_name_relation);
        getManager().applyChange(add_chr_name);

        // get object properties
        OWLObjectProperty has_part = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.HAS_PART_PROPERTY_IRI));

        // assert relation
        OWLObjectPropertyAssertionAxiom has_part_relation =
                getDataFactory().getOWLObjectPropertyAssertionAxiom(has_part, chrIndiv, bandIndiv);
        AddAxiom add_has_part = new AddAxiom(ontology, has_part_relation);
        getManager().applyChange(add_has_part);
    }

    protected void convertAssociation(TraitAssociation association, OWLOntology ontology, Set<String> issuedWarnings) {
        // get the trait association class
        OWLClass taClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));

        // create a new trait association instance
        OWLNamedIndividual taIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, association));

        // assert class membership
        OWLClassAssertionAxiom classAssertion = getDataFactory().getOWLClassAssertionAxiom(taClass, taIndiv);
        getManager().addAxiom(ontology, classAssertion);

        // get datatype relations
        OWLDataProperty has_p_value = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_P_VALUE_PROPERTY_IRI));

        // assert pValue relation
        OWLLiteral pValue = getDataFactory().getOWLLiteral(association.getPValue());
        OWLDataPropertyAssertionAxiom p_value_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_p_value, taIndiv, pValue);
        AddAxiom add_p_value = new AddAxiom(ontology, p_value_relation);
        getManager().applyChange(add_p_value);

        // get the snp instance for this association
        OWLNamedIndividual snpIndiv;
        try {
            snpIndiv = getDataFactory().getOWLNamedIndividual(
                    getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, association.getAssociatedSNP()));
        }
        catch (ObjectMappingException e) {
            String warning = e.getMessage() + ": a new SNP with the given RSID only will be created";
            if (!issuedWarnings.contains(warning)) {
                getLog().warn(warning);
                issuedWarnings.add(warning);
            }
            snpIndiv = getDataFactory().getOWLNamedIndividual(
                    getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, "snp", association));
        }

        // get object properties
        OWLObjectProperty associated_with = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.ASSOCIATED_WITH_PROPERTY_IRI));

        // assert relation
        OWLObjectPropertyAssertionAxiom associated_with_snp_relation =
                getDataFactory().getOWLObjectPropertyAssertionAxiom(associated_with, taIndiv, snpIndiv);
        AddAxiom add_associated_with_snp = new AddAxiom(ontology, associated_with_snp_relation);
        getManager().applyChange(add_associated_with_snp);

        // create a new trait association instance
        OWLNamedIndividual traitIndiv = getDataFactory().getOWLNamedIndividual(
                getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, "trait", association));

        // create new instance of it's trait
        OWLClass traitClass;
        try {
            traitClass = association.getAssociatedTrait();
        }
        catch (OntologyTermException e) {
            // catch the exception for missing ontology terms, and map to experimental factor plus label
            String warning = e.getMessage() + ": this trait will be mapped to Experimental Factor";
            if (!issuedWarnings.contains(warning)) {
                getLog().warn(warning);
                issuedWarnings.add(warning);
            }
            traitClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));

            // and also add the gwas label to the individual so we don't lose it
            OWLDataProperty has_gwas_trait_name = getDataFactory().getOWLDataProperty(
                    IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

            // assert pValue relation
            OWLLiteral gwasTrait = getDataFactory().getOWLLiteral(association.getUnmappedGWASLabel());
            OWLDataPropertyAssertionAxiom gwas_trait_relation =
                    getDataFactory().getOWLDataPropertyAssertionAxiom(has_gwas_trait_name, taIndiv, gwasTrait);
            AddAxiom add_gwas_trait_name = new AddAxiom(ontology, gwas_trait_relation);
            getManager().applyChange(add_gwas_trait_name);
        }

        // assert class membership
        OWLClassAssertionAxiom traitClassAssertion =
                getDataFactory().getOWLClassAssertionAxiom(traitClass, traitIndiv);
        getManager().addAxiom(ontology, traitClassAssertion);

        // assert relation
        OWLObjectPropertyAssertionAxiom associated_with_trait_relation =
                getDataFactory().getOWLObjectPropertyAssertionAxiom(associated_with, taIndiv, traitIndiv);
        AddAxiom add_associated_with_trait = new AddAxiom(ontology, associated_with_trait_relation);
        getManager().applyChange(add_associated_with_trait);
    }
}
