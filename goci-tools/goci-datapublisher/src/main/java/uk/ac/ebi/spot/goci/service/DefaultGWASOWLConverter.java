package uk.ac.ebi.spot.goci.service;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.ImportChange;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.exception.OWLConversionException;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Location;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Region;
import uk.ac.ebi.spot.goci.model.RiskAllele;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.ontology.OntologyConstants;
import uk.ac.ebi.spot.goci.ontology.ReflexiveIRIMinter;
import uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A default implementation of {@link GWASOWLConverter} that fetches data from the GWAS catalog using a {@link
 * uk.ac.ebi.spot.goci.ontology.owl.OntologyLoader} and converts all obtained {@link Study} objects to OWL.
 *
 * @author Tony Burdett Date 26/01/12
 */
@Service
public class DefaultGWASOWLConverter implements GWASOWLConverter {
    //    private OntologyConfiguration configuration;

    private OntologyLoader ontologyLoader;

    private ReflexiveIRIMinter minter;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    public DefaultGWASOWLConverter(OntologyLoader ontologyLoader) {
        this.ontologyLoader = ontologyLoader;
        this.minter = new ReflexiveIRIMinter();
    }

    public OWLOntologyManager getManager() {
        return this.ontologyLoader.getOntology().getOWLOntologyManager();
    }

    public OWLDataFactory getDataFactory() {
        return getManager().getOWLDataFactory();
    }

    public ReflexiveIRIMinter getMinter() {
        return minter;
    }

    public OWLOntology createConversionOntology() throws OWLConversionException {
        try {
            // create a new graph to represent our data dump
            OWLOntology conversion =
                    getManager().createOntology(IRI.create(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI + "/" +
                                                                   new SimpleDateFormat("yyyy/MM/dd").format(new Date())));

            // import the gwas ontology schema and efo
            OWLImportsDeclaration gwasImportDecl = getDataFactory().getOWLImportsDeclaration(
                    IRI.create(OntologyConstants.GWAS_ONTOLOGY_SCHEMA_IRI));
            ImportChange gwasImport = new AddImport(conversion, gwasImportDecl);
            getManager().applyChange(gwasImport);

            OWLImportsDeclaration efoImportDecl = getDataFactory().getOWLImportsDeclaration(
                    IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI));
            ImportChange efoImport = new AddImport(conversion, efoImportDecl);
            getManager().applyChange(efoImport);

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

    public void addAssociationsToOntology(Collection<Association> associations, OWLOntology ontology) {
        // the set of warnings that were issued during mappings
        Set<String> issuedWarnings = new HashSet<String>();
        for (Association association : associations) {
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

        // get annotation relations
        OWLAnnotationProperty rdfsLabel =
                getDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        // assert author relation
        // THOR
        OWLLiteral author = getDataFactory().getOWLLiteral(study.getPublicationId().getFirstAuthor().getFullname());
        OWLDataPropertyAssertionAxiom author_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_author, studyIndiv, author);
        AddAxiom add_author = new AddAxiom(ontology, author_relation);
        getManager().applyChange(add_author);

        // assert publication_date relation
        if (study.getPublicationId().getPublicationDate() != null) {
            String rfcTimezone =
                    new SimpleDateFormat("Z").format(study.getPublicationId().getPublicationDate());
            String xsdTimezone =
                    rfcTimezone.substring(0, 3).concat(":").concat(rfcTimezone.substring(3, rfcTimezone.length()));
            String xmlDatetimeStr =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(study.getPublicationId().getPublicationDate()) + xsdTimezone;
            OWLLiteral publication_date = getDataFactory().getOWLLiteral(xmlDatetimeStr, OWL2Datatype.XSD_DATE_TIME);
            OWLDataPropertyAssertionAxiom publication_date_relation =
                    getDataFactory().getOWLDataPropertyAssertionAxiom(has_publication_date,
                                                                      studyIndiv,
                                                                      publication_date);
            AddAxiom add_publication_date = new AddAxiom(ontology, publication_date_relation);
            getManager().applyChange(add_publication_date);
        }

        // assert pubmed_id relation
        OWLLiteral pubmed_id = getDataFactory().getOWLLiteral(study.getPublicationId().getPubmedId());
        OWLDataPropertyAssertionAxiom pubmed_id_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_pubmed_id, studyIndiv, pubmed_id);
        AddAxiom add_pubmed_id = new AddAxiom(ontology, pubmed_id_relation);
        getManager().applyChange(add_pubmed_id);

        // assert label
        OWLLiteral study_label = getDataFactory().getOWLLiteral(pubmed_id.toString());
        OWLAnnotationAssertionAxiom label_annotation =
                getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel, studyIndiv.getIRI(), study_label);
        AddAxiom add_label = new AddAxiom(ontology, label_annotation);
        getManager().applyChange(add_label);

        // add object properties...

        // get the has_part relation
        OWLObjectProperty has_part = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.HAS_PART_PROPERTY_IRI));

        OWLObjectProperty part_of = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));

        // for this study, get all trait associations 
        Collection<Association> associations = study.getAssociations();
        // and create an study has_part association assertion for each one
        for (Association association : associations) {
            // get the trait association instance for this association

            IRI traitIRI = getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, association);
            OWLNamedIndividual taIndiv = getDataFactory().getOWLNamedIndividual(traitIRI);
            // assert relation
            OWLObjectPropertyAssertionAxiom has_part_relation =
                    getDataFactory().getOWLObjectPropertyAssertionAxiom(has_part, studyIndiv, taIndiv);
            AddAxiom addAxiomChange = new AddAxiom(ontology, has_part_relation);
            getManager().applyChange(addAxiomChange);

            OWLObjectPropertyAssertionAxiom is_part_of_relation =
                    getDataFactory().getOWLObjectPropertyAssertionAxiom(part_of, taIndiv, studyIndiv);
            AddAxiom addAxiomChangeRev = new AddAxiom(ontology, is_part_of_relation);
            getManager().applyChange(addAxiomChangeRev);

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

        // get annotation relations
        OWLAnnotationProperty rdfsLabel =
                getDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        // assert rsid relation

        OWLLiteral rsid = getDataFactory().getOWLLiteral(snp.getRsId());
        OWLDataPropertyAssertionAxiom rsid_relation =
                getDataFactory().getOWLDataPropertyAssertionAxiom(has_snp_rsid, snpIndiv, rsid);
        AddAxiom add_rsid = new AddAxiom(ontology, rsid_relation);
        getManager().applyChange(add_rsid);

        // assert bp_pos relation
        if (snp.getLocations() != null) {
            for (Location snpLocation : snp.getLocations()) {
                if (snpLocation.getChromosomePosition() != null) {
                    OWLLiteral bp_pos =
//                            getDataFactory().getOWLLiteral(snpLocation.getChromosomePosition(), OWL2Datatype.XSD_INT);
                            getDataFactory().getOWLLiteral(snpLocation.getChromosomePosition());

                    OWLDataPropertyAssertionAxiom bp_pos_relation =
                            getDataFactory().getOWLDataPropertyAssertionAxiom(has_bp_pos, snpIndiv, bp_pos);
                    AddAxiom add_bp_pos = new AddAxiom(ontology, bp_pos_relation);
                    getManager().applyChange(add_bp_pos);
                }
            }
        }
        else {
            getLog().debug("No SNP location available for SNP " + rsid);
        }

        // assert label
        OWLAnnotationAssertionAxiom snp_label_annotation =
                getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel, snpIndiv.getIRI(), rsid);
        AddAxiom add_snp_label = new AddAxiom(ontology, snp_label_annotation);
        getManager().applyChange(add_snp_label);

        // get the band class
        OWLClass bandClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.CYTOGENIC_REGION_CLASS_IRI));

        // get datatype relations
        OWLDataProperty has_name = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

        // get object relations
        OWLObjectProperty located_in = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
        OWLObjectProperty location_of = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.LOCATION_OF_PROPERTY_IRI));

        // get datatype relations
        OWLDataProperty has_chr_name = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

        // get object properties
        OWLObjectProperty has_part = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.HAS_PART_PROPERTY_IRI));
        OWLObjectProperty part_of = getDataFactory().getOWLObjectProperty(
                IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));

        for (Location location : snp.getLocations()) {
            Region region = location.getRegion();

            if (region.getName() != null){
                // create a new band individual
                OWLNamedIndividual bandIndiv = getDataFactory().getOWLNamedIndividual(
                        getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI,
                                         "CytogeneticRegion",
                                         region.getName())
                );

                // assert class membership
                OWLClassAssertionAxiom bandClassAssertion =
                        getDataFactory().getOWLClassAssertionAxiom(bandClass, bandIndiv);
                getManager().addAxiom(ontology, bandClassAssertion);


                // assert name relation
                OWLLiteral name = getDataFactory().getOWLLiteral(region.getName());
                OWLDataPropertyAssertionAxiom name_relation =
                        getDataFactory().getOWLDataPropertyAssertionAxiom(has_name, bandIndiv, name);
                AddAxiom add_name = new AddAxiom(ontology, name_relation);
                getManager().applyChange(add_name);

                // assert label
                OWLAnnotationAssertionAxiom band_label_annotation =
                        getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel, bandIndiv.getIRI(), name);
                AddAxiom add_band_label = new AddAxiom(ontology, band_label_annotation);
                getManager().applyChange(add_band_label);

                // assert located_in relation
                OWLObjectPropertyAssertionAxiom located_in_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(located_in, snpIndiv, bandIndiv);
                AddAxiom add_located_in = new AddAxiom(ontology, located_in_relation);
                getManager().applyChange(add_located_in);

                // assert location_of relation
                OWLObjectPropertyAssertionAxiom location_of_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(location_of, bandIndiv, snpIndiv);
                AddAxiom add_location_of = new AddAxiom(ontology, location_of_relation);
                getManager().applyChange(add_location_of);

                // get the appropriate chromosome class given the chromosome name
                OWLClass chrClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.CHROMOSOME_CLASS_IRI));

                // create a new chromosome individual
                //If a snp has a chromosome name, create the chromosome individual if it doesn't have one (ex : the snp is
                // no mapped any more) then just don't create it.

                String chromName = location.getChromosomeName();
                if (chromName != null) {
                    OWLNamedIndividual chrIndiv = getDataFactory().getOWLNamedIndividual(
                            getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI,
                                             "Chromosome",
                                             chromName));

                    OWLClassAssertionAxiom chrClassAssertion =
                            getDataFactory().getOWLClassAssertionAxiom(chrClass, chrIndiv);
                    getManager().addAxiom(ontology, chrClassAssertion);

                    // assert chr_name relation
                    OWLLiteral chr_name = getDataFactory().getOWLLiteral(chromName);
                    OWLDataPropertyAssertionAxiom chr_name_relation =
                            getDataFactory().getOWLDataPropertyAssertionAxiom(has_chr_name, chrIndiv, chr_name);
                    AddAxiom add_chr_name = new AddAxiom(ontology, chr_name_relation);
                    getManager().applyChange(add_chr_name);

                    // assert label
                    OWLLiteral chr_label = getDataFactory().getOWLLiteral("Chromosome " + chromName);
                    OWLAnnotationAssertionAxiom chr_label_annotation =
                            getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel,
                                                                            chrIndiv.getIRI(),
                                                                            chr_label);
                    AddAxiom add_chr_label = new AddAxiom(ontology, chr_label_annotation);
                    getManager().applyChange(add_chr_label);

                    // assert has_part relation
                    OWLObjectPropertyAssertionAxiom has_part_relation =
                            getDataFactory().getOWLObjectPropertyAssertionAxiom(has_part, chrIndiv, bandIndiv);
                    AddAxiom add_has_part = new AddAxiom(ontology, has_part_relation);
                    getManager().applyChange(add_has_part);

                    // assert part_of relation
                    OWLObjectPropertyAssertionAxiom part_of_relation =
                            getDataFactory().getOWLObjectPropertyAssertionAxiom(part_of, bandIndiv, chrIndiv);
                    AddAxiom add_part_of = new AddAxiom(ontology, part_of_relation);
                    getManager().applyChange(add_part_of);

                }
            }
            else {
                getLog().trace("No known region for location on chromosomse " + location.getChromosomeName());
            }

        }
    }

    protected void convertAssociation(Association association, OWLOntology ontology, Set<String> issuedWarnings) {
        // get the trait association class
        OWLClass taClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));

        IRI taIndIRI = getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, association);

        // create a new trait association instance
        OWLNamedIndividual taIndiv = getDataFactory().getOWLNamedIndividual(taIndIRI);

        // assert class membership
        OWLClassAssertionAxiom classAssertion = getDataFactory().getOWLClassAssertionAxiom(taClass, taIndiv);
        getManager().addAxiom(ontology, classAssertion);

        // get datatype relations
        OWLDataProperty has_p_value = getDataFactory().getOWLDataProperty(
                IRI.create(OntologyConstants.HAS_P_VALUE_PROPERTY_IRI));

        // get annotation relations
        OWLAnnotationProperty rdfsLabel =
                getDataFactory().getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());

        // assert pValue relation
        //Sometimes there won't be any pvalue associated with an association. For example if the author doesn't give the
        //pvalue but says it was less then 10-6. So if we have no pvalue we just don't add it.
        if (association.getPvalueMantissa() != null && association.getPvalueExponent() != null) {
            double pval = association.getPvalueMantissa() * Math.pow(10, association.getPvalueExponent());
            OWLLiteral pValue = getDataFactory().getOWLLiteral(pval);

            //            OWLLiteral pValue = getDataFactory().getOWLLiteral(association.getPvalueMantissa()+"e"+association.getPvalueExponent());
            OWLDataPropertyAssertionAxiom p_value_relation =
                    getDataFactory().getOWLDataPropertyAssertionAxiom(has_p_value, taIndiv, pValue);
            AddAxiom add_p_value = new AddAxiom(ontology, p_value_relation);
            getManager().applyChange(add_p_value);
        }
        // get the snp instance for this association
        OWLNamedIndividual snpIndiv;
        String rsId = null;
        for (Locus locus : association.getLoci()) {
            for (RiskAllele riskAllele : locus.getStrongestRiskAlleles()) {
                SingleNucleotidePolymorphism snp = riskAllele.getSnp();
                rsId = snp.getRsId();

                snpIndiv = getDataFactory().getOWLNamedIndividual(
                        getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI, snp));

                if (snpIndiv == null) {
                    String warning = "A new SNP with the given RSID only will be created";

                    if (!issuedWarnings.contains(warning)) {
                        getLog().warn(warning);
                        issuedWarnings.add(warning);
                    }

                    snpIndiv = getDataFactory().getOWLNamedIndividual(
                            getMinter().mint(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI,
                                             "SingleNucleotidePolymorphism",
                                             snp.getRsId(),
                                             true));

                    // assert class membership
                    OWLClass snpClass = getDataFactory().getOWLClass(IRI.create(OntologyConstants.SNP_CLASS_IRI));
                    OWLClassAssertionAxiom snpClassAssertion =
                            getDataFactory().getOWLClassAssertionAxiom(snpClass, snpIndiv);
                    getManager().addAxiom(ontology, snpClassAssertion);

                    // assert rsid relation
                    OWLDataProperty has_snp_rsid = getDataFactory().getOWLDataProperty(
                            IRI.create(OntologyConstants.HAS_SNP_REFERENCE_ID_PROPERTY_IRI));
                    OWLLiteral rsid = getDataFactory().getOWLLiteral(snp.getRsId());
                    OWLDataPropertyAssertionAxiom rsid_relation =
                            getDataFactory().getOWLDataPropertyAssertionAxiom(has_snp_rsid, snpIndiv, rsid);
                    AddAxiom add_rsid = new AddAxiom(ontology, rsid_relation);
                    getManager().applyChange(add_rsid);

                    // assert label
                    OWLAnnotationAssertionAxiom snp_label_annotation =
                            getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel, snpIndiv.getIRI(), rsid);
                    AddAxiom add_snp_label = new AddAxiom(ontology, snp_label_annotation);
                    getManager().applyChange(add_snp_label);

                }

                // get object properties
                OWLObjectProperty has_subject =
                        getDataFactory().getOWLObjectProperty(IRI.create(OntologyConstants.HAS_SUBJECT_IRI));
                OWLObjectProperty is_subject_of =
                        getDataFactory().getOWLObjectProperty(IRI.create(OntologyConstants.IS_SUBJECT_OF_IRI));

                // assert relations
                OWLObjectPropertyAssertionAxiom has_subject_snp_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(has_subject, taIndiv, snpIndiv);
                AddAxiom add_has_subject_snp = new AddAxiom(ontology, has_subject_snp_relation);
                getManager().applyChange(add_has_subject_snp);

                OWLObjectPropertyAssertionAxiom is_subject_of_snp_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(is_subject_of, snpIndiv, taIndiv);
                AddAxiom add_is_subject_of_snp = new AddAxiom(ontology, is_subject_of_snp_relation);
                getManager().applyChange(add_is_subject_of_snp);
            }

            // get the EFO class for the trait
            for (EfoTrait efoTrait : association.getEfoTraits()) {
                OWLClass traitClass;
                traitClass = getDataFactory().getOWLClass(IRI.create(efoTrait.getUri()));

                if (traitClass == null) {
                    String warning = "This trait will be mapped to Experimental Factor";
                    if (!issuedWarnings.contains(warning)) {
                        getLog().warn(warning);
                        issuedWarnings.add(warning);
                    }
                    traitClass =
                            getDataFactory().getOWLClass(IRI.create(OntologyConstants.EXPERIMENTAL_FACTOR_CLASS_IRI));
                }

                // create a new trait instance (puns the class)
                IRI traitIRI = traitClass.getIRI();
                OWLNamedIndividual traitIndiv = getDataFactory().getOWLNamedIndividual(traitIRI);

                if (ontology.containsIndividualInSignature(traitIRI)) {
                    getLog().trace(
                            "Trait individual '" + traitIRI.toString() + "' (type: " + traitClass + ") already exists");
                }
                else {
                    getLog().trace(
                            "Creating trait individual '" + traitIRI.toString() + "' (type: " + traitClass + ")");
                }

                // and also add the gwas label to the individual so we don't lose curated data
                OWLDataProperty has_gwas_trait_name = getDataFactory().getOWLDataProperty(
                        IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

                OWLLiteral gwasTrait =
                        getDataFactory().getOWLLiteral(association.getStudy().getDiseaseTrait().getTrait());

                OWLDataPropertyAssertionAxiom gwas_trait_relation =
                        getDataFactory().getOWLDataPropertyAssertionAxiom(has_gwas_trait_name, taIndiv, gwasTrait);
                AddAxiom add_gwas_trait_name = new AddAxiom(ontology, gwas_trait_relation);
                getManager().applyChange(add_gwas_trait_name);


                // assert class membership
                OWLClassAssertionAxiom traitClassAssertion =
                        getDataFactory().getOWLClassAssertionAxiom(traitClass, traitIndiv);
                getManager().addAxiom(ontology, traitClassAssertion);

                // get object properties
                OWLObjectProperty has_object =
                        getDataFactory().getOWLObjectProperty(IRI.create(OntologyConstants.HAS_OBJECT_IRI));
                OWLObjectProperty is_object_of =
                        getDataFactory().getOWLObjectProperty(IRI.create(OntologyConstants.IS_OBJECT_OF_IRI));

                // assert relations
                OWLObjectPropertyAssertionAxiom has_object_trait_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(has_object, taIndiv, traitIndiv);
                AddAxiom add_has_object_trait = new AddAxiom(ontology, has_object_trait_relation);
                getManager().applyChange(add_has_object_trait);

                OWLObjectPropertyAssertionAxiom is_object_of_trait_relation =
                        getDataFactory().getOWLObjectPropertyAssertionAxiom(is_object_of, traitIndiv, taIndiv);
                AddAxiom add_is_object_of_trait = new AddAxiom(ontology, is_object_of_trait_relation);
                getManager().applyChange(add_is_object_of_trait);


            }
            // finally, assert label for this association
            OWLLiteral label = getDataFactory().getOWLLiteral(
                    "Association between " + rsId + " and " +
                            association.getStudy().getDiseaseTrait().getTrait());

            OWLAnnotationAssertionAxiom label_annotation =
                    getDataFactory().getOWLAnnotationAssertionAxiom(rdfsLabel, taIndiv.getIRI(), label);
            AddAxiom add_band_label = new AddAxiom(ontology, label_annotation);
            getManager().applyChange(add_band_label);


        }
    }
}
