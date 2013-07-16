package uk.ac.ebi.fgpt.lode.service;

import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Simon Jupp
 * @date 21/02/2013
 * Functional Genomics Group EMBL-EBI
 */
public interface ExploreService {


    /**
     * Query for all related resources by a given set of property URIs.
     * @param resourceUri the requested resource
     * @param propertyUris set of URIs to get relations from
     * @param excludeTypes set of URI types to ignore
     * @param ignoreBnodes include bnodes in results
     * @throws LodeException
     */
    Collection<RelatedResourceDescription> getRelatedResourceByProperty (URI resourceUri, Set<URI> propertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException;

    /**
     *
     * For a given resource get a collection of related RelatedResourceDescription.
     * Optional properties or types to exclude can be supplied
     * @param resourceUri the requested resource
     * @param excludePropertyUris set of properties to exclude
     * @param excludeTypes set of URI types to ignore
     * @param ignoreBnodes include bnodes in results
     * @return Collection<RelatedResourceDescription>
     * @throws LodeException
     */
    Collection<RelatedResourceDescription> getRelatedToObjects (URI resourceUri, Set<URI> excludePropertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException;

    /**
     *
     * For a given resource get a collection of related RelatedResourceDescription objects.
     *
     * @param resourceUri the requested resource
     * @param excludePropertyUris set of properties to exclude
     * @param excludeTypes set of URI types to ignore
     * @param ignoreBnodes include bnodes in results
     * @return Collection<RelatedResourceDescription>
     * @throws LodeException
     */
    Collection<RelatedResourceDescription> getRelatedFromSubjects (URI resourceUri, Set<URI> excludePropertyUris, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException;


    /**
     *
     * For a given resource get the type
     *
     * @param resourceUri the requested resource
     * @param excludeTypes set of URI types to ignore
     * @param ignoreBnodes include bnodes in results
     * @return Collection<RelatedResourceDescription>
     * @throws LodeException
     */
    Collection<RelatedResourceDescription> getTypes (URI resourceUri, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException;


    /**
     *
     * For a given resource get all the types (transitive)
     *
     * @param resourceUri the requested resource
     * @param excludeTypes set of URI types to ignore
     * @param ignoreBnodes include bnodes in results
     * @return Collection<RelatedResourceDescription>
     * @throws LodeException
     */
    Collection<RelatedResourceDescription> getAllTypes (URI resourceUri, Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException;

    /**
     *
     * Get a bare minimum information about a resource (uri, label, description, dataset)
     *
     * @param resourceUri the requested resource
     * @return Collection<ShortResourceDescription>
     * @throws LodeException
     */
    ShortResourceDescription getShortResourceDescription (URI resourceUri, Set<URI> labelUris, Set<URI> descriptionUris) throws LodeException;

    /**
     *
     * Get a URL that depicts this resource
     *
     * @param uri the requested resource
     * @param depictRelation uri to identfy pictures
     * @return Collection<DepictionBean>
     * @throws LodeException
     */

    Collection<String> getResourceDepiction(URI uri, URI depictRelation);

}
