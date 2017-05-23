package uk.ac.ebi.spot.goci.repository.mapper;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 26/03/2015.
 * <p>
 * Mapper to insert 'intergenic' into the context field if no context is specified and the SNP has been labelled as
 * 'intergenic' via the binary is_intergenic field
 */
@Component
public class DownloadContextMapper implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.CONTEXT,
                             CatalogHeaderBinding.INTERGENIC_CONTEXT_ENSEMBL,
                             CatalogHeaderBinding.INTERGENIC_CONTEXT_ENTREZ);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_CONTEXT;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String context = databaseValues.get(CatalogHeaderBinding.CONTEXT);
        String isIntergenicEntrez = databaseValues.get(CatalogHeaderBinding.INTERGENIC_CONTEXT_ENTREZ);
        String isIntergenicEnsembl = databaseValues.get(CatalogHeaderBinding.INTERGENIC_CONTEXT_ENSEMBL);

        if (context.isEmpty()) {
            if (isIntergenicEntrez != null && isIntergenicEnsembl != null) {
                if (isIntergenicEntrez.equals("1") && isIntergenicEnsembl.equals("1")) {
                    context = "intergenic";
                }
            }
        }

        return context;
    }
}
