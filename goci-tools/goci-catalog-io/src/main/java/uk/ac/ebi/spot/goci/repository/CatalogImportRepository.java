package uk.ac.ebi.spot.goci.repository;

import org.springframework.stereotype.Repository;
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
            Long studyId;
            Integer pubmedIdErrorStudy;
            String ncbiPaperTitle;
            String ncbiFirstAuthor;
            String ncbiNormalisedFirstAuthor;
            Date ncbiFrstUpdateDate;

            // Association report attributes
            Long associationId;
            Boolean snpPending;
            Date lastUpdateDate;
            Integer geneError;
            Integer pubmedIdErrorAss;
            String snpError;

            // For each key in our map, extract the cell at that index
            for (CatalogHeaderBinding binding : headerColumnMap.keySet()) {
                String valueToInsert = line[headerColumnMap.get(binding)];
                switch (binding) {
                    case STUDY_ID:
                        studyId = Long.valueOf(valueToInsert);
                        break;
                    case PUBMED_ID_ERROR:
                        pubmedIdErrorStudy = Integer.valueOf(valueToInsert);
                        break;
                    case

                }

                if (databaseColName.equals(CatalogHeaderBinding.STUDY_ID)) {

                    studyId = Long.valueOf(valueToInsert);

                }

            }

            // Once you have all bits for a study report, association report add them
            addStudyReport();
        }
    }

    private void addStudyReport() {


    }

    private void addAssociationReport() {


    }

    private void addMappedData() {

    }

    public static <T> T[] extractRange(T[] array, int startIndex) {
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