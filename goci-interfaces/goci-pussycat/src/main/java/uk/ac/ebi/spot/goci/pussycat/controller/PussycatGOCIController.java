package uk.ac.ebi.spot.goci.pussycat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.pussycat.exception.NoRenderableDataException;
import uk.ac.ebi.spot.goci.pussycat.exception.PussycatSessionNotReadyException;
import uk.ac.ebi.spot.goci.pussycat.lang.Filter;
import uk.ac.ebi.spot.goci.pussycat.manager.PussycatManager;
import uk.ac.ebi.spot.goci.pussycat.renderlet.RenderletNexus;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSession;
import uk.ac.ebi.spot.goci.pussycat.session.PussycatSessionStrategy;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.spot.goci.pussycat.lang.Filtering.*;

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

    @RequestMapping
    public @ResponseBody String testBasicService(HttpSession session) throws PussycatSessionNotReadyException {
        return "Welcome to the Pussycat server! Your session is " + session.getId()
                + " and your Pussycat session is " +         getPussycatSession(session).getSessionID();
    }

    @RequestMapping(value = "/clear")
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
    public @ResponseBody String renderGWASDiagram(HttpSession session) throws PussycatSessionNotReadyException, NoRenderableDataException {
        // render all data using the pussycat session for this http session
        return getPussycatSession(session).performRendering(getRenderletNexus(session));
    }

    @RequestMapping(value = "/gwasdiagram/associations")
    public @ResponseBody String renderAssociations(@RequestParam(value = "pvaluemin", required = false) String pvalueMin,
                                                   @RequestParam(value = "pvaluemax", required = false) String pvalueMax,
                                                   @RequestParam(value = "datemin", required = false) String dateMin,
                                                   @RequestParam(value = "datemax", required = false) String dateMax,
                                                   HttpSession session)
            throws PussycatSessionNotReadyException, NoRenderableDataException {

        getLog().debug("Received a new rendering request - " +
                "putting together the query from date '" + dateMin + "' to '" + dateMax + "' and from pvalue '" + pvalueMin + "' to '" + pvalueMax + "'");

        if(pvalueMin == ""){
            pvalueMin = null;
        }
        if(pvalueMax == ""){
            pvalueMax = null;
        }
        if(dateMin == ""){
            dateMin = null;
        }
        if(dateMax == ""){
            dateMax = null;
        }
        Filter pvalueFilter = null;
        Filter dateFilter = null;

        if(pvalueMin != null || pvalueMax != null){
            pvalueFilter = setPvalueFilter(pvalueMin, pvalueMax);
            getRenderletNexus(session).setRenderingContext(pvalueFilter);
        }

        if(dateMin != null || dateMax != null){
            dateFilter = setDateFilter(dateMin, dateMax);
            getRenderletNexus(session).setRenderingContext(dateFilter);
        }


        if(dateFilter == null && pvalueFilter == null){
            return getPussycatSession(session).performRendering(getRenderletNexus(session));
        }
        else if(dateFilter == null && pvalueFilter != null){
            return getPussycatSession(session).performRendering(getRenderletNexus(session), pvalueFilter);

        }
        else if(pvalueFilter == null && dateFilter != null){
            return getPussycatSession(session).performRendering(getRenderletNexus(session), dateFilter);

        }
        else {
            return getPussycatSession(session).performRendering(getRenderletNexus(session), dateFilter, pvalueFilter);
        }
    }

    private Filter setDateFilter(String dateMin, String dateMax) {
        Filter dateFilter;

        DateFormat df_input = new SimpleDateFormat("yyyy-MM-dd");

        Date to, from;

        try {

             if(dateMax != null){
                int endYearVar = Integer.parseInt(dateMax.split("-")[0]);
                int endMonthVar = Integer.parseInt(dateMax.split("-")[1]);

                String end_month, end_year;

     //API call provides date for "up to and including the end of" - must increment month for query
                 if (endMonthVar == 12) {
                    end_month = "01";
                    endYearVar++;
                    end_year = Integer.toString(endYearVar);
                }
                else {
                    endMonthVar++;
                    if (endMonthVar > 9) {
                        end_month = Integer.toString(endMonthVar);
                    }
                    else {
                        end_month = "0".concat(Integer.toString(endMonthVar));
                    }
                end_year = Integer.toString(endYearVar);
                }

                to = df_input.parse(end_year + "-" + end_month + "-01");
            }
            else{
                //change to use today's date
                to = df_input.parse(df_input.format(new Date()));
            }

            if(dateMin != null){
                from = df_input.parse(dateMin + "-01");
            }
            else{
                from = df_input.parse("2005-01-01");
            }


            Calendar fromValue = Calendar.getInstance();
            fromValue.setTime(from);

            Calendar toValue = Calendar.getInstance();
            toValue.setTime(to);

            Study study = template(Study.class);
            dateFilter = refine(study).on(study.getPublicationDate()).hasRange(fromValue, toValue);

        }
        catch (ParseException e) {
            getLog().error("Bad date in URL for date range '" + dateMin + "' to '" + dateMax, e);
            throw new RuntimeException("Bad date in URL for date range '" + dateMin + "' to '" + dateMax, e);
        }
        return dateFilter;
    }

    private Filter setPvalueFilter(String pvalueMin, String pvalueMax) {

        double start_pvalue, end_pvalue;
        if(pvalueMin != null) {
            int startMantissaNum = Integer.parseInt(pvalueMin.split("e")[0]);
            int startExponentNum = Integer.parseInt(pvalueMin.split("e")[1]);
            start_pvalue = startMantissaNum * Math.pow(10, startExponentNum);
        }
        else{
           start_pvalue = 0.0;
        }

        if(pvalueMax != null) {
            int endMantissaNum = Integer.parseInt(pvalueMax.split("e")[0]);
            int endExponentNum = Integer.parseInt(pvalueMax.split("e")[1]);
            end_pvalue = endMantissaNum * Math.pow(10, endExponentNum);
        }
        else {
            end_pvalue = 1*Math.pow(10,-5);
        }


        Association association = template(Association.class);
        Filter filter = refine(association).on(association.getPvalue()).hasValues(start_pvalue, end_pvalue);

        return filter;
    }


    @RequestMapping(value = "/snps")
    public @ResponseBody String renderSNPs(HttpSession session) throws PussycatSessionNotReadyException, NoRenderableDataException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(snp));
    }

    @RequestMapping(value = "/snps/{rsID}")
    public @ResponseBody String renderSNP(@PathVariable String rsID, HttpSession session)
            throws PussycatSessionNotReadyException, NoRenderableDataException {
        SingleNucleotidePolymorphism snp = template(SingleNucleotidePolymorphism.class);
        Filter filter = refine(snp).on(snp.getRsId()).hasValue(rsID);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter);
    }

    @RequestMapping(value = "/associations")
    public @ResponseBody String renderAllAssociations(HttpSession session) throws PussycatSessionNotReadyException, NoRenderableDataException {
        Association ta = template(Association.class);
        return getPussycatSession(session).performRendering(getRenderletNexus(session), filter(ta));
    }

    @RequestMapping(value = "/traits/{efoURI}")
    public @ResponseBody String renderTrait(@PathVariable String efoURI, HttpSession session)
            throws PussycatSessionNotReadyException, NoRenderableDataException {
        Association ta = template(Association.class);
        Filter filter = refine(ta).on(ta.getEfoTraits()).hasValue(URI.create(efoURI));
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

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(NoRenderableDataException.class)
    public @ResponseBody String handleNoRenderableDataException(NoRenderableDataException e) {
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
