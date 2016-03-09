package uk.ac.ebi.spot.goci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.ArrayInformation;
import uk.ac.ebi.spot.goci.model.Study;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwelter on 08/03/16.
 */
@Service
public class PlatformMappingService {

    private StudyService studyService;
    private List<Study> studies;
    private List<ArrayInformation> platformEntries;
    private List<String> manufacturers;
    private List<String> qualifiers;

    private Logger output = LoggerFactory.getLogger("output");

    protected Logger getOutput() {
        return output;
    }


    @Autowired
    public PlatformMappingService(StudyService studyService){
        this.studyService = studyService;
        manufacturers = new ArrayList<>();
        manufacturers.add("Affymetrix");
        manufacturers.add("Illumina");
        manufacturers.add("Perlegen");

        this.qualifiers = new ArrayList<>();
        qualifiers.add("up to");
        qualifiers.add("at least");
        qualifiers.add("~");
        qualifiers.add(">");
        qualifiers.add("more than");

    }
    public void mapAllValues() {
        this.studies = studyService.findPublishedStudies();

        for(Study study : studies){
            String platform = study.getPlatform();

            if(platform != null){
                List<String> manufacturer = new ArrayList<>();
                boolean imputed = false;
                boolean pooled = false;
                Integer snpCount = null;
                List<String> qualifier = new ArrayList<>();
                String qual = null;
                String comment = null;

                if(platform.equals("NR")){
                    comment = platform;
                }
                else {
                    for(String man : manufacturers){
                        if(platform.contains(man)){
                            manufacturer.add(man);
                        }
                    }

                    if(platform.contains("imputed")){
                        imputed = true;
                    }
                    if(platform.contains("pooled")){
                        pooled = true;
                    }

                    if(platform.contains("[") && platform.indexOf("[") == platform.lastIndexOf("[")) {
                        int start = platform.indexOf("[");
                        int finish = platform.indexOf("]");

                        String count = platform.substring(start + 1, finish);

                        if(!count.equals("NR")){
                             for (String q : qualifiers) {
                                if (count.contains(q)) {
                                    qualifier.add(q);

                                    count = count.replace(q, "");
                                }
                            }

                            if (count.contains("million")) {
                                count = count.replace("million", "");

                                double c = Double.parseDouble(count);

                                snpCount = (int) (c * 1000000);

                            }

                            else if (count.contains(",")) {
                                try {
                                    snpCount = NumberFormat.getNumberInstance(java.util.Locale.US).parse(count).intValue();
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            else {
                                snpCount = Integer.parseInt(count);
                            }
                        }

                    }

                    else {
                        comment = platform;
                    }

                }

                if(qualifier.size() > 1){
                    platform = comment;
                }
                else {
                    qual = qualifier.get(0);
                }

                ArrayInformation info = new ArrayInformation(manufacturer, snpCount, qual, imputed, pooled, comment, study, platform);

                platformEntries.add(info);

            }
        }

        printArrays(platformEntries);
    }

    private void printArrays(List<ArrayInformation> arrayInformation) {

        getOutput().info("Platform\tStudyID\tManufacturers\tQualifier\tSNPcount\tImputer\tPooled\tComment");

        for(ArrayInformation info : arrayInformation){
            String platform = info.getPlatformString();

            String id = info.getStudy().getId().toString();
            String manufacturer = "";

            if(info.getManufacturer().size() != 0){
                 for(String m : info.getManufacturer()){
                     if(manufacturer.equals("")){
                          manufacturer = m;
                     }
                     else {
                         manufacturer = manufacturer.concat(" & ").concat(m);
                     }
                 }
            }

            String q = "";

            if(info.getQualifier() != null){
                q = info.getQualifier();
            }

            String count = "NR";
            if(info.getSnps() != null){
                 count = info.getSnps().toString();
            }

            String comment = "";
            if(info.getComment() != null){
                comment = info.getComment();
            }

            String imputed = "";

            if(info.isImputed()) {
                imputed = "imputed";
            }

            String pooled = "";

            if(info.isPooled()){
                pooled = "pooled";
            }

            getOutput().info(platform + "\t" + id + "\t" + manufacturer  + "\t" + q  + "\t" + count  + "\t" + imputed  + "\t" + pooled  + "\t" + comment);
        }
    }

}
