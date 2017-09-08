//package uk.ac.ebi.spot.goci.model.projection;
//
//import org.springframework.data.rest.core.config.Projection;
//import uk.ac.ebi.spot.goci.model.Gene;
//import uk.ac.ebi.spot.goci.model.Location;
//import uk.ac.ebi.spot.goci.model.SingleNucleotidePolymorphism;
//
//import java.util.Collection;
//import java.util.Date;
//
///**
// * Created by dwelter on 06/09/17.
// */
//@Projection(name = "snp", types = {SingleNucleotidePolymorphism.class})
//public interface SnpProjection {
//    String getRsId();
//    Long getMerged();
//    String getFunctionalClass();
//    Date getLastUpdateDate();
//    Collection<Location> getLocations();
////    Collection<GenomicContext> getGenomicContexts();
////    Collection<RiskAllele> getRiskAlleles();
//    SingleNucleotidePolymorphism getCurrentSnp();
////    Collection<Association> getAssociations();
//    Collection<Gene> getGenes();
////    Collection<Study> getStudies();
//
//}
