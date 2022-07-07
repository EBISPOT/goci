package uk.ac.ebi.spot.goci.curation.service.deposition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.goci.model.*;
import uk.ac.ebi.spot.goci.model.deposition.DepositionSampleDto;
import uk.ac.ebi.spot.goci.repository.AncestralGroupRepository;
import uk.ac.ebi.spot.goci.repository.AncestryExtensionRepository;
import uk.ac.ebi.spot.goci.repository.AncestryRepository;
import uk.ac.ebi.spot.goci.repository.CountryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DepositionSampleService {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    @Autowired
    CountryRepository countryRepository;
    @Autowired
    AncestryRepository ancestryRepository;
    @Autowired
    AncestralGroupRepository ancestralGroupRepository;
    @Autowired
    AncestryExtensionRepository extensionRepository;

    public DepositionSampleService() {
    }

    @Transactional
    public String saveSamples(String studyTag, Study study, List<DepositionSampleDto> samples, ImportLog importLog) {
        //find samples in study
        StringBuffer studyNote = new StringBuffer();
        String initialSampleSize = "";
        String replicateSampleSize = "";
        for (DepositionSampleDto sampleDto : samples) {
            if (sampleDto.getStudyTag().equals(studyTag)) {
                ImportLogStep importStep = importLog.addStep(new ImportLogStep("Creating sample", study.getAccessionId()));

                Ancestry ancestry = new Ancestry();
                if (sampleDto.getStage().equalsIgnoreCase("Discovery")) {
                    ancestry.setType("initial");
                    //initialSampleSize += buildDescription(sampleDto) + ", ";
                } else if (sampleDto.getStage().equalsIgnoreCase("Replication")) {
                    ancestry.setType("replication");
                   // replicateSampleSize += buildDescription(sampleDto) + ", ";
                } else {
                    studyNote.append("unknown ancestry type: " + sampleDto.getStage());
                }
                List<Country> countryList = new ArrayList<>();
                String countryRecruitment = sampleDto.getCountryRecruitement();
                if (countryRecruitment != null) {
                    String[] countries = countryRecruitment.split("\\|");
                    Map<String, String> countryMap = new HashMap<>();
                    for (String country : countries) {
                        countryMap.put(country.trim(), "");
                    }

                    for (String country : countryMap.keySet()) {
                        Country cCountry = countryRepository.findByCountryNameIgnoreCase(country);
                        if (cCountry == null) {
                            importLog.addError("Unable to find country: " + country, "Creating sample");
                        } else {
                            countryList.add(cCountry);
                        }
                    }
                }
                ancestry.setCountryOfRecruitment(countryList);
                if (sampleDto.getSize() != -1) {
                    ancestry.setNumberOfIndividuals(sampleDto.getSize());
                }
                ancestralGroupRepository.findByAncestralGroup(sampleDto.getAncestry());
                String ancestryCat = sampleDto.getAncestryCategory();
//                if(ancestryCat != null && !ancestryCat.endsWith("ancestry")){
//                    ancestryCat += " ancestry";
//                }
                List<AncestralGroup> ancestryGroups = new ArrayList<>();
                if (ancestryCat != null) {
                    String[] groups = ancestryCat.split("\\||,");
                    getLog().info("Ancestry groups provided: {}", ancestryCat);
                    for (String group : groups) {
                        AncestralGroup ancestryGroup = ancestralGroupRepository.findByAncestralGroup(group);
                        ancestryGroups.add(ancestryGroup);
                    }
                }
                getLog().info("Ancestry groups mapped: {}", ancestryGroups);
                ancestry.setAncestralGroups(ancestryGroups);
                ancestry.setStudy(study);
                ancestryRepository.save(ancestry);

                AncestryExtension ancestryExtension = new AncestryExtension();
                ancestryExtension.setAncestry(ancestry);
                if (sampleDto.getAncestry() != null) {
                    ancestryExtension.setAncestryDescriptor(sampleDto.getAncestry().replaceAll("\\|", ", "));
                }
                ancestryExtension.setIsolatedPopulation(sampleDto.getAncestryDescription());
                ancestryExtension.setNumberCases(sampleDto.getCases());
                ancestryExtension.setNumberControls(sampleDto.getControls());
                ancestryExtension.setSampleDescription(sampleDto.getSampleDescription());
                extensionRepository.save(ancestryExtension);
                ancestry.setAncestryExtension(ancestryExtension);
                ancestryRepository.save(ancestry);
                importLog.updateStatus(importStep.getId(), ImportLog.SUCCESS);
            }
        }
     /*   initialSampleSize = initialSampleSize.trim();
        replicateSampleSize = replicateSampleSize.trim();
        if (initialSampleSize.endsWith(",")) {
            initialSampleSize = initialSampleSize.substring(0, initialSampleSize.length() - 1);
        }
        if (replicateSampleSize.endsWith(",")) {
            replicateSampleSize = replicateSampleSize.substring(0, replicateSampleSize.length() - 1);
        }
        if (replicateSampleSize.equalsIgnoreCase("")) {
            replicateSampleSize = "NA";
        }*/
        studyNote.append("initial: " + initialSampleSize + "\n");
        studyNote.append("replication: " + replicateSampleSize + "\n");
        //study.setInitialSampleSize(initialSampleSize.trim());
        //study.setReplicateSampleSize(replicateSampleSize.trim());

        return studyNote.toString();
    }

    String buildDescription(DepositionSampleDto sampleDto) {
        String ancestry;
        if (sampleDto.getAncestry() != null) {
            ancestry = sampleDto.getAncestry().trim();
        } else {
            ancestry = sampleDto.getAncestryCategory().trim();
        }
        if (ancestry.trim().equalsIgnoreCase("NR")) {
            if (sampleDto.getCases() != null && sampleDto.getControls() != null && sampleDto.getCases() != 0 && sampleDto.getControls() != 0) {
                ancestry = String.format("%,d", sampleDto.getCases()) + " " + AncestryConstants.CASES + ", " + String.format("%,d", sampleDto.getControls()) + " " + AncestryConstants.CONTROLS;
            } else {
                if (sampleDto.getSize() != -1) {
                    ancestry = String.format("%,d", sampleDto.getSize()) + " " + AncestryConstants.INDIVIDUALS;
                } else {
                    ancestry += " " + AncestryConstants.INDIVIDUALS;
                }
            }
            return ancestry;
        }

        List<String> ancestryList = Arrays.asList(ancestry.split("\\|"));
        ancestry = "";
        for (String entry : ancestryList) {
            String adaptEntry = adaptEntry(entry);

            if (sampleDto.getCases() != null && sampleDto.getControls() != null && sampleDto.getCases() != 0 && sampleDto.getControls() != 0) {
                ancestry += String.format("%,d", sampleDto.getCases()) + " " + adaptEntry + " " + AncestryConstants.CASES + ", " +
                        String.format("%,d", sampleDto.getControls()) + " " + adaptEntry + " " + AncestryConstants.CONTROLS;
            } else {
                ancestry += adaptEntry;
            }

            ancestry += ", ";
        }
        ancestry = ancestry.trim();
        if (ancestry.endsWith(",")) {
            ancestry = ancestry.substring(0, ancestry.length() - 1).trim();
        }
        if (!ancestry.endsWith(AncestryConstants.CONTROLS)) {
            ancestry += " " + AncestryConstants.INDIVIDUALS;
            if (sampleDto.getSize() != -1) {
                ancestry = String.format("%,d", sampleDto.getSize()) + " " + ancestry;
            }
        }

        return ancestry;
    }

    private String adaptEntry(String entry) {
        entry = entry.trim();
        if (entry.contains(AncestryConstants.UNSPECIFIED)) {
            return entry.replace(AncestryConstants.UNSPECIFIED, AncestryConstants.ANCESTRY);
        }
        if (entry.contains(" or ")) {
            return entry;
        }

        if (!Arrays.asList(AncestryConstants.ANCESTRY_CATS).contains(entry.toLowerCase())) {
            entry += " " + AncestryConstants.ANCESTRY;
        }
        return entry;
    }

}
