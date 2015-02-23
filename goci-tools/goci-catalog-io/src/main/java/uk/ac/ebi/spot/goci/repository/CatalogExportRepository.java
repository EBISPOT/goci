package uk.ac.ebi.spot.goci.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBindings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
@Repository
public class CatalogExportRepository {
    private static final String FROM_CLAUSE =
            " FROM CATALOG_SUMMARY_VIEW ";
    private static final String NCBI_WHERE_CLAUSE =
            " WHERE CURATION_STATUS = 'Send to NCBI' ORDER BY STUDY_ID DESC ";
    private static final String DOWNLOAD_WHERE_CLAUSE =
            " WHERE RESULT_PUBLISHED IS NOT NULL ORDER BY STUDY_ID DESC ";

    private final DateFormat df;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CatalogExportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.df = new SimpleDateFormat("dd-MMM-yyyy");
    }

    public String[][] getNCBISpreadsheet() {
        final Map<String, String> dbToNCBI = new LinkedHashMap<>();
        List<String> ncbiQueryHeaders = CatalogHeaderBindings.getNcbiHeaders()
                .stream()
                .peek(binding -> dbToNCBI.put(binding.getDatabaseName(), binding.getNcbiName()))
                .map(CatalogHeaderBinding::getDatabaseName)
                .collect(Collectors.toList());

        String query = buildSelectClause(ncbiQueryHeaders) + FROM_CLAUSE + NCBI_WHERE_CLAUSE;
        List<String[]> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            String[] values = new String[dbToNCBI.keySet().size()];
            int col = 0;
            for (CatalogHeaderBinding binding : CatalogHeaderBindings.getNcbiHeaders()) {
                if (binding.isDate()) {
                    Date value = resultSet.getDate(binding.getDatabaseName());
                    if (value != null) {
                        values[col++] = df.format(value);
                    }
                    else {
                        values[col++] = "";
                    }
                }
                else {
                    String value = resultSet.getString(binding.getDatabaseName());
                    if (value != null) {
                        values[col++] = resultSet.getString(binding.getDatabaseName()).trim();
                    }
                    else {
                        values[col++] = "";
                    }
                }
            }
            return values;
        });
        return rows.toArray(new String[rows.size()][]);
    }

    public String[][] getDownloadSpreadsheet() {
        final Map<String, Integer> columnNumberByLabel = new HashMap<>();
        final AtomicInteger colNum = new AtomicInteger();
        List<String> ncbiQueryHeaders = CatalogHeaderBindings.getDownloadHeaders()
                .stream()
                .peek(binding -> columnNumberByLabel.put(binding.getDownloadName(), colNum.getAndIncrement()))
                .map(CatalogHeaderBinding::getDatabaseName)
                .collect(Collectors.toList());

        String query = buildSelectClause(ncbiQueryHeaders) + FROM_CLAUSE + DOWNLOAD_WHERE_CLAUSE;
        List<String[]> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            String[] values = new String[columnNumberByLabel.keySet().size()];
            int col = 0;
            for (CatalogHeaderBinding binding : CatalogHeaderBindings.getDownloadHeaders()) {
                if (binding.isDate()) {
                    Date value = resultSet.getDate(binding.getDatabaseName());
                    if (value != null) {
                        values[col++] = df.format(value);
                    }
                    else {
                        values[col++] = "";
                    }
                }
                else {
                    String value = resultSet.getString(binding.getDatabaseName());
                    if (value != null) {
                        values[col++] = resultSet.getString(binding.getDatabaseName()).trim();
                    }
                    else {
                        values[col++] = "";
                    }
                }
            }
            return values;
        });
        return rows.toArray(new String[rows.size()][]);
    }

    private String buildSelectClause(List<String> requiredFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");

        Iterator<String> requiredFieldIterator = requiredFields.iterator();
        while (requiredFieldIterator.hasNext()) {
            sb.append(requiredFieldIterator.next());
            if (requiredFieldIterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" ");

        return sb.toString();
    }
}
