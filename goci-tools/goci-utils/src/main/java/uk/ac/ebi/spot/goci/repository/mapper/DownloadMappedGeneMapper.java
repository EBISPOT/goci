package uk.ac.ebi.spot.goci.repository.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.repository.CatalogDataMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 24/02/15
 */
@Component
public class DownloadMappedGeneMapper implements CatalogDataMapper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.MAPPED_GENE,
                             CatalogHeaderBinding.UPSTREAM_MAPPED_GENE,
                             CatalogHeaderBinding.DOWNSTREAM_MAPPED_GENE);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_MAPPED_GENE;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String output;
        String mapped = databaseValues.get(CatalogHeaderBinding.MAPPED_GENE);
        if (mapped.isEmpty()) {
            // use upstream - downstream
            String up = databaseValues.get(CatalogHeaderBinding.UPSTREAM_MAPPED_GENE);
            String down = databaseValues.get(CatalogHeaderBinding.DOWNSTREAM_MAPPED_GENE);
            if (!up.isEmpty() && !down.isEmpty()) {
                output = up + " - " + down;
            }
            else {
                getLog().warn("Unable to extract mapped data correctly from catalog: " +
                                      CatalogHeaderBinding.MAPPED_GENE.getDatabaseName() + " is empty, " +
                                      "and neither were both " + CatalogHeaderBinding.UPSTREAM_MAPPED_GENE + " and " +
                                      CatalogHeaderBinding.DOWNLOAD_MAPPED_GENE + " available");
                output = "";
            }
        }
        else {
            output = mapped;
        }
        return output;
    }
}
