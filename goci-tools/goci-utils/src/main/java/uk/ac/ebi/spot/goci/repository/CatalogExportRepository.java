package uk.ac.ebi.spot.goci.repository;

import org.flywaydb.core.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.CatalogDataMapper;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBindings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
//    private static final String FROM_CLAUSE =
//            " FROM CATALOG_SUMMARY_VIEW ";
    private static final String FROM_NCBI_CLAUSE =
            " FROM NCBI_CATALOG_SUMMARY_VIEW ";
    private static final String NCBI_WHERE_CLAUSE =
            " WHERE CATALOG_PUBLISH_DATE IS NOT NULL AND CATALOG_UNPUBLISH_DATE IS NULL ORDER BY STUDY_ID DESC ";
    private static final String DOWNLOAD_WHERE_CLAUSE =
            " WHERE (REGEXP_LIKE (CHROMOSOME_NAME,'^[[:digit:]]+$') OR CHROMOSOME_NAME = 'X' OR CHROMOSOME_NAME = 'Y') " +
                    "AND CATALOG_PUBLISH_DATE IS NOT NULL AND CATALOG_UNPUBLISH_DATE IS NULL ORDER BY PUBMED_ID DESC ";

    private final DateFormat df;

    private JdbcTemplate jdbcTemplate;

    private Collection<CatalogDataMapper> dataMappers;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

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
                .filter(binding -> binding.getNcbiInclusion().mapsToColumn())
                .filter(binding -> binding.getNcbiInclusion().columnName().isPresent())
                .map(binding -> binding.getNcbiInclusion().columnName().get())
                .collect(Collectors.toList());

        // Get equivalent headers in database
        List<String> ncbiQueryHeaders = CatalogHeaderBindings.getNcbiHeaders()
                .stream()
                .filter(binding -> binding.getDatabaseName().isPresent())
                .map(binding -> binding.getDatabaseName().get())
                .collect(Collectors.toList());

        // export data and return
        return extractData(buildSelectClause(ncbiQueryHeaders) + FROM_NCBI_CLAUSE + NCBI_WHERE_CLAUSE,
                           CatalogHeaderBindings.getNcbiHeaders(),
                           ncbiOutputHeaders,
                           CatalogHeaderBinding::getNcbiInclusion);
    }

    public String[][] getDownloadSpreadsheet(String version) {
        // Get headers for output spreadsheet (in order)
        List<String> downloadOutputHeaders = getOrderedDownloadHeaders(version)
                .stream()
                .filter(binding -> binding.getDownloadInclusion().mapsToColumn())
                .filter(binding -> binding.getDownloadInclusion().columnName().isPresent())
                .map(binding -> binding.getDownloadInclusion().columnName().get())
                .collect(Collectors.toList());

        // Get equivalent headers in database
        List<String> downloadQueryHeaders = getOrderedDownloadHeaders(version)
                .stream()
                .filter(binding -> binding.getDatabaseName().isPresent())
                .map(binding -> binding.getDatabaseName().get())
                .collect(Collectors.toList());

        // export data and return
        return extractData(buildSelectClause(downloadQueryHeaders) + FROM_NCBI_CLAUSE + DOWNLOAD_WHERE_CLAUSE,
                           getOrderedDownloadHeaders(version),
                           downloadOutputHeaders,
                           CatalogHeaderBinding::getDownloadInclusion);
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

    private String[][] extractData(String query,
                                   List<CatalogHeaderBinding> bindings,
                                   List<String> outputHeaders,
                                   InclusionExtractor extractor) {
        getLog().info("Extracting data for spreadsheet export...");
        final LinkedHashMap<Long, Map<CatalogHeaderBinding, String>> data = new LinkedHashMap<>();
        List<Map<CatalogHeaderBinding, String>> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            Map<CatalogHeaderBinding, String> dataForMapping = new LinkedHashMap<>();
            Map<CatalogHeaderBinding, String> rowMap = new LinkedHashMap<>();
            for (CatalogHeaderBinding binding : bindings) {
                if (extractor.extract(binding).mapsToColumn()) {
                    // insert headings in declaration order (this is the correct order)
                    // which controls for reinsertion
                    rowMap.put(binding, "");
                }

                // now extract data if possible
                if (binding.getDatabaseName().isPresent()) {
                    if (extractor.extract(binding).mapsToColumn()) {
                        // if data maps to a column, put data in that column
                        rowMap.put(binding, extractValue(binding, resultSet));
                    }
                    else {
                        // if data doesn't map to a column, extract for processing
                        dataForMapping.put(binding, extractValue(binding, resultSet));
                    }
                }
            }
            // now we've added all the direct (1:1) mapping columns, collect up those for processing
            dataMappers.stream()
                    .filter(mapper -> rowMap.containsKey(mapper.getOutputField()))
                    .forEach(mapper -> rowMap.put(mapper.getOutputField(), mapper.produceOutput(dataForMapping)));

            // next, generate new unique ID from unique ID values
            List<Long> identifiers = new ArrayList<>();
            for (CatalogHeaderBinding binding : bindings) {
                if (extractor.extract(binding).isIdentifier()) {
                    String val = null;
                    if (rowMap.containsKey(binding)) {
                        val = rowMap.get(binding);
                    }
                    else {
                        val = dataForMapping.get(binding);
                    }

                    if (val != null) {
                        if (!val.isEmpty()) {
                            if (!StringUtils.isNumeric(val)) {
                                identifiers.add((long) val.hashCode());
                            }
                            else {
                                try {
                                    identifiers.add(Long.valueOf(val));
                                }
                                catch (NumberFormatException e) {
                                    throw new RuntimeException(
                                            "Cannot use field " + binding + " = " + val + " as ID: " +
                                                    "not a valid numeric value", e);
                                }
                            }
                        }
                    }
                    else {
                        throw new RuntimeException("Unable to locate data for binding '" + binding + "'");
                    }
                }
            }
            long id = generateUniqueID(identifiers);

            if (bindings.contains(CatalogHeaderBinding.UNIQUE_KEY)) {
                rowMap.put(CatalogHeaderBinding.UNIQUE_KEY, Long.toString(id));
            }

            // check if this row already exists
            if (data.containsKey(id)) {
                Map<CatalogHeaderBinding, String> existingValues = data.get(id);
                // now merge new data with existing values
                for (CatalogHeaderBinding binding : bindings) {
                    String existingValue = existingValues.get(binding);
                    String newValue = rowMap.get(binding);
                    if (existingValue != null) {
                        if (!existingValue.contains(newValue)) {
                            if (extractor.extract(binding).isConcatenatable()) {
                                // existing value does not already contain new value, comma separate and append
                                String combinedValue = existingValue.concat(", ").concat(newValue);
                                // update existing values with this new combined value
                                existingValues.put(binding, combinedValue);
                            }
                            else if (binding.getDatabaseName().isPresent()) {
                                throw new RuntimeException(
                                        "Non-concatenatable values for " + binding.getDatabaseName().get() + " " +
                                                "differ in row ID '" + id + "': " +
                                                "existing = " + existingValue + ", new = " + newValue + ".\n" +
                                                "This would result in a new row, causing duplicated unique IDs");

                            }
                            else {
                                throw new RuntimeException(
                                        "Non-concatenatable values for " + binding.toString() + " " +
                                                "differ in row ID '" + id + "': " +
                                                "existing = " + existingValue + ", new = " + newValue + ".\n" +
                                                "This would result in a new row, causing duplicated unique IDs");


                            }
                        }
                        // Need to include a special case for chromosome name
                        // as multiple chromosome names can contain similar characters
                        // e.g. 'CHR_HSCHR6_MHC_COX_CTG1' and '6'
                        else if (binding.name().equalsIgnoreCase("CHROMOSOME_NAME") &&
                                !existingValue.contains(" ".concat(newValue)) && !existingValue.equals(newValue)) {
                            if (extractor.extract(binding).isConcatenatable()) {
                                // existing value does not already contain new value, comma separate and append
                                String combinedValue = existingValue.concat(", ").concat(newValue);
                                // update existing values with this new combined value
                                existingValues.put(binding, combinedValue);
                            }
                            else if (binding.getDatabaseName().isPresent()) {
                                throw new RuntimeException(
                                        "Non-concatenatable values for " + binding.getDatabaseName().get() + " " +
                                                "differ in row ID '" + id + "': " +
                                                "existing = " + existingValue + ", new = " + newValue + ".\n" +
                                                "This would result in a new row, causing duplicated unique IDs");

                            }
                            else {
                                throw new RuntimeException(
                                        "Non-concatenatable values for " + binding.toString() + " " +
                                                "differ in row ID '" + id + "': " +
                                                "existing = " + existingValue + ", new = " + newValue + ".\n" +
                                                "This would result in a new row, causing duplicated unique IDs");


                            }
                        }
                        else {
                            getLog().debug("Ignoring value '" + newValue + "' for " + binding + " - " +
                                                   "already captured by existing data ('" + existingValue + "')");
                        }
                    }
                    else {
                        if (newValue == null) {
                            getLog().debug("Safely ignoring value null for " + binding + " - " +
                                                   "existing value already null exported to spreadsheet");
                        }
                        else {
                            throw new RuntimeException("Cannot ignore value '" + newValue + "' for " + binding + " - " +
                                                               "overrides existing null value");
                        }
                    }
                }
            }
            else {
                data.put(id, rowMap);
            }

            return rowMap;
        });

        // finally, convert each "line" map into a string array
        getLog().info("Extracted " + rows.size() + " rows of data from the GWAS database, mapping to spreadsheet...");
        List<String[]> lines = new ArrayList<>();
        for (long id : data.keySet()) {
            Map<CatalogHeaderBinding, String> lineMap = data.get(id);
            String[] line = new String[lineMap.keySet().size()];
            int col = 0;
            for (CatalogHeaderBinding key : lineMap.keySet()) {
                line[col++] = lineMap.get(key);
            }
            lines.add(line);
        }

        // add the first row, our headers
        lines.add(0, outputHeaders.toArray(new String[outputHeaders.size()]));
        getLog().info("Spreadsheet flattened down to " + lines.size() +
                              " rows of data by comma separating non-identifier fields");
        getLog().info("Spreadsheet data export finished");
        // and convert all lines into a 2D string array
        return lines.toArray(new String[lines.size()][]);
    }

    private String extractValue(CatalogHeaderBinding binding, ResultSet resultSet) throws SQLException {
        if (binding.getDatabaseName().isPresent()) {
            if (binding.isDate()) {
                Date value = resultSet.getDate(binding.getDatabaseName().get());
                if (value != null) {
                    return df.format(value);
                }
                else {
                    return "";
                }
            }
            else {
                String value = resultSet.getString(binding.getDatabaseName().get());
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
        else {
            throw new RuntimeException("Cannot extract data for binding '" + binding + "': " +
                                               "no given column name to extract from ResultSet");
        }
    }

    private long generateUniqueID(long... compositeKeys) {
        //        return recursivelyPair(compositeKeys);
        return concatenateAndHash(compositeKeys);
    }

    private long generateUniqueID(List<Long> compositeKeys) {
        long[] longs = new long[compositeKeys.size()];
        for (int i = 0; i < compositeKeys.size(); i++) {
            longs[i] = compositeKeys.get(i);
        }
        //        return recursivelyPair(longs);
        return concatenateAndHash(longs);
    }

    private long recursivelyPair(long[] compositeKeys) {
        if (compositeKeys.length > 1) {
            if (compositeKeys.length == 2) {
                return calculateCantorPair(compositeKeys[0], compositeKeys[1]);
            }
            else {
                return calculateCantorPair(
                        recursivelyPair(Arrays.copyOfRange(compositeKeys, 0, compositeKeys.length - 1)),
                        compositeKeys[compositeKeys.length - 1]);
            }
        }
        else {
            if (compositeKeys.length == 1) {
                return compositeKeys[0];
            }
            else {
                return 0;
            }
        }
    }

    private static long calculateCantorPair(long x, long y) {
        return (long) (0.5 * (x + y) * (x + y + 1) + y);
    }

    private static long concatenateAndHash(long[] longs) {
        StringBuilder sb = new StringBuilder();
        for (long l : longs) {
            sb.append(l);
        }
        return Math.abs((long) sb.toString().hashCode());
    }

    //put the CatalogHeaderBindings into the correct order for the download spreadsheet
    private List<CatalogHeaderBinding> getOrderedDownloadHeaders(String version) {
        List<CatalogHeaderBinding> catalogHeaders = CatalogHeaderBindings.getDownloadHeaders();
        List<CatalogHeaderBinding> orderedHeaders = new ArrayList<>();
        List<String> order;

        if (version.equals("d")) {
            order = Arrays.asList("DATE ADDED TO CATALOG",
                                  "PUBMEDID",
                                  "FIRST AUTHOR",
                                  "DATE",
                                  "JOURNAL",
                                  "LINK",
                                  "STUDY",
                                  "DISEASE/TRAIT",
                                  "INITIAL SAMPLE DESCRIPTION",
                                  "REPLICATION SAMPLE DESCRIPTION",
                                  "REGION",
                                  "CHR_ID",
                                  "CHR_POS",
                                  "REPORTED GENE(S)",
                                  "ENTREZ_MAPPED_GENE",
                                  "ENSEMBL_MAPPED_GENE",
                                  "ENTREZ_UPSTREAM_GENE_ID",
                                  "ENTREZ_DOWNSTREAM_GENE_ID",
                                  "ENSEMBL_UPSTREAM_GENE_ID",
                                  "ENSEMBL_DOWNSTREAM_GENE_ID",
                                  "SNP_GENE_IDS_ENTREZ",
                                  "SNP_GENE_IDS_ENSEMBL",
                                  "ENTREZ_UPSTREAM_GENE_DISTANCE",
                                  "ENTREZ_DOWNSTREAM_GENE_DISTANCE",
                                  "ENSEMBL_UPSTREAM_GENE_DISTANCE",
                                  "ENSEMBL_DOWNSTREAM_GENE_DISTANCE",
                                  "STRONGEST SNP-RISK ALLELE",
                                  "SNPS",
                                  "MERGED",
                                  "SNP_ID_CURRENT",
                                  "CONTEXT",
                                  "INTERGENIC_ENTREZ",
                                  "INTERGENIC_ENSEMBL",
                                  "RISK ALLELE FREQUENCY",
                                  "P-VALUE",
                                  "PVALUE_MLOG",
                                  "P-VALUE (TEXT)",
                                  "OR or BETA",
                                  "95% CI (TEXT)",
                                  "PLATFORM [SNPS PASSING QC]",
                                  "CNV");
        }
        else {
            order = Arrays.asList("DATE ADDED TO CATALOG",
                                  "PUBMEDID",
                                  "FIRST AUTHOR",
                                  "DATE",
                                  "JOURNAL",
                                  "LINK",
                                  "STUDY",
                                  "DISEASE/TRAIT",
                                  "INITIAL SAMPLE DESCRIPTION",
                                  "REPLICATION SAMPLE DESCRIPTION",
                                  "REGION",
                                  "CHR_ID",
                                  "CHR_POS",
                                  "REPORTED GENE(S)",
                                  "ENTREZ_MAPPED_GENE",
                                  "ENSEMBL_MAPPED_GENE",
                                  "ENTREZ_UPSTREAM_GENE_ID",
                                  "ENTREZ_DOWNSTREAM_GENE_ID",
                                  "ENSEMBL_UPSTREAM_GENE_ID",
                                  "ENSEMBL_DOWNSTREAM_GENE_ID",
                                  "SNP_GENE_IDS_ENTREZ",
                                  "SNP_GENE_IDS_ENSEMBL",
                                  "ENTREZ_UPSTREAM_GENE_DISTANCE",
                                  "ENTREZ_DOWNSTREAM_GENE_DISTANCE",
                                  "ENSEMBL_UPSTREAM_GENE_DISTANCE",
                                  "ENSEMBL_DOWNSTREAM_GENE_DISTANCE",
                                  "STRONGEST SNP-RISK ALLELE",
                                  "SNPS",
                                  "MERGED",
                                  "SNP_ID_CURRENT",
                                  "CONTEXT",
                                  "INTERGENIC_ENTREZ",
                                  "INTERGENIC_ENSEMBL",
                                  "RISK ALLELE FREQUENCY",
                                  "P-VALUE",
                                  "PVALUE_MLOG",
                                  "P-VALUE (TEXT)",
                                  "OR or BETA",
                                  "95% CI (TEXT)",
                                  "PLATFORM [SNPS PASSING QC]",
                                  "CNV",
                                  "MAPPED_TRAIT",
                                  "MAPPED_TRAIT_URI");
        }

        for (String header : order) {
            for (CatalogHeaderBinding binding : catalogHeaders) {
                if (binding.getDownloadInclusion().columnName().isPresent()) {
                    if (binding.getDownloadInclusion().columnName().get().equals(header)) {
                        orderedHeaders.add(binding);
                    }
                }
                else {
                    if (!orderedHeaders.contains(binding)) {
                        orderedHeaders.add(binding);
                    }
                }
            }
        }
        return orderedHeaders;
    }

    private interface InclusionExtractor {
        CatalogHeaderBinding.Inclusion extract(CatalogHeaderBinding binding);
    }
}
