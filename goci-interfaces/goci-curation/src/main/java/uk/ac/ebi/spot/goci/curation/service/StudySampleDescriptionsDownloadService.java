package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudySampleDescription;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Created by emma on 27/04/2015.
 *
 * @author emma
 *         <p>
 *         This is a service class to process a set of study sample descriptions and output the result to a tsv
 *         file. Based on AssociationDownloadService.java
 */

@Service
public class StudySampleDescriptionsDownloadService {

    // Constructor
    public StudySampleDescriptionsDownloadService() {
    }

    public void createDownloadFile(OutputStream outputStream,
                                   Collection<StudySampleDescription> studySampleDescriptions)
            throws IOException {

        String file = processStudySampleDescriptions(studySampleDescriptions);

        // Write file
        outputStream.write(file.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();

    }

    private String processStudySampleDescriptions(Collection<StudySampleDescription> studySampleDescriptions) {

        String header =
                "Study ID\tAuthor\tPublication Date\tPubmed ID\tInitial Sample Description\tReplication Sample Description\tType\tNumber of Individuals\tEthnic Group\tCountry of Origin\tCountry of Recruitment\tAdditional Description\tSample Sizes Match\tEthnicty Checked Level One\tEthnicty Checked Level Two\tNotes\n";


        StringBuilder output = new StringBuilder();
        output.append(header);

        for (StudySampleDescription studySampleDescription : studySampleDescriptions) {
            StringBuilder line = new StringBuilder();

            // Study ID
            if (studySampleDescription.getStudyId() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getStudyId());

            }
            line.append("\t");


            // Author
            if (studySampleDescription.getAuthor() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getAuthor());

            }
            line.append("\t");

            // Publication Date
            if (studySampleDescription.getPublicationDate() == null) {
                line.append("");
            } else {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String studyDate = dateFormat.format(studySampleDescription.getPublicationDate());
                line.append(studyDate);
            }
            line.append("\t");

            // Pubmed ID
            if (studySampleDescription.getPubmedId() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getPubmedId());
            }
            line.append("\t");

            // Initial sample size
            String initialSampleSize = studySampleDescription.getInitialSampleSize();
            if (initialSampleSize == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(initialSampleSize));
            }
            line.append("\t");

            // Replicate sample size
            String replicateSampleSize = studySampleDescription.getReplicateSampleSize();
            if (replicateSampleSize == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(replicateSampleSize));
            }
            line.append("\t");

            // Type
            if (studySampleDescription.getType() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getType());
            }
            line.append("\t");

            // Number of individuals
            if (studySampleDescription.getNumberOfIndividuals() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getNumberOfIndividuals());
            }
            line.append("\t");

            // Ethnic group
            String ethnicGroup = studySampleDescription.getEthnicGroup();
            if (ethnicGroup == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(ethnicGroup));
            }
            line.append("\t");

            // Origin
            String countryOfOrigin = studySampleDescription.getCountryOfOrigin();
            if (countryOfOrigin == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(countryOfOrigin));
            }
            line.append("\t");

            // Recruitment
            String countryOfRecruitment = studySampleDescription.getCountryOfRecruitment();
            if (countryOfRecruitment == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(countryOfRecruitment));
            }
            line.append("\t");

            // Description , this requires some tidying
            String description = studySampleDescription.getDescription();
            if (description == null) {
                line.append("");
            } else {
                String newline = System.getProperty("line.separator");
                if (description.equals(newline)) {
                    line.append("");
                } else {
                    line.append(tidyStringForOutput(description));
                }
            }
            line.append("\t");

            // Sample size
            if (studySampleDescription.getSampleSizesMatch() == null) {
                line.append("");
            } else {
                line.append(studySampleDescription.getSampleSizesMatch());
            }
            line.append("\t");

            // Housekeeping information
            if (studySampleDescription.isEthnicityCheckedLevelOne() == null) {
                line.append("");
            } else {
                if (studySampleDescription.isEthnicityCheckedLevelOne()) {
                    line.append("Y");
                } else {
                    line.append("N");
                }
            }
            line.append("\t");

            if (studySampleDescription.isEthnicityCheckedLevelTwo() == null) {
                line.append("");
            } else {
                if (studySampleDescription.isEthnicityCheckedLevelTwo()) {
                    line.append("Y");
                } else {
                    line.append("N");
                }
            }
            line.append("\t");


            // Notes, this requires some tidying
            String notes = studySampleDescription.getNotes();
            if (notes == null) {
                line.append("");
            } else {
                line.append(tidyStringForOutput(notes));
            }

            // Add new line
            line.append("\n");
            output.append(line.toString());

        }

        return output.toString();
    }

    private String tidyStringForOutput(String output) {
        // Remove new lines or carriage returns in value
        String newline = System.getProperty("line.separator");
        if (output.contains(newline)) {
            output = output.replaceAll("\n", " ").replaceAll("\r", " ");
        }
        output = output.trim();
        return output;
    }

}
