package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;
import uk.ac.ebi.spot.goci.repository.AncestralGroupRepository;
import uk.ac.ebi.spot.goci.repository.AncestryExtensionRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.CountryRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class DepositionSampleService {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    AncestryRepository ancestryRepository;
    @Autowired
    AncestralGroupRepository ancestralGroupRepository;
    @Autowired
    AncestryExtensionRepository extensionRepository;

    public DepositionSampleService(){}
    public String saveSamples(SecureUser currentUser, String studyTag, Study study, List<DepositionSampleDto> samples){
        //find samples in study
        StringBuffer studyNote = new StringBuffer();
        String initialSampleSize = "";
        String replicateSampleSize = "";
        for (DepositionSampleDto sampleDto : samples) {
            if (sampleDto.getStudyTag().equals(studyTag)) {
                Ancestry ancestry = new Ancestry();
                if (sampleDto.getStage().equalsIgnoreCase("Discovery")) {
                    ancestry.setType("initial");
                    initialSampleSize += buildDescription(sampleDto) + ", ";
                } else if (sampleDto.getStage().equalsIgnoreCase("Replication")) {
                    ancestry.setType("replication");
                    replicateSampleSize += buildDescription(sampleDto) + ", ";
                }else{
                    studyNote.append("unknown ancestry type: " + sampleDto.getStage());
                }
                ancestry.setDescription(sampleDto.getAncestryDescription());
                List<Country> countryList = new ArrayList<>();
                String countryRecruitment = sampleDto.getCountryRecruitement();
                if(countryRecruitment != null){
                    String[] countries = countryRecruitment.split("\\|");
                    for(String country: countries){
                        countryList.add(countryRepository.findByCountryNameIgnoreCase(country.trim()));

                    }
                }
                ancestry.setCountryOfRecruitment(countryList);
                if(sampleDto.getSize() != -1) {
                    ancestry.setNumberOfIndividuals(sampleDto.getSize());
                }
                ancestralGroupRepository.findByAncestralGroup(sampleDto.getAncestry());
                String ancestryStr = sampleDto.getAncestry();
                String ancestryCat = sampleDto.getAncestryCategory();
//                if(ancestryCat != null && !ancestryCat.endsWith("ancestry")){
//                    ancestryCat += " ancestry";
//                }
                AncestralGroup ancestryGroup = null;
                if(ancestryStr != null){
                    ancestryGroup = ancestralGroupRepository.findByAncestralGroup(ancestryStr);
                }else{
                    ancestryGroup = ancestralGroupRepository.findByAncestralGroup(ancestryCat);
                }
                List<AncestralGroup> ancestryGroups = new ArrayList<>();
                ancestryGroups.add(ancestryGroup);
                ancestry.setAncestralGroups(ancestryGroups);
                ancestry.setStudy(study);
                if(sampleDto.getAncestry() != null) {
                    ancestry.setDescription(sampleDto.getAncestry().replaceAll("\\|", ", "));
                }
                ancestryRepository.save(ancestry);
                AncestryExtension ancestryExtension = new AncestryExtension();
                ancestryExtension.setAncestry(ancestry);
                ancestryExtension.setIsolatedPopulation(sampleDto.getAncestryDescription());
                ancestryExtension.setNumberCases(sampleDto.getCases());
                ancestryExtension.setNumberControls(sampleDto.getControls());
                ancestryExtension.setSampleDescription(sampleDto.getSampleDescription());
                extensionRepository.save(ancestryExtension);
                ancestry.setAncestryExtension(ancestryExtension);
                ancestryRepository.save(ancestry);
            }
        }
        if(initialSampleSize.endsWith(", "))
        {
            initialSampleSize = initialSampleSize.substring(0, initialSampleSize.length() - 2);
        }
        if(replicateSampleSize.endsWith(", "))
        {
            replicateSampleSize = replicateSampleSize.substring(0, replicateSampleSize.length() - 2);
        }
        studyNote.append("initial: " + initialSampleSize + "\n");
        studyNote.append("replication: " + replicateSampleSize + "\n");
        study.setInitialSampleSize(initialSampleSize.trim());
        study.setReplicateSampleSize(replicateSampleSize.trim());
        return studyNote.toString();
    }

    String buildDescription(DepositionSampleDto sampleDto){
        String ancestry = null;
        if(sampleDto.getAncestry() != null){
            ancestry = sampleDto.getAncestry().replaceAll("\\|", ", ");
        }
        else{
            ancestry = sampleDto.getAncestryCategory();
        }
        if(ancestry.toString().equalsIgnoreCase("nr")) {
            if (sampleDto.getCases() != null && sampleDto.getControls() != null) {
                ancestry = String.format("%,d", sampleDto.getCases()) + " " + ancestry + " cases, " +
                        String.format("%,d", sampleDto.getControls()) + " " + ancestry + " controls";
            } else if (sampleDto.getSize() != -1) {
                 ancestry = String.format("%,d", sampleDto.getSize()) + " " + ancestry + " individuals";
            } else {
                ancestry += " individuals";
            }
        }else{
            if (sampleDto.getCases() != null && sampleDto.getControls() != null) {
                ancestry = String.format("%,d", sampleDto.getCases()) + " " + ancestry + " ancestry cases, " +
                        String.format("%,d", sampleDto.getControls()) + " " + ancestry + " ancestry controls";
            } else if (sampleDto.getSize() != -1) {
                ancestry = String.format("%,d", sampleDto.getSize()) + " " + ancestry + " ancestry individuals";
            } else {
                ancestry += " ancestry individuals";
            }
        }
        return ancestry;
    }

}
