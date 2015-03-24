package uk.ac.ebi.spot.goci.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* Created by dwelter on 23/03/15.
*/

@Component
public class DownloadPvalLogMapper implements CatalogDataMapper{
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Collections.singletonList(CatalogHeaderBinding.P_VALUE_FOR_MLOG);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_P_VALUE_MLOG;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {

        String pvalLog;

        String pval = databaseValues.get(CatalogHeaderBinding.P_VALUE_FOR_MLOG);

        if(pval != null && pval != "") {
            double p = Double.parseDouble(pval);

            double log = -Math.log10(p);
             pvalLog = String.valueOf(log);
        }
        else {
            pvalLog = "";
        }

        return pvalLog;
    }
}
