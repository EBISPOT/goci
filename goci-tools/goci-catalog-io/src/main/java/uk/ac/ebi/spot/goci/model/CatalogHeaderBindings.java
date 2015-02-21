package uk.ac.ebi.spot.goci.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
public class CatalogHeaderBindings {
    private static List<CatalogHeaderBinding> ncbiHeaders = new ArrayList<>();
    private static List<CatalogHeaderBinding> downloadHeaders = new ArrayList<>();

    static {
        for (CatalogHeaderBinding catalogHeaderBinding : CatalogHeaderBinding.values()) {
            if (catalogHeaderBinding.requiredByNcbi()) {
                ncbiHeaders.add(catalogHeaderBinding);
            }
            if (catalogHeaderBinding.requiredByDownload()) {
                downloadHeaders.add(catalogHeaderBinding);
            }
        }
    }

    public static List<CatalogHeaderBinding> getNcbiHeaders() {
        return ncbiHeaders;
    }

    public static List<CatalogHeaderBinding> getDownloadHeaders() {
        return downloadHeaders;
    }
}
