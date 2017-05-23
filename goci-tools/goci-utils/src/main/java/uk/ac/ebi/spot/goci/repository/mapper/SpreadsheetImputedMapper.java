package uk.ac.ebi.spot.goci.repository.mapper;

import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by dwelter on 22/03/16.
 */
public class SpreadsheetImputedMapper implements CatalogDataMapper {
    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Collections.singletonList(CatalogHeaderBinding.IMPUTED);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.SPREADSHEET_IMPUTED;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {
        return databaseValues.get(CatalogHeaderBinding.IMPUTED).equals("1") ? "Y" : "N";
    }
}
