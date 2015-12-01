package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emma on 07/10/2015.
 * <p>
 * Service class used by YearlyReportController and MonthlyReportController to build redirect values
 */
@Service
public class ReportService {

    public ReportService() {
    }

    // Creates redirect statement
    public String buildRedirect(String redirectPrefix, Map<String, Object> filterMap) {

        // Build redirect
        Collection<String> redirectBuilder = new ArrayList<>();
        String redirect = "";

        if (!filterMap.isEmpty()) {
            for (String key : filterMap.keySet()) {
                redirectBuilder.add(key + "=" + filterMap.get(key));
            }
        }
        if (!redirectBuilder.isEmpty()) {
            redirect = String.join("&", redirectBuilder);
        }

        if (redirect.isEmpty()) {
            return redirectPrefix;
        } else {
            return redirectPrefix + "?" + redirect;
        }
    }

    // Builds a map that can be used to create a redirect
    public Map<String, Object> buildRedirectMap(Long status, Long curator, Integer year, Integer month) {

        Map<String, Object> filterMap = new HashMap<>();

        if (curator != null) {
            filterMap.putIfAbsent("curator", curator);
        }
        if (status != null) {
            filterMap.putIfAbsent("status", status);
        }
        if (year != null) {
            filterMap.putIfAbsent("year", year);
        }
        if (month != null) {
            filterMap.putIfAbsent("month", month);
        }

        return filterMap;
    }
}