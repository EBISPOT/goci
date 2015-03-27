package uk.ac.ebi.spot.goci.repository.mapper;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.repository.CatalogDataMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Dani on 26/03/2015.
 *
 * Mapper to insert 'intergenic' into the context field if no context is specificed and
 * the SNP has been labelled as 'intergenic' via the binary is_intergenic field
 *
 */
@Component
public class DownloadContextMapper implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.CONTEXT,
                             CatalogHeaderBinding.INTERGENIC_CONTEXT);    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_CONTEXT;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String context = databaseValues.get(CatalogHeaderBinding.CONTEXT);

        if (context.isEmpty()){
            if(databaseValues.get(CatalogHeaderBinding.INTERGENIC_CONTEXT).equals("1")){
                context = "intergenic";
            }
        }

        return context;
    }
}
