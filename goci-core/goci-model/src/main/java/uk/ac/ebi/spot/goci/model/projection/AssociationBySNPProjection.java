package uk.ac.ebi.spot.goci.model.projection;

import org.springframework.data.rest.core.config.Projection;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.model.Gene;
import uk.ac.ebi.spot.goci.model.Locus;
import uk.ac.ebi.spot.goci.model.Study;

import java.util.Collection;

/**
 * Created by dwelter on 08/09/17.
 */
@Projection(name = "associationBySnp", types = {Association.class})
public interface AssociationBySNPProjection {
    
      String getRiskFrequency();
      String getPvalueDescription();
      Integer getPvalueMantissa();
      Integer getPvalueExponent();
      Boolean getMultiSnpHaplotype();
      Boolean getSnpInteraction();
      String getSnpType();
      Float getStandardError();
      String getRange();

      String getDescription();
      Float getOrPerCopyNum();
      Float getBetaNum();
      String getBetaUnit();
      String getBetaDirection();

      Study getStudy();

      Collection<Locus> getLoci();

      Collection<EfoTrait> getEfoTraits();

      Collection<Gene> getGenes();

      
}
