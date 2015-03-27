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
 * Created by Dani on 26/03/2015.
 */
@Component
public class PvalueMapper implements CatalogDataMapper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
        return Arrays.asList(CatalogHeaderBinding.P_VALUE_MANTISSA,
                             CatalogHeaderBinding.P_VALUE_EXPONENT);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.P_VALUE;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {

        String pval;

        String mant = databaseValues.get(CatalogHeaderBinding.P_VALUE_MANTISSA);
        String exp = databaseValues.get(CatalogHeaderBinding.P_VALUE_EXPONENT);

        if(!mant.isEmpty() && !exp.isEmpty()) {
            pval = mant.concat("E").concat(exp);
        }
        else {
            pval = "";
        }

        return pval;
    }
}
