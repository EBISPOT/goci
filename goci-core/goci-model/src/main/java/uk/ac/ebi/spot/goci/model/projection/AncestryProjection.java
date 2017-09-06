package uk.ac.ebi.spot.goci.model.projection;

import org.springframework.data.rest.core.config.Projection;
import uk.ac.ebi.spot.goci.model.AncestralGroup;
import uk.ac.ebi.spot.goci.model.Ancestry;
import uk.ac.ebi.spot.goci.model.Country;

import java.util.Collection;

/**
 * Created by dwelter on 24/08/17.
 */

@Projection(name = "ancestry", types = {Ancestry.class})
public interface AncestryProjection {

    String getType();
    Integer getNumberOfIndividuals();
    String getDescription();
//    Study getStudy();
    Collection<Country> getCountryOfOrigin();
    Collection<Country> getCountryOfRecruitment();
    Collection<AncestralGroup> getAncestralGroups();
}
