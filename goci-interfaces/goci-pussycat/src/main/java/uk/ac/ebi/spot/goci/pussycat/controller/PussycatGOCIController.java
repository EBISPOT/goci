package uk.ac.ebi.spot.goci.pussycat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ac.ebi.spot.goci.lang.Filter;
import uk.ac.ebi.spot.goci.lang.OntologyConstants;
import uk.ac.ebi.spot.goci.ui.model.AssociationSummary;
import uk.ac.ebi.spot.goci.ui.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.ui.model.Study;
import uk.ac.ebi.spot.goci.ui.model.TraitAssociation;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import static uk.ac.ebi.spot.goci.lang.Filtering.filter;
import static uk.ac.ebi.spot.goci.lang.Filtering.refine;
import static uk.ac.ebi.spot.goci.lang.Filtering.template;

/**
 * A MVC controller for Pussycat.  This controller can be used to create a new session, load ontology data and create
 * the SVG canvas Pussycat will display.
 *
 * @author Tony Burdett Date 27/02/12
 */
@Controller
public class PussycatGOCIController {
    private PussycatSessionStrategy sessionStrategy;
    private PussycatManager pussycatManager;

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

    @RequestMapping(params = "clear")
    public @ResponseBody boolean clearRendering(HttpSession session) throws PussycatSessionNotReadyException {
        try {
            getRenderletNexus(session).reset();
            return true;
        }
        catch (PussycatSessionNotReadyException e) {
            getLog().error("Attempting to clear a renderlet nexus with no bound pussycat session, nothing happened");
            return false;
        }
    }

    @RequestMapping(value = "/gwasdiagram")
    public @ResponseBody String renderGWASDiagram(HttpSession session) throws PussycatSessionNotReadyException {
        // render all data using the pussycat session for this http session
        return getPussycatSession(session).performRendering(getRenderletNexus(session));
    }

    @RequestMapping(value = "/gwasdiagram/timeseries/{year}/{month}")
    public @ResponseBody String renderGWASDiagramTimeSeries(@PathVariable String year,
                                                            @PathVariable String month,
                                                            HttpSession session)
            throws PussycatSessionNotReadyException {
        // get the subset of studies published before the supplied date
        /*trait association'  and part_of some ('GWAS study' and has_publication_date some dateTime[< "  "^^dateTime])*/
        getLog().debug("Received a new rendering request - " +
                               "putting together the query for year '" + year + "' and month '" + month + "'");

        int monthVar = Integer.parseInt(month);
        int yearVar = Integer.parseInt(year);

        //API call provides date for "up to and including the end of" - must increment month for query
        if (monthVar == 12) {
            month = "01";
            yearVar++;
            year = Integer.toString(yearVar);
        }
        else {
            monthVar++;
            if (monthVar > 9) {
                month = Integer.toString(monthVar);
            }
            else {
                month = "0".concat(Integer.toString(monthVar));
            }
        }

        try {
            DateFormat df = new SimpleDateFormat("YYYY/MM");
            Date from = df.parse("2005/01");
            Date to = df.parse(year + "/" + month);

            Study study = template(Study.class);
            Filter filter = refine(study).on(study.getPublishedDate()).hasRange(from, to);
            return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
        }
        catch (ParseException e) {
            getLog().error("Bad date in URL /gwasdiagram/timeseries/" + year + "/" + month + " - " +
                                   "use /gwasdiagram/timeseries/YYYY/MM", e);
            throw new RuntimeException("Bad date in URL /gwasdiagram/timeseries/" + year + "/" + month + " - " +
                                               "use /gwasdiagram/timeseries/YYYY/MM", e);
        }
    }

    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) throws PussycatSessionNotReadyException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(snp));
    }

    @RequestMapping(value = "/snps/{rsID}")
    public @ResponseBody String renderSNP(@PathVariable String rsID, HttpSession session)
            throws PussycatSessionNotReadyException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        Filter filter = refine(snp).on(snp.getRSID()).hasValue(rsID);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAssociations(HttpSession session) throws PussycatSessionNotReadyException {
        TraitAssociation ta = template(TraitAssociation.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(ta));
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException {
        TraitAssociation ta = template(TraitAssociation.class);
        Filter filter = refine(ta).on(ta.getAssociatedTrait()).hasValue(URI.create(efoURI));
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
    }

    /**
     * This method returns all the children of the provided URI in order to allow filtering based on URIs
     *
     * @param traitName the name of the trait to lookup
     * @param session   the http session in which to perform this request
     * @return a list of URIs, appropriately "shortformed", that describe the classes that are relevant for this trait
     * name
     * @throws PussycatSessionNotReadyException
     */
    @RequestMapping(value = "/filter/{traitName}")
    public @ResponseBody Set<String> getRelatedTraits(@PathVariable String traitName, HttpSession session)
            throws PussycatSessionNotReadyException {
        Set<URI> uris = getPussycatSession(session).getRelatedTraits(traitName);
        Set<String> results = new HashSet<String>();
        for (URI uri : uris) {
            // process URI to just keep term name
            String typeStr = uri.toString();
            typeStr = typeStr.substring(typeStr.lastIndexOf("/") + 1, typeStr.length());
            typeStr = typeStr.contains("#") ? typeStr.substring(typeStr.lastIndexOf("#") + 1,
                                                               typeStr.length()) : typeStr;
            results.add(typeStr);
        }
        return results;
    }

    @RequestMapping(value = "/associations/{associationIds}")
    public @ResponseBody List<AssociationSummary> getAssociationSummaries(
            @PathVariable String associationIds, HttpSession session)
            throws PussycatSessionNotReadyException {
        getLog().debug("Received request to display information for associations " + associationIds);

        List<URI> uris = new ArrayList<URI>();
        if (associationIds.contains(",")) {
            StringTokenizer tokenizer = new StringTokenizer(associationIds, ",");
            while (tokenizer.hasMoreTokens()) {
                String next = tokenizer.nextToken();
                URI nextURI = URI.create(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI + "/TraitAssociation/" + next);
                uris.add(nextURI);
            }
        }
        else {
            URI uri = URI.create(OntologyConstants.GWAS_ONTOLOGY_BASE_IRI + "/TraitAssociation/" + associationIds);
            uris.add(uri);
        }

        getLog().debug("This trait represents " + uris.size() + " different associations");

        return getPussycatSession(session).getAssociationSummaries(uris);
    }


    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(PussycatSessionNotReadyException.class)
    public @ResponseBody String handlePussycatSessionNotReadyException(PussycatSessionNotReadyException e) {
        String responseMsg = "Please wait while Pussycat starts up!<br/>" + e.getMessage();
        getLog().error(responseMsg, e);
        return responseMsg;
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(RuntimeException.class)
    public @ResponseBody String handleRuntimeException(RuntimeException e) {
        String responseMsg = "There has been a problem generating the GWAS diagram.  " +
                "We've been notified, and are working to fix this problem as soon as we can.<br/>" + e.getMessage();
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

    protected RenderletNexus getRenderletNexus(HttpSession session) throws PussycatSessionNotReadyException {
        getLog().debug("Attempting to obtain RenderletNexus session for HttpSession '" + session.getId() + "'");

        RenderletNexus renderletNexus;
        if (getPussycatManager().hasAvailableRenderletNexus(session)) {
            getLog().debug("RenderletNexus available for HttpSession '" + session.getId() + "'");
            renderletNexus = getPussycatManager().getRenderletNexus(session);
        }
        else {
            renderletNexus = getPussycatManager().createRenderletNexus(
                    getPussycatManager().getPussycatSession(session));
            getPussycatManager().bindRenderletNexus(session, renderletNexus);
        }

        return renderletNexus;
    }
}
