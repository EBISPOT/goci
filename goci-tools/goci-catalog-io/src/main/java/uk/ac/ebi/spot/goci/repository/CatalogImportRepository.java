package uk.ac.ebi.spot.goci.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.exception.DataImportException;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBindings;

import java.lang.reflect.Array;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 20/02/15
 */
@Repository
public class CatalogImportRepository {
    private JdbcTemplate jdbcTemplate;

    private SimpleJdbcInsert insertStudyReport;
    private StudyReportUpdate updateStudyReport;

    private SimpleJdbcInsert insertAssociationReport;
    private AssociationReportUpdate updateAssociationReport;

    private static final String SELECT_STUDY_REPORTS =
            "SELECT ID FROM STUDY_REPORT WHERE STUDY_ID = ?";

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired(required = false)
    public CatalogImportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertStudyReport =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("STUDY_REPORT")
                        .usingColumns("STUDY_ID")
                        .usingColumns("PUBMED_ID_ERROR")
                        .usingColumns("NCBI_PAPER_TITLE")
                        .usingColumns("NCBI_FIRST_AUTHOR")
                        .usingColumns("NCBI_NORMALIZED_FIRST_AUTHOR")
                        .usingColumns("NCBI_FIRST_UPDATE_DATE")
                        .usingGeneratedKeyColumns("ID");

        this.updateStudyReport = new StudyReportUpdate(jdbcTemplate);

        this.insertAssociationReport =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("ASSOCIATION_REPORT")
                        .usingColumns("ASSOCIATION_ID")
                        .usingColumns("LAST_UPDATE_DATE")
                        .usingColumns("GENE_ERROR")
                        .usingColumns("SNP_ERROR")
                        .usingColumns("SNP_GENE_ON_DIFF_CHR")
                        .usingColumns("NO_GENE_FOR_SYMBOL")
                        .usingColumns("GENE_NOT_ON_GENOME")
                        .usingGeneratedKeyColumns("ID");

        this.updateAssociationReport = new AssociationReportUpdate(jdbcTemplate);
    }

    public void loadNCBIMappedData(String[][] data) {

        // Create a map of col number to header
        Map<Integer, String> colNumHeaderMap = new HashMap<>();
        Integer colNum = 0;

        for (String[] header : data) {
            for (String cell : header) {
                colNumHeaderMap.put(colNum, cell);
                colNum++;
            }
            // Break after first line, as this is all we need to establish header
            break;
        }

        Map<CatalogHeaderBinding, Integer> headersToExtract = mapHeader(colNumHeaderMap);
        mapData(headersToExtract, extractRange(data, 1));
    }

    private Map<CatalogHeaderBinding, Integer> mapHeader(Map<Integer, String> colNumHeaderMap) {
        Map<CatalogHeaderBinding, Integer> result = new HashMap<>();
        for (int i : colNumHeaderMap.keySet()) {
            for (CatalogHeaderBinding binding : CatalogHeaderBindings.getLoadHeaders()) {
                if (binding.getLoadName().toUpperCase().equals(colNumHeaderMap.get(i).toUpperCase())) {
                    result.put(binding, i);
                    break;
                }
            }
        }
        return result;
    }

    private void mapData(Map<CatalogHeaderBinding, Integer> headerColumnMap, String[][] data) {
        // 2014-08-01 00:00:00.000
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // Read through each line
        int row = 0;
        boolean caughtErrors = false;
        for (String[] line : data) {
            row++;
            // Study report attributes
            Long studyId = null; // STUDY_ID
            Integer pubmedIdError = null;  // PUBMED_ID_ERROR
            String ncbiPaperTitle = null; // NCBI_PAPER_TITLE
            String ncbiFirstAuthor = null; // NCBI_FIRST_AUTHOR
            String ncbiNormalisedFirstAuthor = null; // NCBI_NORMALIZED_FIRST_AUTHOR
            Date ncbiFirstUpdateDate = null; // NCBI_FIRST_UPDATE_DATE

            // Association report attributes
            Long associationId = null; // ASSOCIATION_ID
            Date lastUpdateDate = null; // LAST_UPDATE_DATE
            Long geneError = null; // GENE_ERROR
            String snpError = null; // SNP_ERROR
            String snpGeneOnDiffChr = null; // SNP_GENE_ON_DIFF_CHR
            String noGeneForSymbol = null; // NO_GENE_FOR_SYMBOL
            String geneNotOnGenome = null; // GENE_NOT_ON_GENOME

            // mapped genetic info
            String region = null; // REGION
            String chromosomeName = null; // CHROMOSOME_NAME
            String chromosomePosition = null; // CHROMOSOME_POSITION
            String upstreamMappedGene = null; // UPSTREAM_MAPPED_GENE
            String upstreamEntrezGeneId = null; // UPSTREAM_ENTREZ_GENE_ID
            Integer upstreamGeneDistance = null; // UPSTREAM_GENE_DISTANCE
            String downstreamMappedGene = null; // DOWNSTREAM_MAPPED_GENE
            String downstreamEntrezGeneId = null; // DOWNSTREAM_ENTREZ_GENE_ID
            Integer downstreamGeneDistance = null; // DOWNSTREAM_GENE_DISTANCE
            Boolean isIntergenic = null; // IS_INTERGENIC

            // For each key in our map, extract the cell at that index
            for (CatalogHeaderBinding binding : headerColumnMap.keySet()) {
                try {
                    String valueToInsert = line[headerColumnMap.get(binding)];
                    switch (binding) {
                        case STUDY_ID:
                            studyId = Long.valueOf(valueToInsert);
                            break;
                        case PUBMED_ID_ERROR:
                            pubmedIdError = Integer.valueOf(valueToInsert);
                            break;
                        case NCBI_PAPER_TITLE:
                            ncbiPaperTitle = valueToInsert;
                            break;
                        case NCBI_FIRST_AUTHOR:
                            ncbiFirstAuthor = valueToInsert;
                            break;
                        case NCBI_NORMALISED_FIRST_AUTHOR:
                            ncbiNormalisedFirstAuthor = valueToInsert;
                            break;
                        case NCBI_FIRST_UPDATE_DATE:
                            ncbiFirstUpdateDate = df.parse(valueToInsert);
                            break;
                        case ASSOCIATION_ID:
                            associationId = Long.valueOf(valueToInsert);
                            break;
                        case GENE_ERROR:
                            geneError = Long.valueOf(valueToInsert);
                            break;
                        case SNP_ERROR:
                            snpError = valueToInsert;
                            break;
                        case SNP_GENE_ON_DIFF_CHR:
                            snpGeneOnDiffChr = valueToInsert;
                            break;
                        case NO_GENE_FOR_SYMBOL:
                            noGeneForSymbol = valueToInsert;
                            break;
                        case GENE_NOT_ON_GENOME:
                            geneNotOnGenome = valueToInsert;
                            break;
                        case REGION:
                            region = valueToInsert;
                            break;
                        case CHROMOSOME_NAME:
                            chromosomeName = valueToInsert;
                            break;
                        case CHROMOSOME_POSITION:
                            chromosomePosition = valueToInsert;
                            break;
                        case UPSTREAM_MAPPED_GENE:
                            upstreamMappedGene = valueToInsert;
                            break;
                        case UPSTREAM_ENTREZ_GENE_ID:
                            upstreamEntrezGeneId = valueToInsert;
                            break;
                        case UPSTREAM_GENE_DISTANCE:
                            upstreamGeneDistance = Integer.valueOf(valueToInsert);
                            break;
                        case DOWNSTREAM_MAPPED_GENE:
                            downstreamMappedGene = valueToInsert;
                            break;
                        case DOWNSTREAM_ENTREZ_GENE_ID:
                            downstreamEntrezGeneId = valueToInsert;
                            break;
                        case DOWNSTREAM_GENE_DISTANCE:
                            downstreamGeneDistance = Integer.valueOf(valueToInsert);
                            break;
                        case IS_INTERGENIC:
                            isIntergenic = Boolean.valueOf(valueToInsert);
                            break;
                        default:
                            throw new DataImportException(
                                    "Unrecognised column flagged for import: " + binding.getLoadName());
                    }

                }
                catch (ParseException e) {
                    getLog().error("Unable to parse date at row " + row, e);
                    caughtErrors = true;
                }
                catch (Exception e) {
                    getLog().error("Unable to insert data at row " + row, e);
                    caughtErrors = true;
                }
            }

            // Once you have all bits for a study report, association report add them
            addStudyReport(studyId,
                           pubmedIdError,
                           ncbiPaperTitle,
                           ncbiFirstAuthor,
                           ncbiNormalisedFirstAuthor,
                           ncbiFirstUpdateDate);
            addAssociationReport(associationId,
                                 lastUpdateDate,
                                 geneError,
                                 snpError,
                                 snpGeneOnDiffChr,
                                 noGeneForSymbol,
                                 geneNotOnGenome);
            addMappedData(studyId,
                          associationId,
                          region,
                          chromosomeName,
                          chromosomePosition,
                          upstreamMappedGene,
                          upstreamEntrezGeneId,
                          upstreamGeneDistance,
                          downstreamMappedGene,
                          downstreamEntrezGeneId,
                          downstreamGeneDistance,
                          isIntergenic);

        }


        if (caughtErrors) {
            throw new DataImportException("Caught errors whilst processing data import - " +
                                                  "please check the logs for more information");
        }
    }

    private void addStudyReport(Long studyId,
                                Integer pubmedIdError,
                                String ncbiPaperTitle,
                                String ncbiFirstAuthor,
                                String ncbiNormalisedFirstAuthor, Date ncbiFirstUpdateDate) {


        // Check for an existing id in database
        Long studyReportIdInDatabase = jdbcTemplate.queryForObject(SELECT_STUDY_REPORTS, Long.class, studyId);

        Map<String, Object> studyArgs = new HashMap<>();
        studyArgs.put("STUDY_ID", studyId);
        studyArgs.put("PUBMED_ID_ERROR", pubmedIdError);
        studyArgs.put("NCBI_PAPER_TITLE", ncbiPaperTitle);
        studyArgs.put("NCBI_FIRST_AUTHOR", ncbiFirstAuthor);
        studyArgs.put("NCBI_NORMALISED_FIRST_AUTHOR", ncbiNormalisedFirstAuthor);
        studyArgs.put("NCBI_FIRST_UPDATE_DATE", ncbiFirstUpdateDate);

        if (studyReportIdInDatabase != null) {
            insertStudyReport.execute(studyArgs);
        }

        else {
            studyArgs.put("ID", studyReportIdInDatabase);
            updateStudyReport.updateByNamedParam(studyArgs);
        }


    }

    private void addAssociationReport(Long associationId,
                                      Date lastUpdateDate,
                                      Long geneError,
                                      String snpError,
                                      String snpGeneOnDiffChr,
                                      String noGeneForSymbol,
                                      String geneNotOnGenome) {

        Map<String, Object> associationArgs = new HashMap<>();
        associationArgs.put("ASSOCIATION_ID", associationId);
        associationArgs.put("LAST_UPDATE_DATE", lastUpdateDate);
        associationArgs.put("GENE_ERROR", geneError);
        associationArgs.put("SNP_ERROR", snpError);
        associationArgs.put("SNP_GENE_ON_DIFF_CHR", snpGeneOnDiffChr);
        associationArgs.put("NO_GENE_FOR_SYMBOL", noGeneForSymbol);
        associationArgs.put("GENE_NOT_ON_GENOME", geneNotOnGenome);
        insertAssociationReport.execute(associationArgs);


    }

    private void addMappedData(Long studyId,
                               Long associationId,
                               String region,
                               String chromosomeName,
                               String chromosomePosition,
                               String upstreamMappedGene,
                               String upstreamEntrezGeneId,
                               Integer upstreamGeneDistance,
                               String downstreamMappedGene,
                               String downstreamEntrezGeneId,
                               Integer downstreamGeneDistance, Boolean isIntergenic) {


    }

    private static <T> T[] extractRange(T[] array, int startIndex) {
        if (startIndex > array.length) {
            return (T[]) Array.newInstance(array.getClass().getComponentType(), 0);
        }
        else {
            T[] response = (T[]) Array.newInstance(
                    array.getClass().getComponentType(),
                    array.length - startIndex);

            System.arraycopy(array, startIndex, response, 0, response.length);
            return response;
        }
    }

    private class StudyReportUpdate extends SqlUpdate {
        private StudyReportUpdate(JdbcTemplate jdbcTemplate) {
            setJdbcTemplate(jdbcTemplate);
            setSql("UPDATE STUDY_REPORT SET " +
                           "STUDY_ID = ?, " +
                           "PUBMED_ID_ERROR = ?, " +
                           "NCBI_PAPER_TITLE = ?, " +
                           "NCBI_FIRST_AUTHOR = ?, " +
                           "NCBI_NORMALIZED_FIRST_AUTHOR = ?, " +
                           "NCBI_FIRST_UPDATE_DATE = ?" +
                           "WHERE ID = ?");
            declareParameter(new SqlParameter("studyId", Types.NUMERIC));
            declareParameter(new SqlParameter("pubmedIdError", Types.NUMERIC));
            declareParameter(new SqlParameter("id", Types.NUMERIC));
            // ... declare more parameters!
            compile();
        }
    }

    private class AssociationReportUpdate extends SqlUpdate {
        public AssociationReportUpdate(JdbcTemplate jdbcTemplate) {
            //        Long associationId = null; // ASSOCIATION_ID
            //        Date lastUpdateDate = null; // LAST_UPDATE_DATE
            //        Long geneError = null; // GENE_ERROR
            //        String snpError = null; // SNP_ERROR
            //        String snpGeneOnDiffChr = null; // SNP_GENE_ON_DIFF_CHR
            //        String noGeneForSymbol = null; // NO_GENE_FOR_SYMBOL
            //        String geneNotOnGenome = null; // GENE_NOT_ON_GENOME
            setJdbcTemplate(jdbcTemplate);
            setSql("UPDATE ASSOCIATION_REPORT SET " +
                           "ASSOCIATION_ID = ?, " +
                           "LAST_UPDATE_DATE = ?, " +
                           "GENE_ERROR = ?, " +
                           "SNP_ERROR = ?, " +
                           "SNP_GENE_ON_DIFF_CHR = ?, " +
                           "NO_GENE_FOR_SYMBOL = ?, " +
                           "GENE_NOT_ON_GENOME = ?, " +
                           "WHERE ID = ?");
            declareParameter(new SqlParameter("associationId", Types.NUMERIC));
            declareParameter(new SqlParameter("lastUpdateDate", Types.NUMERIC));
            declareParameter(new SqlParameter("id", Types.NUMERIC));
            // ... declare more parameters!
            compile();
        }
    }
}