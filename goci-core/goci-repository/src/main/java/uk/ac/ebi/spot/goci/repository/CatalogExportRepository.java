package uk.ac.ebi.spot.goci.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBindings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // TODO INCLUDE OR CLAUSE TO GET ALL PUBLISHED STUDIES ALSO
    private static final String NCBI_WHERE_CLAUSE =
            " WHERE CURATION_STATUS = 'Send to NCBI' OR RESULT_PUBLISHED IS NOT NULL ORDER BY STUDY_ID DESC ";
    private static final String DOWNLOAD_WHERE_CLAUSE =
            " WHERE RESULT_PUBLISHED IS NOT NULL ORDER BY STUDY_ID DESC ";

    private final DateFormat df;

    private JdbcTemplate jdbcTemplate;

    private Collection<CatalogDataMapper> dataMappers;

    @Autowired(required = false)
    public CatalogExportRepository(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, Collections.emptyList());
    }

    @Autowired(required = false)
    public CatalogExportRepository(JdbcTemplate jdbcTemplate, Collection<CatalogDataMapper> dataMappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataMappers = dataMappers;
        this.df = new SimpleDateFormat("dd-MMM-yyyy");
    }

    public String[][] getNCBISpreadsheet() {

        // Get headers for output spreadsheet
        List<String> ncbiOutputHeaders = CatalogHeaderBindings.getNcbiHeaders()
                .stream()
                .filter(binding -> binding.getNcbiName() != null)
                .map(CatalogHeaderBinding::getNcbiName)
                .collect(Collectors.toList());

        // Get equivalent headers in database
        List<String> ncbiQueryHeaders = CatalogHeaderBindings.getNcbiHeaders()
                .stream()
                .filter(binding -> binding.getDatabaseName() != null)
                .map(CatalogHeaderBinding::getDatabaseName)
                .collect(Collectors.toList());

        // Build query
        String query = buildSelectClause(ncbiQueryHeaders) + FROM_CLAUSE + NCBI_WHERE_CLAUSE;
        List<String[]> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            Map<CatalogHeaderBinding, String> dataForMapping = new LinkedHashMap<>();
            Map<CatalogHeaderBinding, String> rowMap = new LinkedHashMap<>();
            for (CatalogHeaderBinding binding : CatalogHeaderBindings.getNcbiHeaders()) {
                if (binding.getNcbiName() != null) {
                    // insert headings in declaration order (this is the correct order)
                    // which controls for reinsertion
                    rowMap.put(binding, "");
                }

                // now extract data if possible
                if (binding.getDatabaseName() != null) {
                    if (binding.getNcbiName() != null) {
                        rowMap.put(binding, extractValue(binding, resultSet));
                    }
                    else {
                        // if ncbi name is null, this data needs mapping
                        dataForMapping.put(binding, extractValue(binding, resultSet));
                    }
                }
            }
            // now we've mapped all the direct values, collect up those for processing
            dataMappers.stream()
                    .filter(mapper -> rowMap.containsKey(mapper.getOutputField()))
                    .forEach(mapper -> rowMap.put(mapper.getOutputField(), mapper.produceOutput(dataForMapping)));

            // finally, convert rowMap into a string array and return
            String[] row = new String[rowMap.keySet().size()];
            int col = 0;
            for (CatalogHeaderBinding key : rowMap.keySet()) {
                row[col++] = rowMap.get(key);
            }
            return row;
        });

        // add the first row, our headers
        rows.add(0, ncbiOutputHeaders.toArray(new String[ncbiOutputHeaders.size()]));
        // and convert to a 2D string array
        return rows.toArray(new String[rows.size()][]);
    }

    public String[][] getDownloadSpreadsheet() {
        List<String> downloadOutputHeaders = CatalogHeaderBindings.getDownloadHeaders()
                .stream()
                .filter(binding -> binding.getDownloadName() != null)
                .map(CatalogHeaderBinding::getDownloadName)
                .collect(Collectors.toList());

        List<String> downloadQueryHeaders = CatalogHeaderBindings.getDownloadHeaders()
                .stream()
                .filter(binding -> binding.getDatabaseName() != null)
                .map(CatalogHeaderBinding::getDatabaseName)
                .collect(Collectors.toList());

        String query = buildSelectClause(downloadQueryHeaders) + FROM_CLAUSE + DOWNLOAD_WHERE_CLAUSE;
        List<String[]> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            Map<CatalogHeaderBinding, String> dataForMapping = new LinkedHashMap<>();
            Map<CatalogHeaderBinding, String> rowMap = new LinkedHashMap<>();
            for (CatalogHeaderBinding binding : CatalogHeaderBindings.getDownloadHeaders()) {
                if (binding.getDownloadName() != null) {
                    // insert headings in declaration order (this is the correct order)
                    // which controls for reinsertion
                    rowMap.put(binding, "");
                }

                // now extract data if possible
                if (binding.getDatabaseName() != null) {
                    if (binding.getDownloadName() != null) {
                        rowMap.put(binding, extractValue(binding, resultSet));
                    }
                    else {
                        // if download name is null, this data needs mapping
                        dataForMapping.put(binding, extractValue(binding, resultSet));
                    }
                }
            }

            // now we've mapped all the direct values, collect up those for processing
            dataMappers.stream()
                    .filter(mapper -> rowMap.containsKey(mapper.getOutputField()))
                    .forEach(mapper -> rowMap.put(mapper.getOutputField(), mapper.produceOutput(dataForMapping)));

            // finally, convert rowMap into a string array and return
            String[] row = new String[rowMap.keySet().size()];
            int col = 0;
            for (CatalogHeaderBinding key : rowMap.keySet()) {
                row[col++] = rowMap.get(key);
            }
            return row;
        });

        // add the first row, our headers
        rows.add(0, downloadOutputHeaders.toArray(new String[downloadOutputHeaders.size()]));
        // and convert to a 2D string array
        return rows.toArray(new String[rows.size()][]);
    }

    private String buildSelectClause(List<String> requiredFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT ");

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

    private String extractValue(CatalogHeaderBinding binding, ResultSet resultSet) throws SQLException {
        if (binding.isDate()) {
            Date value = resultSet.getDate(binding.getDatabaseName());
            if (value != null) {
                return df.format(value);
            }
            else {
                return "";
            }
        }
        else {
            String value = resultSet.getString(binding.getDatabaseName());
            if (value != null) {

                // Remove new lines or carriage returns in value
                String newline = System.getProperty("line.separator");
                if (value.contains(newline)) {
                    value = value.replaceAll("\n", "").replaceAll("\r", "");
                }

                return value.trim();
            }
            else {
                return "";
            }
        }
    }
}
