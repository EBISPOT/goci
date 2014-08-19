package uk.ac.ebi.fgpt.goci.pussycat.controller;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.ebi.fgpt.goci.dao.OntologyDAO;
import uk.ac.ebi.fgpt.goci.exception.OWLConversionException;
import uk.ac.ebi.fgpt.goci.lang.OntologyConfiguration;
import uk.ac.ebi.fgpt.goci.lang.OntologyConstants;
import uk.ac.ebi.fgpt.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.fgpt.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.fgpt.goci.pussycat.model.TraitSummary;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.fgpt.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA. User: dwelter Date: 24/09/12 Time: 14:51 To change this template use File | Settings |
 * File Templates.
 */
@Controller
@RequestMapping("/summaries")
public class PussycatSummariesController {
    private PussycatSessionStrategy sessionStrategy;
    private PussycatManager pussycatManager;

    private OntologyConfiguration ontologyConfiguration;

    private OntologyDAO ontologyDAO;

    private Map<IRI, String> ontologyLabelsMap;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public PussycatSessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    @Autowired
    public void setSessionStrategy(PussycatSessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public PussycatManager getPussycatManager() {
        return pussycatManager;
    }

    @Autowired
    public void setPussycatSessionManager(PussycatManager pussycatManager) {
        this.pussycatManager = pussycatManager;
    }

    public OntologyConfiguration getOntologyConfiguration() {
        return ontologyConfiguration;
    }

    @Autowired
    public void setOntologyConfiguration(@Qualifier("config") OntologyConfiguration ontologyConfiguration) {
        this.ontologyConfiguration = ontologyConfiguration;
    }

    public OntologyDAO getOntologyDAO() {
        return ontologyDAO;
    }

    @Autowired
    public void setOntologyDAO(@Qualifier("gwasDAO") OntologyDAO ontologyDAO) {
        this.ontologyDAO = ontologyDAO;
    }

    @RequestMapping(value = "/associations/{associationIds}")
    public @ResponseBody TraitSummary getAssociationSummary(@PathVariable String associationIds,
                                                            HttpSession session)
            throws PussycatSessionNotReadyException, OWLConversionException {
        getLog().debug("Received request to display information for associations " + associationIds);

        ArrayList<String> allIds = new ArrayList<String>();
        if (associationIds.contains(",")) {
            StringTokenizer tokenizer = new StringTokenizer(associationIds, ",");
            while (tokenizer.hasMoreTokens()) {
                String uri = tokenizer.nextToken();
                uri = uri.replace("gwas-diagram:", "http://www.ebi.ac.uk/efo/gwas-diagram/");
                allIds.add(uri);
            }
        }
        else {
            associationIds = associationIds.replace("gwas-diagram:", "http://www.ebi.ac.uk/efo/gwas-diagram/");
            allIds.add(associationIds);
        }

        getLog().debug("This trait represents " + allIds.size() + " different associations");

        return getSummary(allIds, session);
    }


    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(PussycatSessionNotReadyException.class)
    public @ResponseBody String handlePussycatSessionNotReadyException(PussycatSessionNotReadyException e) {
        String responseMsg = "Please wait while Pussycat starts up!<br/>" + e.getMessage();
        getLog().error(responseMsg, e);
        return responseMsg;
    }

    protected PussycatSession getPussycatSession(HttpSession session) {
        getLog().debug("Attempting to obtain Pussycat session for HttpSession '" + session.getId() + "'");
        if (getPussycatManager().hasAvailablePussycatSession(session)) {
            getLog().debug("Pussycat manager has an available session for HttpSession '" + session.getId() + "'");
            return getPussycatManager().getPussycatSession(session);
        }
        else {
            PussycatSession pussycatSession;
            if (getSessionStrategy() == PussycatSessionStrategy.JOIN &&
                    getPussycatManager().getPussycatSessions().size() > 0) {
                pussycatSession = getPussycatManager().getPussycatSessions().iterator().next();
            }
            else {
                pussycatSession = getPussycatManager().createPussycatSession();
                getLog().debug("Created new pussycat session, id '" + pussycatSession.getSessionID() + "'");
            }
            getLog().debug("Pussycat manager has no available session, but can join HttpSession " +
                                   "'" + session.getId() + "' to pussycat session " +
                                   "'" + pussycatSession.getSessionID() + "'");
            return getPussycatManager().bindPussycatSession(session, pussycatSession);
        }
    }

    protected TraitSummary getSummary(ArrayList<String> associationURIs, HttpSession session)
            throws PussycatSessionNotReadyException, OWLConversionException {
        TraitSummary summary = new TraitSummary();

        OWLOntology ontology = getOntologyDAO().getOntology();
        OWLDataFactory df = ontologyConfiguration.getOWLDataFactory();

        for (String associationURI : associationURIs) {
            getLog().debug("Acquiring information for association " + associationURI);

            String rs_id = null;
            String pm_id = null;
            String author = null;
            String pub_date = null;
            String pval = null;
            String gwastrait = null;
            String efotrait = null;
            String efouri = null;

            IRI iri = IRI.create(associationURI);
            OWLNamedIndividual association = df.getOWLNamedIndividual(iri);
            getLog().debug("Got the OWL individual " + association);

            //get the SNP and the trait
            OWLObjectProperty has_subject = df.getOWLObjectProperty(IRI.create(OntologyConstants.HAS_SUBJECT_IRI));
            Set<OWLIndividual> related = association.getObjectPropertyValues(has_subject, ontology);

            IRI snp_class = IRI.create(OntologyConstants.SNP_CLASS_IRI);
            OWLNamedIndividual snp = null;
            OWLNamedIndividual trait = null;

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


            if (trait != null) {
                Set<OWLClassExpression> allTypes = trait.getTypes(ontology);
                for (OWLClassExpression expr : allTypes) {
                    OWLClass typeClass = expr.asOWLClass();
                    IRI typeIRI = typeClass.getIRI();
                    efouri = typeIRI.toString();
                    initEFOLabelMap();
                    efotrait = ontologyLabelsMap.get(typeIRI);

                    getLog().debug("The EFO label and URI are " + efotrait + " and " + efouri);

                }
            }


            OWLDataProperty has_name =
                    df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_GWAS_TRAIT_NAME_PROPERTY_IRI));

            if (association.getDataPropertyValues(has_name, ontology).size() != 0) {
                OWLLiteral name = association.getDataPropertyValues(has_name, ontology).iterator().next();
                gwastrait = name.getLiteral();
                getLog().debug("The GWAS trait for this association is " + gwastrait);
            }

//get the pvalue
            OWLDataProperty has_pval = df.getOWLDataProperty((IRI.create(OntologyConstants.HAS_P_VALUE_PROPERTY_IRI)));
            if (association.getDataPropertyValues(has_pval, ontology).size() != 0) {
                OWLLiteral p = association.getDataPropertyValues(has_pval, ontology).iterator().next();
                pval = p.getLiteral();
                getLog().debug("The p-value for this association is " + pval);
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

                if (summary.getChromBand() == null) {
                    OWLObjectProperty located_in =
                            df.getOWLObjectProperty(IRI.create(OntologyConstants.LOCATED_IN_PROPERTY_IRI));
                    Set<OWLIndividual> bands = snp.getObjectPropertyValues(located_in, ontology);

                    for (OWLIndividual ind : bands) {
                        OWLNamedIndividual band = (OWLNamedIndividual) ind;
                        OWLDataProperty band_name =
                                df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_NAME_PROPERTY_IRI));

                        if (band.getDataPropertyValues(band_name, ontology).size() != 0) {
                            OWLLiteral name = band.getDataPropertyValues(band_name, ontology).iterator().next();
                            summary.setChromBand(name.getLiteral());
                            getLog().debug("The chromosomal band is " + name.getLiteral());
                        }
                    }

                }
            }


//get the Pubmed ID of the study
            OWLObjectProperty part_of = df.getOWLObjectProperty(IRI.create(OntologyConstants.PART_OF_PROPERTY_IRI));
            Set<OWLIndividual> studies = association.getObjectPropertyValues(part_of, ontology);
            OWLDataProperty has_pmid = df.getOWLDataProperty(IRI.create(OntologyConstants.HAS_PUBMED_ID_PROPERTY_IRI));
            OWLDataProperty has_author =
                    df.getOWLDataProperty((IRI.create((OntologyConstants.HAS_AUTHOR_PROPERTY_IRI))));
            OWLDataProperty has_pubdate =
                    df.getOWLDataProperty((IRI.create(OntologyConstants.HAS_PUBLICATION_DATE_PROPERTY_IRI)));

            for (OWLIndividual study : studies) {
                Set<OWLLiteral> pmids = study.getDataPropertyValues(has_pmid, ontology);
                for (OWLLiteral id : pmids) {
                    pm_id = id.getLiteral();
                    getLog().debug("The Pubmed id is " + pm_id);

                }

                Set<OWLLiteral> authors = study.getDataPropertyValues(has_author, ontology);
                for (OWLLiteral a : authors) {
                    author = a.getLiteral();
                    getLog().debug("The author is " + author);

                }

                Set<OWLLiteral> dates = study.getDataPropertyValues(has_pubdate, ontology);
                for (OWLLiteral date : dates) {
                    pub_date = date.getLiteral();
                    pub_date = pub_date.substring(0, 4);
                    getLog().debug("The publication date is " + pub_date);

                }

            }

            summary.addSNP(pm_id, author, pub_date, rs_id, pval, gwastrait, efotrait, efouri);

        }

        return summary;

    }

    public boolean checkType(OWLNamedIndividual individual, OWLOntology ontology, IRI typeIRI) {
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

    private void initEFOLabelMap() {
        if (ontologyLabelsMap == null) {
            ontologyLabelsMap = new HashMap<IRI, String>();
            try {
                getLog().debug("Trying to load EFO");
                OWLOntology efo = ontologyConfiguration.getOWLOntologyManager()
                        .loadOntology(IRI.create(OntologyConstants.EFO_ONTOLOGY_SCHEMA_IRI));
                getLog().debug(("Successfully loaded EFO"));
                Set<OWLClass> allClasses = efo.getClassesInSignature();

                OWLAnnotationProperty label = ontologyConfiguration.getOWLDataFactory().getOWLAnnotationProperty(
                        OWLRDFVocabulary.RDFS_LABEL.getIRI());

                for (OWLClass efoClass : allClasses) {
                    IRI clsIri = efoClass.getIRI();
                    String className = null;

                    for (OWLAnnotation annotation : efoClass.getAnnotations(efo, label)) {
                        if (annotation.getValue() instanceof OWLLiteral) {
                            OWLLiteral val = (OWLLiteral) annotation.getValue();
                            className = val.getLiteral();
                        }
                        if (efoClass.getAnnotations(efo, label).size() != 1) {
                            getLog().debug("More than one label for class " + className);
                        }
                    }

                    if (className == null) {
                        getLog().debug("Class without label " + efoClass);
                    }
                    else {
                        ontologyLabelsMap.put(clsIri, className);
                    }
                }
            }
            catch (OWLOntologyCreationException e) {
                getLog().error("Could not load EFO " + e);
            }
        }
    }
}
