package uk.ac.ebi.spot.goci.model;

import org.springframework.data.rest.core.config.Projection;

import java.util.Collection;
import java.util.Date;

/**
 * Created by Dani on 13/10/16.
 */

@Projection(name = "publicStudy", types = { Study.class })
public interface PublicStudy {

    String getAuthor();

    Date getPublicationDate();

    String getPublication();

    String getTitle();

    String getInitialSampleSize();

    String getReplicateSampleSize();

    Collection<Platform> getPlatforms();

    String getPubmedId();

//    Boolean getCnv() {
//        return cnv;
//    }
//
//
//    Boolean getGxe() {
//        return gxe;
//    }
//
//
//    Boolean getGxg() {
//        return gxg;
//    }
//
//
//    Boolean getTargetedArray() {
//        return targetedArray;
//    }
//
//
//    Collection<Association> getAssociations() {
//        return associations;
//    }
//
//
//    DiseaseTrait getDiseaseTrait() {
//        return diseaseTrait;
//    }
//
//
//    Collection<EfoTrait> getEfoTraits() {
//        return efoTraits;
//    }

//
//    Collection<Ethnicity> getEthnicities() {
//        return ethnicities;
//    }
//
//
//
//    Integer getSnpCount() {
//        return snpCount;
//    }
//
//
//    String getQualifier() {
//        return qualifier;
//    }
//
//
//    boolean getImputed() {
//        return imputed;
//    }
//
//
//    boolean getPooled() {
//        return pooled;
//    }
//
//
//    String getStudyDesignComment() {
//        return studyDesignComment;
//    }
//
//
//    Boolean getGenomewideArray() {
//        return genomewideArray;
//    }
//

//
//
//    String getAccessionId() {
//        return accessionId;
//    }
}
