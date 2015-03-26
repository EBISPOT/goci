//package uk.ac.ebi.spot.goci.repository;
//
//import org.springframework.stereotype.Component;
//import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
///**
// * Javadocs go here!
// *
// * @author Tony Burdett
// * @date 24/02/15
// */
//@Component
//public class DownloadIntergenicMapper implements CatalogDataMapper {
//    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
//        return Collections.singletonList(CatalogHeaderBinding.IS_INTERGENIC);
//    }
//
//    @Override public CatalogHeaderBinding getOutputField() {
//        return CatalogHeaderBinding.DOWNLOAD_INTERGENIC;
//    }
//
//    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
//        return databaseValues.get(CatalogHeaderBinding.IS_INTERGENIC).equals("0") ? "0" : "1";    }
//}
