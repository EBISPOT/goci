package uk.ac.ebi.fgpt.lode.impl;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.service.JenaQueryExecutionService;
import uk.ac.ebi.fgpt.lode.model.LabeledResource;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;
import uk.ac.ebi.fgpt.lode.utils.SparqlQueryReader;

import java.net.URI;
import java.util.*;

/**
 * @author Simon Jupp
 * @date 03/05/2013
 * Functional Genomics Group EMBL-EBI
 */
public class JenaExploreService implements ExploreService {

    private Logger log = LoggerFactory.getLogger(getClass());

    // some common defaults
    private static URI label = URI.create("http://www.w3.org/2000/01/rdf-schema#label");
    private static URI type = URI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private static URI description = URI.create("http://purl.org/dc/elements/1.1/description");

    @Value("${lode.explorer.max.objects}")
    private int sampleLimit = -1;

    @Value("${lode.explorer.useInference}")
    private boolean useInference = false;

    @Value("${lode.explorer.description}")
    private String descriptionProperties;

    @Value("${lode.explorer.label}")
    private String labelProperties;

    private SparqlQueryReader queryReader;

    private JenaQueryExecutionService queryExecutionService;

    public JenaQueryExecutionService getQueryExecutionService() {
        return queryExecutionService;
    }

    public void setQueryExecutionService(JenaQueryExecutionService queryExecutionService) {
        this.queryExecutionService = queryExecutionService;
    }

    public int getSampleLimit() {
        return sampleLimit;
    }

    public void setSampleLimit(int sampleLimit) {
        this.sampleLimit = sampleLimit;
    }

    public SparqlQueryReader getQueryReader() {
        return queryReader;
    }

    public Set<URI> getDescriptionsProperties () {
        HashSet<URI> descriptionProps = new HashSet<URI>();
        if (descriptionProperties != null) {
            for (String s : descriptionProperties.split(",")) {
                descriptionProps.add(URI.create(s));
            }
        }
        else {
            descriptionProps.add(description);
        }
        return descriptionProps;
    }

    public Set<URI> getLabelProperties () {
        HashSet<URI> labelProps = new HashSet<URI>();
        if (labelProperties != null) {
            for (String s : labelProperties.split(",")) {
                labelProps.add(URI.create(s));
            }
        }
        else {
            labelProps.add(label);
        }
        return labelProps;
    }


    @Autowired
    public void setQueryReader(SparqlQueryReader queryReader) {
        this.queryReader = queryReader;
    }

    public Collection<RelatedResourceDescription> getRelatedResourceByProperty(URI resourceUri, Set<URI> propertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {
        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("RELATEDTO.PROPERTIES.QUERY");
        return getRelatedResourceByProperty(resourceUri, query, propertyUris, excludeTypes, ignoreBnodes, false);
    }

    public Collection<RelatedResourceDescription> getRelatedToObjects(URI resourceUri, Set<URI> excludePropertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {

        Set<URI> allRelatedProps = new HashSet<URI>();
        for (String uri : getRelatedProperties(resourceUri, "subject")) {
            if (!excludePropertyUris.contains(URI.create(uri))) {
                allRelatedProps.add(URI.create(uri));
            }
        }
        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("RELATEDTO.PROPERTIES.QUERY");
        return getRelatedResourceByProperty(resourceUri, query, allRelatedProps, excludeTypes, ignoreBnodes, false);

    }

    public Collection<RelatedResourceDescription> getRelatedFromSubjects(URI resourceUri, Set<URI> excludePropertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {

        Set<URI> allRelatedProps = new HashSet<URI>();
        for (String uri : getRelatedProperties(resourceUri, "object")) {
            if (!excludePropertyUris.contains(URI.create(uri))) {
                allRelatedProps.add(URI.create(uri));
            }
        }
        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("RELATEDFROM.PROPERTIES.QUERY");
        return getRelatedResourceByProperty(resourceUri, query, allRelatedProps, excludeTypes, ignoreBnodes, false);
    }

    public Collection<RelatedResourceDescription> getTypes(URI resourceUri, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {

        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("TYPES.QUERY");

        QuerySolutionMap initialBinding = new QuerySolutionMap();

        initialBinding.add("bound", new ResourceImpl(resourceUri.toString()));
        Graph g = getQueryExecutionService().getDefaultGraph();

        QueryExecution endpoint = getQueryExecutionService().getQueryExecution(g, query, initialBinding, useInference);

        RelatedResourceDescription resources = new RelatedResourceDescription();
        try {
            ResultSet results = endpoint.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();
                Resource res = solution.getResource("resource");
                if (res.isAnon()) {
                    continue;
                }
                if (excludeTypes.contains(URI.create(res.getURI()))) {
                    continue;
                }
                ShortResourceDescription description1 = getShortResourceDescription(g, URI.create(res.getURI()), getLabelProperties(), getDescriptionsProperties());

                resources.setPropertyUri(type.toString());
                resources.setPropertyUri("type");
                resources.getRelatedObjects().add(new LabeledResource(res.getURI(), description1.getDisplayLabel(), description1.getDescription()));
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint != null) {
                endpoint.close();
            }
            if (g != null ) {
                g.close();
            }
        }
        return Collections.singleton(resources);
    }

    public Collection<RelatedResourceDescription> getAllTypes(URI resourceUri, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {

        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("ALLTYPES.QUERY");

        QuerySolutionMap initialBinding = new QuerySolutionMap();

        initialBinding.add("bound", new ResourceImpl(resourceUri.toString()));
        Graph g = getQueryExecutionService().getDefaultGraph();

        QueryExecution endpoint = getQueryExecutionService().getQueryExecution(g, query, initialBinding, useInference);

        RelatedResourceDescription resources = new RelatedResourceDescription();
        try {
            ResultSet results = endpoint.execSelect();
            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();
                Resource res = solution.getResource("resource");
                if (res.isAnon()) {
                    continue;
                }
                if (excludeTypes.contains(URI.create(res.getURI()))) {
                    continue;
                }
                ShortResourceDescription description1 = getShortResourceDescription(g, URI.create(res.getURI()), getLabelProperties(), getDescriptionsProperties());

                resources.setPropertyUri(type.toString());
                resources.setPropertyUri("type");
                resources.getRelatedObjects().add(new LabeledResource(res.getURI(), description1.getDisplayLabel(), description1.getDescription()));
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint != null) {
                endpoint.close();
            }
            if (g != null ) {
                g.close();
            }
        }
        return Collections.singleton(resources);
    }

    public ShortResourceDescription getShortResourceDescription(URI resourceUri, Set<URI> labelUris, Set<URI> descriptionUris) throws LodeException {
        Graph g = getQueryExecutionService().getDefaultGraph();
        ShortResourceDescription description1 = getShortResourceDescription(g, resourceUri, labelUris, descriptionUris);
        if (g!= null) {
            g.close();
        }
        return description1;
    }

    private ShortResourceDescription getShortResourceDescription(Graph g, URI resourceUri, Set<URI> labelUris, Set<URI> descriptionUris) throws LodeException {

        // try and get a label
        String label = null;
        String description = null;
        String dataset = null;

        Query query = new Query();

        //specify the type of Sparql query SELECT
        query.setQuerySelectType();

        //variable bindings that we want to return
        query.addResultVar("label");
        query.addResultVar("description");

        Resource resourceNode = new ResourceImpl(resourceUri.toString());

        ElementUnion union = new ElementUnion();
        for (URI l : labelUris) {
            ElementTriplesBlock labelPattern = new ElementTriplesBlock();
            labelPattern.addTriple(new Triple(resourceNode.asNode(), new ResourceImpl(l.toString()).asNode(), Node.createVariable("label")));
            union.addElement(labelPattern);
        }

        for (URI d : descriptionUris) {
            ElementTriplesBlock descriptionPattern = new ElementTriplesBlock();
            descriptionPattern.addTriple(new Triple(resourceNode.asNode(), new ResourceImpl(d.toString()).asNode(), Node.createVariable("description")));
            union.addElement(descriptionPattern);
        }

        query.setQueryPattern(union);
        QueryExecution endpoint = null;

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, query, false);

            ResultSet results = endpoint.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();

                Literal labelNode = solution.getLiteral("label");
                if (label == null && labelNode !=null) {
                    label = labelNode.getLexicalForm();
                }
                Literal descriptionNode = solution.getLiteral("description");
                if (description == null && descriptionNode !=null) {
                    description = descriptionNode.getLexicalForm();
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
            }
        }
        if (label == null) {
            label = getShortForm(resourceUri);
        }
        return new ShortResourceDescription(resourceUri.toString(), label, description, dataset);
    }

    public Collection<String> getResourceDepiction(URI subject, URI depictRelation) {

        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("DEPICT.QUERY");
        QuerySolutionMap initialBinding = new QuerySolutionMap();

        initialBinding.add("subject", new ResourceImpl(subject.toString()));
        initialBinding.add("depict", new ResourceImpl(depictRelation.toString()));
        QueryExecution endpoint = null;
        Set<String> uris = new HashSet<String>();
        Graph g = getQueryExecutionService().getDefaultGraph();

        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, query, initialBinding, false);

            ResultSet results = endpoint.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();

                Resource propertyNode = solution.getResource("img");
                if (propertyNode != null) {
                    uris.add(propertyNode.getURI());
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g != null ) {
                    g.close();
                }
            }
        }
        return uris;
    }

    private Set<String> getRelatedProperties (URI resource, String binding) {

        String query = getQueryReader().getSparqlQuery("PREFIX") + "\n\n" + getQueryReader().getSparqlQuery("PROPERTIES.QUERY");

        QuerySolutionMap initialBinding = new QuerySolutionMap();

        initialBinding.add(binding, new ResourceImpl(resource.toString()));
        QueryExecution endpoint = null;
        Set<String> uris = new HashSet<String>();
        Graph g = getQueryExecutionService().getDefaultGraph();

        try {
            endpoint =  getQueryExecutionService().getQueryExecution(g, query, initialBinding, false);

            ResultSet results = endpoint.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();

                Resource propertyNode = solution.getResource("property");
                if (propertyNode != null) {
                    uris.add(propertyNode.getURI());
                }
            }

        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint !=  null)  {
                endpoint.close();
                if (g != null ) {
                    g.close();
                }
            }
        }
        return uris;
    }

    private Collection<RelatedResourceDescription> getRelatedResourceByProperty(URI resourceUri, String query, Set<URI> propertyUris, Set<URI> excludeTypes, boolean ignoreBnodes, boolean withInference) {

        Map<URI,RelatedResourceDescription> descCollection = new LinkedHashMap<URI, RelatedResourceDescription>();

        QuerySolutionMap initialBinding = new QuerySolutionMap();
        QueryExecution endpoint = null;
        Graph g = getQueryExecutionService().getDefaultGraph();

        initialBinding.add("bound", new ResourceImpl(resourceUri.toString()));
        try {
            endpoint = getQueryExecutionService().getQueryExecution(g, query, initialBinding, withInference);

            ResultSet results = endpoint.execSelect();

            while (results.hasNext()) {
                QuerySolution solution = (QuerySolution) results.next();
                String resource = null;
                String resourceLabel = null;
                String resourceType = null;
                String resourceTypeLabel = null;

                Resource propertyResource = solution.getResource("property");
                URI prop = URI.create(propertyResource.getURI());

                if (!propertyUris.contains(prop)) {
                    continue;
                }

                if (!descCollection.containsKey(prop)) {
                    descCollection.put(prop, new RelatedResourceDescription());
                    descCollection.get(prop).setPropertyUri(prop.toString());
                    // check for a property label
                    String propertyLabel = getShortForm(prop);
                    if (solution.contains("propertyLabel")) {
                        propertyLabel = solution.getLiteral("propertyLabel").getLexicalForm();
                    }
                    descCollection.put(prop, new RelatedResourceDescription());
                    descCollection.get(prop).setPropertyUri(prop.toString());
                    descCollection.get(prop).setPropertyLabel(propertyLabel);

                }
                else if (descCollection.get(prop).getRelatedObjects().size() >= getSampleLimit()) {
                    continue;
                }

                // get the related resource
                RDFNode resourceNode = solution.get("resource");

                if (resourceNode == null) {
                    log.error("no resource bound in query");
                    continue;
                }

                if (resourceNode.isAnon() && ignoreBnodes) {
                    continue;
                }
                else if (resourceNode.isLiteral()) {
                    resourceLabel = ( (Literal) resourceNode).getLexicalForm();
                }
                else if (resourceNode.isResource()) {

                    resource = ( (Resource) resourceNode).getURI();

                    if (isTypeAndExcluded(prop, resource, excludeTypes)) {
                        continue;
                    }
                    // check if resource has a label
                    resourceLabel = getShortForm(URI.create(resource));
                    if (solution.contains("resourceLabel")) {
                        resourceLabel = solution.getLiteral("resourceLabel").getLexicalForm();
                    }
                }

                // get the resource
                Resource resourceTypeNode = solution.getResource("resourceType");

                // see if the resource is typed
                if (resourceTypeNode != null) {
                    // see if this type is in the excluded set
                    if (resourceTypeNode.isAnon() && ignoreBnodes) {
                        continue;
                    }
                    if (!excludeTypes.contains(URI.create(resourceTypeNode.getURI()))) {
                        resourceType = solution.getResource("resourceType").getURI();
                        log.debug("got a resource type: " + resourceType);
                        resourceTypeLabel = getShortForm(URI.create(resourceTypeNode.getURI()));
                        if (solution.contains("resourceTypeLabel")) {
                            resourceTypeLabel = solution.getLiteral("resourceTypeLabel").getLexicalForm();
                        }
                    }
                    else {
                        log.debug("ignoring type: " + resourceTypeNode);
                    }
                }

                if (resource == null && resourceLabel == null) {
                    log.warn("resource node and label are null for: " + resourceUri.toString());
                }
                LabeledResource relatedResource = new LabeledResource(resource, resourceLabel, "");
                descCollection.get(prop).getRelatedObjects().add(relatedResource);

                if (resourceType != null) {
                    LabeledResource relatedResourceType = new LabeledResource(resourceType, resourceTypeLabel, "");
                    descCollection.get(prop).getRelatedObjectTypes().add(relatedResourceType);
                }
            }

        } catch (Exception e) {
            log.error("Error retrieving results for " + query, e);
        }
        finally {
            if (endpoint != null) {
                endpoint.close();
            }
            if (g != null ) {
                g.close();
            }
        }

        try {

            // finally get the labels for the properties
            for (URI prop : descCollection.keySet()) {
                ShortResourceDescription propertyDescription = getShortResourceDescription(prop, getLabelProperties(), Collections.<URI>emptySet());
                // see if the property has a label
                if (isNullOrEmpty(propertyDescription.getDisplayLabel())) {
                    descCollection.get(prop).setPropertyLabel(getShortForm(prop));
                }
                else {
                    descCollection.get(prop).setPropertyLabel(propertyDescription.getDisplayLabel());
                }
            }
        } catch (LodeException e) {
            log.error("Error retrieving results for " + query, e);

        }

        return descCollection.values();

    }

    private boolean isTypeAndExcluded(URI prop, String resource, Set<URI> excludeTypes) {
        return prop.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") && excludeTypes.contains(URI.create(resource));
    }


    private String getShortForm (URI uri) {
        String rendering = uri.getFragment();
        if (rendering != null && rendering.length() >0) {
            return rendering;
        }
        else {
            String s = uri.toString();
            int lastSlashIndex = s.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex != s.length() -1) {
                return s.substring(lastSlashIndex + 1);
            }
        }
        return uri.toString();
    }

    public static boolean isNullOrEmpty(Object o) {
        if (o == null) {
            return true;
        }
        return "".equals(o);
    }


}
