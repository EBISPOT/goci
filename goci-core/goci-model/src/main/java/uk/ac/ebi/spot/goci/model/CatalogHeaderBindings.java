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
    private static List<CatalogHeaderBinding> loadHeaders = new ArrayList<>();

    static {
        for (CatalogHeaderBinding catalogHeaderBinding : CatalogHeaderBinding.values()) {
            if (catalogHeaderBinding.getNcbiInclusion().isRequired()) {
                ncbiHeaders.add(catalogHeaderBinding);
            }
            if (catalogHeaderBinding.getDownloadInclusion().isRequired()) {
                downloadHeaders.add(catalogHeaderBinding);
            }
            if (catalogHeaderBinding.getLoadInclusion().isRequired()) {
                loadHeaders.add(catalogHeaderBinding);
            }
        }
    }

    public static List<CatalogHeaderBinding> getNcbiHeaders() {
        return ncbiHeaders;
    }

    public static List<CatalogHeaderBinding> getDownloadHeaders() {
        return downloadHeaders;
    }

    public static List<CatalogHeaderBinding> getLoadHeaders() {
        return loadHeaders;
    }
}
