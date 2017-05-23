package uk.ac.ebi.spot.goci.repository.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dwelter on 23/03/15.
 */
@Component
public class DownloadSNPIdCurrentMapper implements CatalogDataMapper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Collections.singletonList(CatalogHeaderBinding.SNP_RSID_FOR_ID);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_SNP_ID;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String output;

        String rsId = databaseValues.get(CatalogHeaderBinding.SNP_RSID_FOR_ID);

        if (!rsId.isEmpty() && rsId.contains("rs")) {
            if (rsId.contains("x")) {
                String front = rsId.split("x")[0].trim();
                output = front.substring(2);
            }
            else {
                output = rsId.substring(2);
            }
        }
        else {
            output = "";
        }

        return output;
    }
}
