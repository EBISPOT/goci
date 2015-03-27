package uk.ac.ebi.spot.goci.repository.mapper;

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
 * @date 23/02/15
 */
@Component
public class SpreadsheetCiMapper implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.CI, CatalogHeaderBinding.CI_QUALIFIER);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.SPREADSHEET_CI;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        String result = databaseValues.get(CatalogHeaderBinding.CI);
        if (!databaseValues.get(CatalogHeaderBinding.CI_QUALIFIER).equals("")) {
            result = result.concat(" (").concat(databaseValues.get(CatalogHeaderBinding.CI_QUALIFIER)).concat(")");
        }
        return result;
    }
}
