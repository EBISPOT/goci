package uk.ac.ebi.spot.goci.repository.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
* Created by dwelter on 23/03/15.
*/

@Component
public class DownloadPvalLogMapper implements CatalogDataMapper {
    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Override public List<CatalogHeaderBinding> getRequiredDatabaseFields() {
//        return Arrays.asList(CatalogHeaderBinding.P_VALUE_MANT_FOR_MLOG,
//                             CatalogHeaderBinding.P_VALUE_EXPO_FOR_MLOG);
        return Arrays.asList(CatalogHeaderBinding.P_VALUE_MANTISSA,
                             CatalogHeaderBinding.P_VALUE_EXPONENT);
    }

    @Override public CatalogHeaderBinding getOutputField() {
        return CatalogHeaderBinding.DOWNLOAD_P_VALUE_MLOG;
    }

    @Override public String produceOutput(Map<CatalogHeaderBinding, String> databaseValues) {

        String pvalLog;

        String mant = databaseValues.get(CatalogHeaderBinding.P_VALUE_MANTISSA);
        String exp = databaseValues.get(CatalogHeaderBinding.P_VALUE_EXPONENT);

        if(!mant.isEmpty() && !exp.isEmpty()) {
            double m = Double.parseDouble(mant);
            double e = Double.parseDouble(exp);

            double pval = m * Math.pow(10,e);

            double log = -Math.log10(pval);
             pvalLog = String.valueOf(log);
        }
        else {
            pvalLog = "";
        }

        return pvalLog;
    }
}
