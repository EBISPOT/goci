package uk.ac.ebi.fgpt.lode.model;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 02/05/2013
 * Functional Genomics Group EMBL-EBI
 *
 * This is an interface to an explorer view configuration
 * This configuration is used to select certain URIs for
 * inclusion and exclusion in the output service in the ExploreService
 */
public interface ExplorerViewConfiguration {


    /**
     * Get top relationships, these are the most important relationships to display
     * @return List<URI> Top URIs to display in order
     */
    List<URI> getTopRelationships();

    /**
     * Always ignore these predicates when generating a view
     * @return List<URI> Predicates to ignore
     */
    Set<URI> getIgnoreRelationships();

    /**
     * Always ignore these classes when generating a view
     * @return List<URI> Classes to ignore
     */
    Set<URI> getIgnoreTypes();

    /**
     * Return blank nodes in the view
     * @return boolean ignore blank nodes
     */
    boolean ignoreBlankNodes();

    /**
     * When getting a set of related resources, set a limit on how may should be returned
     * @return int number of objects to return for each predicate
     */
    int getObjectMaxSample();

    /**
     * Get the predicates used for resource labels
     * @return Set<URI> Collection of predicate URIs for labels to use
     */
    Set<URI> getLabelRelations();

    /**
     * Get the predicates used for resource descriptions
     * @return Set<URI> Collection of predicate URIs for descriptions
     */
    Set<URI> getDescriptionRelations();

    /**
     * URI for predicate that links to a depiction of this resource
     * The object should be a URL for an image that resolves.
     * @return predicate URI related a depiction of the current resource
     */
    URI getDepictRelation();
}
