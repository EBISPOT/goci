/*
 * Copyright (c) 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package uk.ac.ebi.fgpt.lode.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ExplorerViewConfiguration;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;
import uk.ac.ebi.fgpt.lode.service.SparqlService;
import uk.ac.ebi.fgpt.lode.view.DepictionBean;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 26/04/2013
 * Functional Genomics Group EMBL-EBI
 */
@Controller
@RequestMapping("/explore")
public class ExplorerServlet {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ExplorerViewConfiguration configuration;
    private ExploreService service;
    private SparqlService sparqlService;

    public SparqlService getSparqlService() {
        return sparqlService;
    }

    @Autowired
    public void setSparqlService(SparqlService sparqlService) {
        this.sparqlService = sparqlService;
    }

    public ExploreService getService() {
        return service;
    }

    @Autowired
    public void setService(ExploreService service) {
        this.service = service;
    }

    public ExplorerViewConfiguration getConfiguration() {
        return configuration;
    }

    @Autowired
    public void setConfiguration(ExplorerViewConfiguration configuration) {
        this.configuration = configuration;
    }

    protected Logger getLog() {
        return log;
    }

    @RequestMapping (produces="application/rdf+xml")
    public @ResponseBody
    void describeResourceAsXml (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        if (uri != null && uri.length() > 0) {
            String query = "DESCRIBE <" + URI.create(uri) + ">";
            response.setContentType("application/rdf+xml");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "RDF/XML", false, out);
            out.close();
        }
        else {

        }
    }

    @RequestMapping (produces="application/rdf+n3")
    public @ResponseBody
    void describeResourceAsN3 (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        if (uri != null && uri.length() > 0) {
            String query = "DESCRIBE <" + URI.create(uri) + ">";
            log.trace("querying for graph rdf+n3");
            response.setContentType("application/rdf+n3");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "N3", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping (produces="application/rdf+json")
    public @ResponseBody
    void describeResourceAsJson (
            @RequestParam(value = "uri", required = true ) String uri,
            HttpServletResponse response) throws IOException, LodeException {
        if (uri != null && uri.length() > 0) {
            String query = "DESCRIBE <" + URI.create(uri) + ">";
            log.trace("querying for graph rdf+n3");
            response.setContentType("application/rdf+json");
            ServletOutputStream out = response.getOutputStream();
            out.println();
            out.println();
            getSparqlService().query(query, "JSON-LD", false, out);
            out.close();
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    public @ResponseBody
    void describeResource (
            @RequestParam(value = "uri", required = true ) String uri,
            @RequestParam(value = "format", required = false ) String format,
            HttpServletResponse response) throws IOException, LodeException {
        if (uri != null && uri.length() > 0) {
            if (format.toLowerCase().equals("n3")) {
                describeResourceAsN3(uri, response);
            }
            else if (format.toLowerCase().equals("json-ld")) {
                describeResourceAsJson(uri, response);
            }
            else {
                describeResourceAsXml(uri, response);
            }
        }
        else {
            handleBadUriException(new Exception("Malformed or empty URI request: " + uri));
        }
    }

    @RequestMapping(value = "/resourceTypes", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getTypesWithLabelsAndDescription(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        if (uri != null && uri.length() > 0) {
            return getService().getTypes(
                    URI.create(uri),
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes()
                    );
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/resourceAllTypes", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getAllTypesWithLabelsAndDescription(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        if (uri != null && uri.length() > 0) {
            return getService().getAllTypes(
                    URI.create(uri),
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes()
                    );
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/relatedToObjects", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getRelatedToObjects(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {
        if (uri != null && uri.length() > 0) {
            // get the relationships to ignore
            Set<URI> ignoreProps = getConfiguration().getIgnoreRelationships();
            ignoreProps.addAll(getConfiguration().getTopRelationships());

            return getService().getRelatedToObjects(
                    URI.create(uri),
                    ignoreProps,
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = "/resourceShortDescription", method = RequestMethod.GET)
    public @ResponseBody ShortResourceDescription getShortResourceDescritption(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().trace("Getting short description for: " + uri);

        if (uri != null && uri.length() > 0) {
            return getService().getShortResourceDescription(
                    URI.create(uri),
                    getConfiguration().getLabelRelations(),
                    getConfiguration().getDescriptionRelations()
            );
        }
        else {
            return new ShortResourceDescription(uri, uri, null, null);
        }
    }

    @RequestMapping(value = "/resourceDepictions", method = RequestMethod.GET)
    public @ResponseBody
    Collection<DepictionBean> getShortResourceDepiction(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().trace("Getting image urls for: " + uri);
        Set<DepictionBean> dps= new HashSet<DepictionBean>();
        if (uri != null && uri.length() > 0) {

            for (String u :  getService().getResourceDepiction(
                    URI.create(uri),
                    getConfiguration().getDepictRelation())) {
                dps.add(new DepictionBean(u));
            }
            return dps;
        }
        else {
            return Collections.emptySet();
        }
    }

    @RequestMapping(value = "/resourceTopObjects", method = RequestMethod.GET)
    public @ResponseBody Collection<RelatedResourceDescription> getTopRelatedResourceByProperty(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {

        getLog().trace("Getting top objects for: " + uri);

        if (uri != null && uri.length() > 0) {
            Set<URI> toprelations = new LinkedHashSet<URI>(getConfiguration().getTopRelationships());
            return getService().getRelatedResourceByProperty(
                    URI.create(uri),
                    toprelations,
                           getConfiguration().getIgnoreTypes(),
                            getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }


    @RequestMapping("/relatedFromSubjects")
    public @ResponseBody Collection<RelatedResourceDescription> getRelatedFromSubjects(
            @RequestParam(value = "uri", required = true ) String uri) throws LodeException {
        if (uri != null && uri.length() > 0) {
            return getService().getRelatedFromSubjects(
                    URI.create(uri),
                    new HashSet<URI>(),
                    getConfiguration().getIgnoreTypes(),
                    getConfiguration().ignoreBlankNodes());
        }
        else {
            return Collections.emptyList();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleBadUriException(Exception e)  {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LodeException.class)
    public @ResponseBody String handleLodException(LodeException e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IOException.class)
    public @ResponseBody String handleIOException(IOException e) {
        getLog().error(e.getMessage(), e);
        return e.getMessage();
    }
}
