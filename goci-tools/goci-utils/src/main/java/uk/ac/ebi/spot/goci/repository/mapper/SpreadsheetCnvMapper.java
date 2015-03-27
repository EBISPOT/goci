package uk.ac.ebi.spot.goci.repository.mapper;

import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.repository.CatalogDataMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 23/02/15
 */
@Component
public class SpreadsheetCnvMapper implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Collections.singletonList(CatalogHeaderBinding.CNV);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.SPREADSHEET_CNV;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        return databaseValues.get(CatalogHeaderBinding.CNV).equals("1") ? "Y" : "N";
    }
}
