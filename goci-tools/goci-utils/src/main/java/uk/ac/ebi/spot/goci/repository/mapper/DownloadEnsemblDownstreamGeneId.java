package uk.ac.ebi.spot.goci.repository.mapper;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by emma on 27/08/2015.
 *
 * @author emma
 *         <p>
 *         Mapper component to handle scenario where a mapped Ensembl gene is present. In this case the download should
 *         not include details of downstream gene IDs.
 */
@Component
public class DownloadEnsemblDownstreamGeneId implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.ENSEMBL_MAPPED_GENE,
                             CatalogHeaderBinding.ENSEMBL_DOWNSTREAM_GENE_ID);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_ENSEMBL_DOWNSTREAM_GENE_ID;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String output;
        String mapped = databaseValues.get(CatalogHeaderBinding.ENSEMBL_MAPPED_GENE);
        if (mapped.isEmpty()) {
            output = databaseValues.get(CatalogHeaderBinding.ENSEMBL_DOWNSTREAM_GENE_ID);
        }
        else {
            output = "";
        }
        return output;
    }
}
