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
                    initialSampleSize += buildDescription(sampleDto);
                } else if (sampleDto.getStage().equalsIgnoreCase("Replication")) {
                    ancestry.setType("replication");
                    replicateSampleSize += buildDescription(sampleDto);
                }else{
                    studyNote.append("unknown ancestry type: " + sampleDto.getStage());
                }
                ancestry.setDescription(sampleDto.getAncestryDescription());
                List<Country> countryList = new ArrayList<>();
                String countryRecruitment = sampleDto.getCountryRecruitement();
                if(countryRecruitment != null){
                    String[] countries = countryRecruitment.split("\\|");
                    for(String country: countries){
                        countryList.add(countryRepository.findByCountryName(country));

                    }
                }
                ancestry.setCountryOfRecruitment(countryList);
                if(sampleDto.getSize() != -1) {
                    ancestry.setNumberOfIndividuals(sampleDto.getSize());
                }
                ancestralGroupRepository.findByAncestralGroup(sampleDto.getAncestry());
                sampleDto.getAncestryCategory();
                String ancestryStr = sampleDto.getAncestry();
                String ancestryCat = sampleDto.getAncestryCategory();
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
                ancestry.setDescription(sampleDto.getAncestry());
                ancestryRepository.save(ancestry);
                AncestryExtension ancestryExtension = new AncestryExtension();
                ancestryExtension.setAncestry(ancestry);
                ancestryExtension.setAncestryDescriptor(sampleDto.getAncestryDescription());
                extensionRepository.save(ancestryExtension);
                ancestry.setAncestryExtension(ancestryExtension);
                ancestryRepository.save(ancestry);
            }
        }
        studyNote.append(initialSampleSize + "\n");
        studyNote.append(replicateSampleSize + "\n");
        study.setInitialSampleSize(initialSampleSize.trim());
        study.setReplicateSampleSize(replicateSampleSize.trim());
        return studyNote.toString();
    }

    private String buildDescription(DepositionSampleDto sampleDto){
        String ancestry = null;
        if(sampleDto.getAncestry() != null && !sampleDto.getAncestry().contains("|")){
            ancestry = sampleDto.getAncestry();

        }else{
            ancestry = sampleDto.getAncestryCategory();
        }
        if(sampleDto.getCases() != null && sampleDto.getControls() != null){
            return sampleDto.getCases() + " " + ancestry + " ancestry cases, "
                    + sampleDto.getControls() + " " + ancestry + " ancestry controls\n";
        }else if(sampleDto.getSize() != -1){
            return sampleDto.getSize() + " " + ancestry + " individuals";
        }else{
            return ancestry + " individuals";
        }
    }

}
