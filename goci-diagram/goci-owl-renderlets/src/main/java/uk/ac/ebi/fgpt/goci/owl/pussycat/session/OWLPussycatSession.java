package uk.ac.ebi.fgpt.goci.owl.pussycat.session;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import uk.ac.ebi.fgpt.goci.dao.OntologyDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.Filter;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.model.AssociationSummary;
import uk.ac.ebi.fgpt.goci.owl.lang.OWLAPIFilterInterpreter;
import uk.ac.ebi.fgpt.goci.owl.pussycat.layout.LayoutUtils;
import uk.ac.ebi.fgpt.goci.owl.pussycat.reasoning.ReasonerSession;
import uk.ac.ebi.fgpt.goci.pussycat.exception.DataIntegrityViolationException;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.layout.BandInformation;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.Renderlet;
import uk.ac.ebi.fgpt.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.fgpt.goci.pussycat.session.AbstractSVGIOPussycatSession;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An OWL-based implementation of a Pussycat session.  Loads a knowledgebase from an OWL file, reasons over the whole
 * knowledgebase, and derives OWLClassExpressions from filters to perform renderings of the knowledge contained.
 * <p/>
 * Utilises reasoners that are capable of rendering OWLOntology and OWLIndividual data
 *
 * @author Tony Burdett
 * @date 28/07/14
 */
public class OWLPussycatSession extends AbstractSVGIOPussycatSession {
    private final OWLAPIFilterInterpreter filterInterpreter;

    private final OntologyConfiguration ontologyConfiguration;
    private final OntologyDAO ontologyDAO;
    private final ReasonerSession reasonerSession;

    private boolean rendering = false;

    public OWLPussycatSession(OWLAPIFilterInterpreter filterInterpreter,
                              OntologyConfiguration ontologyConfiguration,
                              OntologyDAO ontologyDAO,
                              ReasonerSession reasonerSession) {
        super();
        this.filterInterpreter = filterInterpreter;
        this.ontologyConfiguration = ontologyConfiguration;
        this.ontologyDAO = ontologyDAO;
        this.reasonerSession = reasonerSession;
    }

    public synchronized boolean isRendering() {
        return rendering;
    }

    public synchronized void setRendering(boolean rendering) {
        this.rendering = rendering;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    public OntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    public ReasonerSession getReasonerSession() {
        return reasonerSession;
    }

    public OWLReasoner getReasoner() throws OWLConversionException, PussycatSessionNotReadyException {
        if (getReasonerSession().isReasonerInitialized()) {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is fully initialized and ready to serve data");
            return getReasonerSession().getReasoner();
        }
        else {
            getLog().debug("Pussycat Session '" + getSessionID() + "' is not yet initialized - waiting for reasoner");
            throw new PussycatSessionNotReadyException("Reasoner is being initialized");
        }
    }

    @Override public String performRendering(RenderletNexus renderletNexus, Filter... filters)
            throws PussycatSessionNotReadyException {
        // derive class expression
        OWLClassExpression classExpression = filterInterpreter.interpretFilters(filters);

        getLog().debug("Rendering SVG for OWLClass '" + classExpression + "'...");
        try {
            // attempt to query the reasoner for data
            getLog().trace("Attempting to obtain reasoner to use in rendering...");
            OWLReasoner reasoner = getReasoner();
            getLog().trace("Acquired reasoner OK!");

            // now render
            if (!isRendering()) {
                setRendering(true);
                Set<OWLClass> classes = reasoner.getSubClasses(classExpression, false).getFlattened();
                Set<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression, false).getFlattened();

                // render classes first
                for (OWLClass cls : classes) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, reasoner, cls)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, reasoner, cls);
                        }
                    }
                }

                // then render individuals, sorted into the order we need them in for rendering
                List<OWLNamedIndividual> sortedIndividuals = sortIndividualsIntoRenderingOrder(reasoner, individuals);
                for (OWLNamedIndividual individual : sortedIndividuals) {
                    for (Renderlet r : getAvailableRenderlets()) {
                        if (r.canRender(renderletNexus, reasoner, individual)) {
                            getLog().trace("Dispatching render() request to renderlet '" + r.getName() + "'");
                            r.render(renderletNexus, reasoner, individual);
                        }
                    }
                }
                setRendering(false);
                return renderletNexus.getSVG();
            }
            else {
                throw new PussycatSessionNotReadyException("The GWAS diagram is currently being rendered");
            }
        }
        catch (OWLConversionException e) {
            throw new RuntimeException("Failed to initialize reasoner - cannot render SVG", e);
        }
    }

    @Override public List<AssociationSummary> getAssociationSummaries(List<URI> associationURIs) {
        List<AssociationSummary> summaries = new ArrayList<AssociationSummary>();

        OWLOntology ontology = getOntologyDAO().getOntology();
        OWLDataFactory df = ontologyConfiguration.getOWLDataFactory();

        for (URI associationURI : associationURIs) {
            getLog().debug("Acquiring information for association " + associationURI);

            String rs_id;
            String pm_id;
            String author;
            String pub_date;
            String pval;
            String gwastrait;
            String efotrait;
            URI efouri;

            IRI iri = IRI.create(associationURI);
            OWLNamedIndividual association = df.getOWLNamedIndividual(iri);
            getLog().debug("Got the OWL individual " + association);

            //get the SNP and the trait
            OWLObjectProperty has_subject = df.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_SUBJECT_IRI));
            Set<OWLIndividual> related = association.getObjectPropertyValues(has_subject, ontology);

            IRI snp_class = IRI.create(OntologyConstants.SNP_CLASS_IRI);
            OWLNamedIndividual snp = null;
            OWLNamedIndividual trait = null;

            // get the trait associated with this association
            for (OWLIndividual ind : related) {
                boolean isSNP = checkType((OWLNamedIndividual) ind, ontology, snp_class);
                if (isSNP) {
                    snp = (OWLNamedIndividual) ind;
                    getLog().debug("The SNP for this association is " + snp);
                }
                else {
                    trait = (OWLNamedIndividual) ind;
                    getLog().debug("The trait for this association is " + trait);
                }
            }

            // get the efo
            if (trait != null) {
                Set<OWLClassExpression> allTypes = trait.getTypes(ontology);
                if (allTypes.isEmpty()) {
                    OWLClass typeClass = allTypes.iterator().next().asOWLClass();
                    Set<String> labels = getOntologyDAO().getClassRDFSLabels(typeClass);
                    efotrait = labels.size() > 0 ? labels.iterator().next() : "unknown";
                    efouri = typeClass.getIRI().toURI();
                    getLog().debug("The EFO label and URI are " + efotrait + " and " + efouri);

                }
                else {
                    efotrait = "Unknown";
                    efouri = null;
                }
            }
            else {
                getLog().error("Unable to identify the trait linked to association '" + associationURI + "'");
                efotrait = "unknown";
                efouri = null;
            }

            // get the gwas trait name
            OWLDataProperty has_name =
                    df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));
            if (association.getDataPropertyValues(has_name, ontology).size() != 0) {
                OWLLiteral name = association.getDataPropertyValues(has_name, ontology).iterator().next();
                gwastrait = name.getLiteral();
                getLog().debug("The GWAS trait for this association is " + gwastrait);
            }
            else {
                getLog().error("No gwas trait name for association '" + associationURI + "'");
                gwastrait = "unknown";
            }

            //get the pvalue
            OWLDataProperty has_pval = df.getOWLDataProperty((IRI.create(OntologyConstants.HAS_P_VALUE_PROPERTY_IRI)));
            if (association.getDataPropertyValues(has_pval, ontology).size() != 0) {
                OWLLiteral p = association.getDataPropertyValues(has_pval, ontology).iterator().next();
                pval = p.getLiteral();
                getLog().debug("The p-value for this association is " + pval);
            }
            else {
                getLog().error("No p-value for association '" + associationURI + "'");
                pval = "unknown";
            }

            //get the RS id for the SNP
            if (snp != null) {
                OWLDataProperty has_rsID =
                        df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_SNP_REFERENCE_ID_PROPERTY_IRI));
                if (snp.getDataPropertyValues(has_rsID, ontology).size() != 0) {
                    OWLLiteral id = snp.getDataPropertyValues(has_rsID, ontology).iterator().next();
                    rs_id = id.getLiteral();
                    getLog().debug("The RS id is " + rs_id);
                }
                else {
                    getLog().error("Unable to acquire SNP rsID for snp '" + snp.getIRI() + "'");
                    rs_id = "N/A";
                }
            }
            else {
                getLog().error("No SNP related to association '" + association + "'");
                rs_id = "N/A";
            }

            //get the Pubmed ID of the study
            OWLObjectProperty part_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));
            Set<OWLIndividual> studies = association.getObjectPropertyValues(part_of, ontology);
            OWLDataProperty has_pmid = df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_PUBMED_ID_PROPERTY_IRI));
            OWLDataProperty has_author =
                    df.getOWLDataProperty((IRI.create((OntologyConstants.HAS_AUTHOR_PROPERTY_IRI))));
            OWLDataProperty has_pubdate =
                    df.getOWLDataProperty((IRI.create(OntologyConstants.HAS_PUBLICATION_DATE_PROPERTY_IRI)));

            if (studies.isEmpty()) {
                getLog().error("No study identified for association '" + association + "'");
                throw new RuntimeException("No study identified for association '" + association + "'");
            }
            else {
                if (studies.size() != 1) {
                    String message = "Wrong number of studies for association '" + association + " - should be 1, " +
                            "actually " + studies.size();
                    getLog().error(message);
                    throw new RuntimeException(message);
                }
                else {
                    // ok
                    OWLIndividual study = studies.iterator().next();
                    Set<OWLLiteral> pmids = study.getDataPropertyValues(has_pmid, ontology);
                    if (pmids.size() != 1) {
                        String message = "Wrong number of PubMed IDs for study '" + study + "' - should be" +
                                " 1, actually " + pmids.size();
                        getLog().error(message);
                        pm_id = "N/A";
                    }
                    else {
                        pm_id = pmids.iterator().next().getLiteral();
                        getLog().debug("The pm_id is " + pm_id);
                    }

                    Set<OWLLiteral> authors = study.getDataPropertyValues(has_author, ontology);
                    if (authors.size() != 1) {
                        String message = "Wrong number of authors for study '" + study + "' - should be" +
                                " 1, actually " + authors.size();
                        getLog().error(message);
                        author = "Unknown";
                    }
                    else {
                        author = authors.iterator().next().getLiteral();
                        getLog().debug("The author is " + author);
                    }

                    Set<OWLLiteral> dates = study.getDataPropertyValues(has_pubdate, ontology);
                    if (dates.size() != 1) {
                        String message = "Wrong number of publication dates for study '" + study + "' - " +
                                "should be 1, actually " + authors.size();
                        getLog().error(message);
                        pub_date = "Unknown";
                    }
                    else {
                        pub_date = dates.iterator().next().getLiteral();
                        pub_date = pub_date.substring(0, 4);
                        getLog().debug("The publication date is " + pub_date);
                    }
                }
            }

            AssociationSummary summary =
                    new OWLAssociationSummary(pm_id, author, pub_date, rs_id, pval, gwastrait, efotrait, efouri);
            summaries.add(summary);
        }
        return summaries;
    }

    @Override public Set<URI> getRelatedTraits(String traitName) {
        // lookup class from label
        getLog().debug("Filtering on classes with label '" + traitName + "'");
        Set<OWLClass> allClasses = null;
        try {
            Collection<OWLClass> labelledClasses = getOntologyDAO().getOWLClassesByLabel(traitName);
            allClasses = new HashSet<OWLClass>();
            for (OWLClass labelledClass : labelledClasses) {
                allClasses.add(labelledClass);
                allClasses.addAll(getReasoner().getSubClasses(
                        labelledClass,
                        false).getFlattened());
            }
        }
        catch (OWLConversionException e) {
            String message = "Failed to identify traits related to '" + traitName + "'";
            getLog().error(message, e);
            throw new RuntimeException(message + ": " + e.getMessage());
        }
        catch (PussycatSessionNotReadyException e) {
            String message = "Failed to identify traits related to '" + traitName + "'";
            getLog().error(message, e);
            throw new RuntimeException(message + ": " + e.getMessage());
        }

        // now get the short forms for each class in allClasses
        Set<URI> classURIs = new HashSet<URI>();
        for (OWLClass cls : allClasses) {
            classURIs.add(cls.getIRI().toURI());
        }

        // return the URIs of all classes that are children, asserted or inferred, of the provided parent class
        return classURIs;
    }

    /**
     * Sorts a set of OWLIndividuals into an order suitable for rendering.  This essentially means associations should
     * be rendered first, followed by traits.  SNP individuals do not get rendered so their order is not important.
     *
     * @param individuals the set of individuals to sort
     * @return a list of individuals, sorted into suitable rendering order
     */
    private List<OWLNamedIndividual> sortIndividualsIntoRenderingOrder(final OWLReasoner reasoner,
                                                                       final Set<OWLNamedIndividual> individuals) {
        List<OWLNamedIndividual> sorted = new ArrayList<OWLNamedIndividual>();
        sorted.addAll(individuals);

        // sort all individuals of type association first, then of type "trait" second
        OWLDataFactory factory = reasoner.getRootOntology().getOWLOntologyManager().getOWLDataFactory();
        OWLClass associationCls = factory.getOWLClass(IRI.create(OntologyConstants.TRAIT_ASSOCIATION_CLASS_IRI));
        final Set<OWLNamedIndividual> associations = reasoner.getInstances(associationCls, false).getFlattened();

        Collections.sort(sorted, new Comparator<OWLNamedIndividual>() {
            @Override public int compare(OWLNamedIndividual o1, OWLNamedIndividual o2) {
                if (associations.contains(o1) && !associations.contains(o2)) {
                    return -1;
                }
                else {
                    if (associations.contains(o2) && !associations.contains(o1)) {
                        return 1;
                    }
                    else {
                        if (associations.contains(o1) && associations.contains(o2)) {
                            // both associations, sort according to the cytogenetic band
                            BandInformation band1, band2;
                            try {
                                OWLNamedIndividual bi1 =
                                        LayoutUtils.getCachingInstance().getCytogeneticBandForAssociation(reasoner, o1);
                                band1 = LayoutUtils.getCachingInstance().getBandInformation(reasoner, bi1);
                            }
                            catch (DataIntegrityViolationException e) {
                                getLog().debug("Can't properly sort association " + o1 + " - unable to identify band");
                                return 1;
                            }
                            try {
                                OWLNamedIndividual bi2 =
                                        LayoutUtils.getCachingInstance().getCytogeneticBandForAssociation(reasoner, o2);
                                band2 = LayoutUtils.getCachingInstance().getBandInformation(reasoner, bi2);
                            }
                            catch (DataIntegrityViolationException e) {
                                getLog().debug("Can't properly sort association " + o2 + " - unable to identify band");
                                return -1;
                            }
                            return band1.compareTo(band2);
                        }
                        else {
                            // other instance, not an association, so we don't care
                            return 0;
                        }
                    }
                }
            }
        });
        return sorted;
    }

    private boolean checkType(OWLNamedIndividual individual, OWLOntology ontology, IRI typeIRI) {
        boolean type = false;
        OWLClassExpression[] allTypes = individual.getTypes(ontology).toArray(new OWLClassExpression[0]);

        for (int i = 0; i < allTypes.length; i++) {
            OWLClass typeClass = allTypes[i].asOWLClass();

            if (typeClass.getIRI().equals(typeIRI)) {
                type = true;
                break;
            }
        }
        return type;
    }

    private class OWLAssociationSummary implements AssociationSummary {
        private final String pubmedID;
        private final String firstAuthor;
        private final String publicationDate;
        private final String snp;
        private final String pValue;
        private final String gwasTraitName;
        private final String efoTraitLabel;
        private final URI efoTraitURI;

        public OWLAssociationSummary(String pubmedID,
                                     String firstAuthor,
                                     String publicationDate,
                                     String snp,
                                     String pValue,
                                     String gwasTraitName,
                                     String efoTraitLabel,
                                     URI efoTraitURI) {
            this.pubmedID = pubmedID;
            this.firstAuthor = firstAuthor;
            this.publicationDate = publicationDate;
            this.snp = snp;
            this.pValue = pValue;
            this.gwasTraitName = gwasTraitName;
            this.efoTraitLabel = efoTraitLabel;
            this.efoTraitURI = efoTraitURI;
        }

        @Override public String getPubMedID() {
            return pubmedID;
        }

        @Override public String getFirstAuthor() {
            return firstAuthor;
        }

        @Override public String getPublicationDate() {
            return publicationDate;
        }

        public String getSNP() {
            return snp;
        }

        @Override public String getPvalue() {
            return pValue;
        }

        @Override public String getGWASTraitName() {
            return gwasTraitName;
        }

        @Override public String getEFOTraitLabel() {
            return efoTraitLabel;
        }

        @Override public URI getEFOTraitURI() {
            return efoTraitURI;
        }
    }
}