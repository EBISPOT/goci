package uk.ac.ebi.spot.goci.repository;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.goci.model.CatalogHeaderBinding;

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

        mapHeader(colNumHeaderMap, data);

    }

    private void mapHeader(Map<Integer, String> colNumHeaderMap, String[][] data) {

        Map<CatalogHeaderBinding, Integer> testMap = new HashMap<>();
        Integer colNum = 23;
        testMap.put(CatalogHeaderBinding.STUDY_ID, colNum);
        mapData(testMap, data);
    }

    private void mapData(Map<CatalogHeaderBinding, Integer> headerBindingIntegerMap, String[][] data) {

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
            for (Map.Entry<CatalogHeaderBinding, Integer> entry : headerBindingIntegerMap.entrySet()) {
                String valueToInsert = line[entry.getValue()];
                CatalogHeaderBinding databaseColName = entry.getKey();

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

    private void addAssociationReport() {}
}