package uk.ac.ebi.spot.goci.model;

import java.util.List;
import java.util.Map;

/**
 * A component that takes data obtained from within the database and maps it a single key/value pair that can be used in
 * the production of the output file.
 *
 * @author Tony Burdett
 * @date 23/02/15
 */
public interface CatalogDataMapper {
    /**
     * A list of catalog header bindings this mapper can utilise
     *
     * @return the header bindings this mapper needs to produce output
     */
    List<CatalogHeaderBinding> getRequiredDatabaseFields();

    /**
     * The catalog header binding this mapper produces in the output
     *
     * @return the name of the output field in the final spreadsheet
     */
    CatalogHeaderBinding getOutputField();

    /**
     * Takes a map of column headings to data items, where the keys are the database header bindings and the values are
     * all fields that were flagged as required in the {@link uk.ac.ebi.spot.goci.model.CatalogHeaderBinding}.  Produces
     * a single value, that should be inserted into the mapped spreadsheet of results in the column dictated by the
     * catalog header binding "output field"
     *
     * @param databaseValues the data acquired from the database
     * @return a single string for the produced output (may have merged several fields)
     */
    String produceOutput(Map<CatalogHeaderBinding, String> databaseValues);
}
