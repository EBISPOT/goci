package uk.ac.ebi.spot.goci.repository;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.exception.DataImportException;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBindings;

import java.lang.reflect.Array;
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
        // Read through each line
        for (String[] line : data) {
            // Study report attributes
            Long studyId = null; // STUDY_ID
            Integer pubmedIdError = null;  // PUBMED_ID_ERROR
            String ncbiPaperTitle = null; // ??
            String ncbiFirstAuthor = null; // ??
            String ncbiNormalisedFirstAuthor = null; // ??
            Date ncbiFirstUpdateDate = null; // ??

            // Association report attributes
            Long associationId = null; // ASSOCIATION_ID
            Boolean snpPending = null; // ??
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
                String valueToInsert = line[headerColumnMap.get(binding)];
                switch (binding) {
                    case STUDY_ID:
                        studyId = Long.valueOf(valueToInsert);
                        break;
                    case PUBMED_ID_ERROR:
                        pubmedIdError = Integer.valueOf(valueToInsert);
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
                // Once you have all bits for a study report, association report add them
                addStudyReport(studyId,
                               pubmedIdError,
                               ncbiPaperTitle,
                               ncbiFirstAuthor,
                               ncbiNormalisedFirstAuthor,
                               ncbiFirstUpdateDate);
                addAssociationReport(associationId,
                                     snpPending,
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
        }
    }

    private void addStudyReport(Long studyId,
                                Integer pubmedIdError,
                                String ncbiPaperTitle,
                                String ncbiFirstAuthor,
                                String ncbiNormalisedFirstAuthor, Date ncbiFirstUpdateDate) {


    }

    private void addAssociationReport(Long associationId,
                                      Boolean snpPending,
                                      Date lastUpdateDate,
                                      Long geneError,
                                      String snpError,
                                      String snpGeneOnDiffChr,
                                      String noGeneForSymbol,
                                      String geneNotOnGenome) {


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
}