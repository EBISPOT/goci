package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.model.StudySampleDescription;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by emma on 27/04/2015.
 *
 * @author emma This is a service class to process a set of study sample descriptions and output the result to a tsv
 *         file. Based on AssociationDownloadService.java
 */
@Service
public class StudySampleDescriptionsDownloadService {

    public StudySampleDescriptionsDownloadService() {
    }

    public void createDownloadFile(OutputStream outputStream,
                                   Collection<StudySampleDescription> studySampleDescriptions)
            throws IOException {

        String file = processStudySampleDescriptions(studySampleDescriptions);

        InputStream in = new ByteArrayInputStream(file.getBytes("UTF-8"));

        byte[] outputByte = new byte[4096];
        //copy binary contect to output stream
        while (in.read(outputByte, 0, 4096) != -1) {
            outputStream.write(outputByte, 0, 4096);
        }
        in.close();
        outputStream.flush();

    }

    private String processStudySampleDescriptions(Collection<StudySampleDescription> studySampleDescriptions) {

        String header =
                "Author\tStudy Date\tInitial Sample Description\tReplication Sample Description\tEthnicty Checked Level One\tEthnicty Checked Level Two\tType\tNumber of Individuals\tEthnic Group\tCountry of Origin\tCountry of Recruitment\tAdditional Description\tSample Sizes Match\tNotes\r\n";


        StringBuilder output = new StringBuilder();
        output.append(header);

        for (StudySampleDescription studySampleDescription : studySampleDescriptions) {
            StringBuilder line = new StringBuilder();

            if (studySampleDescription.getAuthor() == null) {
                line.append("");
            }
            else {
                line.append(studySampleDescription.getAuthor());

            }
            line.append("\t");

            if (studySampleDescription.getStudyDate() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getStudyDate());
            }
            line.append("\t");

            String initialSampleSize= studySampleDescription.getInitialSampleSize();
            if (initialSampleSize== null) {
                line.append("");
            }

            else {
                line.append(tidyStringForOutput(initialSampleSize));
            }
            line.append("\t");

            if (studySampleDescription.getReplicateSampleSize() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getReplicateSampleSize());
            }
            line.append("\t");

            if (studySampleDescription.isEthnicityCheckedLevelOne() == null) {
                line.append("");
            }

            else {
                if (studySampleDescription.isEthnicityCheckedLevelOne()) {
                    line.append("Y");
                }

                else {line.append("N");}
            }
            line.append("\t");

            if (studySampleDescription.isEthnicityCheckedLevelTwo() == null) {
                line.append("");
            }

            else {
                if (studySampleDescription.isEthnicityCheckedLevelTwo()) {
                    line.append("Y");
                }

                else {line.append("N");}
            }
            line.append("\t");

            if (studySampleDescription.getType() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getType());
            }
            line.append("\t");

            if (studySampleDescription.getNumberOfIndividuals() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getNumberOfIndividuals());
            }
            line.append("\t");

            if (studySampleDescription.getEthnicGroup() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getEthnicGroup());
            }
            line.append("\t");

            if (studySampleDescription.getCountryOfOrigin() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getCountryOfOrigin());
            }
            line.append("\t");

            if (studySampleDescription.getCountryOfRecruitment() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getCountryOfRecruitment());
            }
            line.append("\t");

            if (studySampleDescription.getDescription() == null) {
                line.append("");
            }

            else {

                String newline = System.getProperty("line.separator");
                if (studySampleDescription.getDescription().equals(newline)) {
                    line.append("");
                }
                else {
                    line.append(studySampleDescription.getDescription());
                }
            }
            line.append("\t");

            if (studySampleDescription.getSampleSizesMatch() == null) {
                line.append("");
            }

            else {
                line.append(studySampleDescription.getSampleSizesMatch());
            }
            line.append("\t");

            // Tidy notes field
            String notes = studySampleDescription.getNotes();
            if (notes == null) {
                line.append("");
            }
            else {
                line.append(tidyStringForOutput(notes));
            }
            line.append("\r\n");
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
