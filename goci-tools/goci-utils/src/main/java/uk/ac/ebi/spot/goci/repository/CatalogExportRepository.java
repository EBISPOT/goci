package uk.ac.ebi.spot.goci.repository;

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
    private static final String FROM_CLAUSE =
            " FROM CATALOG_SUMMARY_VIEW ";
    private static final String NCBI_WHERE_CLAUSE =
            " WHERE CURATION_STATUS = 'Send to NCBI' OR RESULT_PUBLISHED IS NOT NULL ORDER BY STUDY_ID DESC ";
    private static final String DOWNLOAD_WHERE_CLAUSE =
            " WHERE RESULT_PUBLISHED IS NOT NULL ORDER BY PUBMED_ID DESC ";

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

            // next, generate new unique ID from values for study id, snp id, (author reported) gene id
            String studyIdStr = rowMap.get(CatalogHeaderBinding.STUDY_ID);
            String associationIdStr = rowMap.get(CatalogHeaderBinding.ASSOCIATION_ID);
            String uniqueKey;
            if (!studyIdStr.isEmpty() && !associationIdStr.isEmpty()) {
                long studyId = Long.valueOf(studyIdStr);
                long associationId = Long.valueOf(associationIdStr);
                uniqueKey = Long.toString(generateUniqueID(studyId, associationId));
            }
            else {
                uniqueKey = "";
            }
            rowMap.put(CatalogHeaderBinding.UNIQUE_KEY, uniqueKey);

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

    public String[][] getDownloadSpreadsheet(String version) {
        List<String> downloadOutputHeaders = getOrderedDownloadHeaders(version)
                .stream()
                .filter(binding -> binding.getDownloadName() != null)
                .map(CatalogHeaderBinding::getDownloadName)
                .collect(Collectors.toList());


        List<String> downloadQueryHeaders = getOrderedDownloadHeaders(version)
                .stream()
                .filter(binding -> binding.getDatabaseName() != null)
                .map(CatalogHeaderBinding::getDatabaseName)
                .collect(Collectors.toList());

        String query = buildSelectClause(downloadQueryHeaders) + FROM_CLAUSE + DOWNLOAD_WHERE_CLAUSE;
        List<String[]> rows = jdbcTemplate.query(query, (resultSet, i) -> {
            Map<CatalogHeaderBinding, String> dataForMapping = new LinkedHashMap<>();
            Map<CatalogHeaderBinding, String> rowMap = new LinkedHashMap<>();
            for (CatalogHeaderBinding binding : getOrderedDownloadHeaders(version)) {
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

    private long generateUniqueID(long... compositeKeys) {
        return recursivelyPair(compositeKeys);
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

    //put the CatalogHeaderBindings into the correct order for the download spreadsheet
    private List<CatalogHeaderBinding> getOrderedDownloadHeaders(String version) {
        List<CatalogHeaderBinding> catalogHeaders = CatalogHeaderBindings.getDownloadHeaders();
        List<CatalogHeaderBinding> orderedHeaders = new ArrayList<CatalogHeaderBinding>();
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
                                  "MAPPED_GENE",
                                  "UPSTREAM_GENE_ID",
                                  "DOWNSTREAM_GENE_ID",
                                  "SNP_GENE_IDS",
                                  "UPSTREAM_GENE_DISTANCE",
                                  "DOWNSTREAM_GENE_DISTANCE",
                                  "STRONGEST SNP-RISK ALLELE",
                                  "SNPS",
                                  "MERGED",
                                  "SNP_ID_CURRENT",
                                  "CONTEXT",
                                  "INTERGENIC",
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
                                  "MAPPED_GENE",
                                  "UPSTREAM_GENE_ID",
                                  "DOWNSTREAM_GENE_ID",
                                  "SNP_GENE_IDS",
                                  "UPSTREAM_GENE_DISTANCE",
                                  "DOWNSTREAM_GENE_DISTANCE",
                                  "STRONGEST SNP-RISK ALLELE",
                                  "SNPS",
                                  "MERGED",
                                  "SNP_ID_CURRENT",
                                  "CONTEXT",
                                  "INTERGENIC",
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
                if (binding.getDownloadName() != null) {
                    if (binding.getDownloadName().equals(header)) {
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

}
