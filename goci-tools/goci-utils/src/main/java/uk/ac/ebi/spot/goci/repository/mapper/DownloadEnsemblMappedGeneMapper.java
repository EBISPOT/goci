package uk.ac.ebi.spot.goci.repository.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 12/08/2015.
 *
 * @author emma
 */
@Component
public class DownloadEnsemblMappedGeneMapper implements CatalogDataMapper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.ENSEMBL_MAPPED_GENE,
                             CatalogHeaderBinding.ENSEMBL_UPSTREAM_MAPPED_GENE,
                             CatalogHeaderBinding.ENSEMBL_DOWNSTREAM_MAPPED_GENE);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_ENSEMBL_MAPPED_GENE;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String output;
        String mapped = databaseValues.get(CatalogHeaderBinding.ENSEMBL_MAPPED_GENE);
        if (mapped.isEmpty()) {
            // use upstream - downstream
            String up = databaseValues.get(CatalogHeaderBinding.ENSEMBL_UPSTREAM_MAPPED_GENE);
            String down = databaseValues.get(CatalogHeaderBinding.ENSEMBL_DOWNSTREAM_MAPPED_GENE);
            if (!up.isEmpty() && !down.isEmpty()) {
                output = up + " - " + down;
            }
            else if (!up.isEmpty()) {
                output = up + " - ";
            }
            else if (!down.isEmpty()) {
                output = " - " + down;
            }
            else {
                getLog().warn("Unable to extract mapped data correctly from catalog: " +
                                      CatalogHeaderBinding.ENSEMBL_MAPPED_GENE.getDatabaseName() + " is empty, " +
                                      "and neither were both " + CatalogHeaderBinding.ENSEMBL_UPSTREAM_MAPPED_GENE +
                                      " and " +
                                      CatalogHeaderBinding.DOWNLOAD_ENSEMBL_MAPPED_GENE + " available");
                output = "";
            }
        }
        else {
            output = mapped;
        }
        return output;
    }

}
