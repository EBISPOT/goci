package uk.ac.ebi.spot.goci.curation.cron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.goci.curation.caching.CacheRunner;
import uk.ac.ebi.spot.goci.curation.controller.assembler.DiseaseTraitDtoAssembler;
import uk.ac.ebi.spot.goci.curation.controller.assembler.EFOTraitAssembler;
import uk.ac.ebi.spot.goci.curation.service.EfoTraitService;
import uk.ac.ebi.spot.goci.model.Association;
import uk.ac.ebi.spot.goci.model.Study;
import uk.ac.ebi.spot.goci.model.deposition.DiseaseTraitDto;
import uk.ac.ebi.spot.goci.model.deposition.EFOTraitDTO;
import uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.repository.AssociationRepository;
import uk.ac.ebi.spot.goci.repository.DiseaseTraitRepository;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;
import uk.ac.ebi.spot.goci.repository.StudyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SyncReportedTraitsTask {

    private static final Logger log = LoggerFactory.getLogger(SyncReportedTraitsTask.class);

    @Autowired
    DiseaseTraitService diseaseTraitService;
    @Autowired
    EfoTraitService efoTraitService;
    @Autowired
    DiseaseTraitRepository diseaseTraitRepository;
    @Autowired
    EfoTraitRepository efoTraitRepository;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    AssociationRepository associationRepository;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CacheRunner cacheRunner;

    @Value("${deposition.ingest.uri}")
    private String depositionIngestURL;
    @Value("${deposition.ingest.diseaseTraits.uri}")
    private String diseaseTraitsUri;

    @Value("${deposition.ingest.efoTraits.uri}")
    private String efoTraitsUri;

    @Scheduled(cron = "0 00 * * * *")
    public void syncDiseaseTraits() {

        String endpoint = depositionIngestURL + diseaseTraitsUri;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<DiseaseTraitDto>> diseaseTraitDtos = restTemplate.exchange(endpoint,
                HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<DiseaseTraitDto>>() {
                });
        log.info("Size of Disease Trait response from Ingest ->" + diseaseTraitDtos.getBody().size());


        Optional.of(diseaseTraitDtos).ifPresent(entity -> entity.getBody()
                .forEach(diseaseTraitDto -> {
                    try {
                        Optional<DiseaseTrait> optionalDiseaseTrait = diseaseTraitRepository.findByMongoSeqId(diseaseTraitDto.getMongoSeqId());
                        //Optional<DiseaseTrait> optionalDiseaseTrait = diseaseTraitRepository.findByTraitIgnoreCase(diseaseTraitDto.getTrait());

                        if (optionalDiseaseTrait.isPresent()) {
                            log.info("Disease Trait Synced from Depo-Curation -: {}", optionalDiseaseTrait.get().getTrait());
                            DiseaseTrait diseaseTrait = optionalDiseaseTrait.get();
                            if (!diseaseTraitDto.getTrait().equalsIgnoreCase(diseaseTrait.getTrait())) {
                                diseaseTrait.setTrait(diseaseTraitDto.getTrait());
                                //diseaseTrait.setMongoSeqId(diseaseTraitDto.getMongoSeqId());
                                diseaseTraitService.syncDiseaseTraitMongoSeqId(diseaseTrait);
                            }
                        } else {
                            log.info("Disease Trait added from Depo-Curation -: {}", diseaseTraitDto.getTrait());
                            DiseaseTrait diseaseTrait = DiseaseTraitDtoAssembler.disassemble(diseaseTraitDto);
                            diseaseTraitService.createDiseaseTrait(diseaseTrait);
                        }
                    } catch(Exception ex) {
                        log.error("Exception with SeqId -: {} and Reported trait -: {}",diseaseTraitDto.getMongoSeqId(),diseaseTraitDto.getTrait());
                        log.error("Exception in syncDiseaseTraits Insert or Update ()"+ex.getMessage(),ex );
                    }

                }));

        List<DiseaseTraitDto> dtos = diseaseTraitDtos.getBody();
        List<String> mongoSeqIdList = dtos.stream().map(DiseaseTraitDto::getMongoSeqId).collect(Collectors.toList());

        List<String> mongoSeqIdsDeleted = diseaseTraitRepository.findAll()
                .stream()
                .map(DiseaseTrait::getMongoSeqId)
                .filter(seqId -> seqId != null)
                .filter(seqId -> !mongoSeqIdList.contains(seqId))
                .collect(Collectors.toList());

        mongoSeqIdsDeleted.forEach((seqId) -> {
            try {
                log.info("Mongo Ids to be deleted {}", seqId);
                diseaseTraitRepository.findByMongoSeqId(seqId).ifPresent((diseaseTrait) -> {
                    log.info("Trait which is deleted is {}", diseaseTrait.getTrait());
                    studyRepository.findByDiseaseTraitId(diseaseTrait.getId()).forEach(study -> {
                        study.setDiseaseTrait(null);
                        study.setBackgroundTrait(null);
                        studyRepository.save(study);
                    });
                    diseaseTraitService.delete(diseaseTrait);
                });
            } catch(Exception ex) {
                log.error("Exception with SeqId -: {} ",seqId);
                log.error("Exception in syncDiseaseTraits Delete ()"+ex.getMessage(),ex );
            }
        });
    }

    @Scheduled(cron = "0 05 * * * *")
    @Transactional(propagation = Propagation.SUPPORTS)
    public void syncEFOTraits() {
        String endpoint = depositionIngestURL + efoTraitsUri;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List<EFOTraitDTO>> efoTraitDtos = restTemplate.exchange(endpoint,
                HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<EFOTraitDTO>>() {
                });
        log.info("Size of EFOTrait response from Ingest ->" + efoTraitDtos.getBody().size());
        Optional.of(efoTraitDtos).ifPresent(entity -> entity.getBody()
                .forEach(efoTraitDto -> {
                    try {
                        EfoTrait efoTrait = efoTraitRepository.findByMongoSeqId(efoTraitDto.getMongoSeqId());
                        //EfoTrait efoTrait = efoTraitRepository.findByTraitIgnoreCase(efoTraitDto.getTrait());
                        if (efoTrait != null) {

                            if (!efoTraitDto.getTrait().equalsIgnoreCase(efoTrait.getTrait()) ||
                                    !efoTraitDto.getShortForm().equalsIgnoreCase(efoTrait.getShortForm()) ||
                                    !efoTraitDto.getUri().equalsIgnoreCase(efoTrait.getUri())) {
                                log.info("EFOTrait Synced from Depo-Curation -: {}", efoTrait.getTrait());
                                efoTrait.setTrait(efoTraitDto.getTrait());
                                efoTrait.setShortForm(efoTraitDto.getShortForm());
                                efoTrait.setUri(efoTraitDto.getUri());
                                //efoTrait.setMongoSeqId(efoTraitDto.getMongoSeqId());
                                efoTraitService.save(efoTrait);
                            }
                        } else {
                            log.info("EFOTrait added from Depo-Curation -: {}", efoTraitDto.getTrait());
                            EfoTrait newEfoTrait = EFOTraitAssembler.disassemble(efoTraitDto);
                            efoTraitService.save(newEfoTrait);
                        }
                    }catch(Exception ex){
                        log.error("Exception with SeqId -: {} and Reported trait -: {}",efoTraitDto.getMongoSeqId(),efoTraitDto.getTrait());
                        log.error("Exception in syncEFOTraits Insert or Update ()"+ex.getMessage(),ex );
                    }
                }));

        List<EFOTraitDTO> dtos = efoTraitDtos.getBody();
        List<String> mongoSeqIds = dtos.stream().map(EFOTraitDTO::getMongoSeqId).collect(Collectors.toList());


        List<String> mongoSeqIdsDeleted = efoTraitRepository.findAll()
                .stream().map(EfoTrait::getMongoSeqId)
                .filter(seqId -> seqId != null)
                .filter(seqId -> !mongoSeqIds.contains(seqId))
                .collect(Collectors.toList());

        mongoSeqIdsDeleted.forEach((seqId) -> {
            try {
                removeStudyEFOMapping(seqId);
            }catch(Exception ex){
                log.error("Exception with SeqId -: {} ",seqId);
                log.error("Exception in syncEFOTraits Delete ()"+ex.getMessage(),ex );

            }
        });

        cacheRunner.runCache(); //Run Cache after every Sync Traits

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeStudyEFOMapping( String seqId) {

        log.info("Mongo Ids to be deleted {}", seqId);
        Optional.ofNullable(efoTraitRepository.findByMongoSeqId(seqId)).ifPresent((efoTrait) -> {

            log.info("Trait which is deleted is {}", efoTrait.getShortForm());
            log.info("Trait ID which is deleted is {}", efoTrait.getId());
            log.info("Size of the study list" +studyRepository.findByEfoTraitsId(efoTrait.getId()).size());
            studyRepository.findByEfoTraitsId(efoTrait.getId()).forEach(study -> {
                //study.setEfoTraits(null);
                log.info("Calling removeStudyEFOMappings()");
                study.getEfoTraits().remove(efoTrait);
                studyRepository.save(study);
            });
            studyRepository.findByMappedBackgroundTraitsId(efoTrait.getId()).forEach(study -> {
                //study.setEfoTraits(null);
                log.info("Calling removeStudyBGEFOMappings()");
                study.getMappedBackgroundTraits().remove(efoTrait);
                studyRepository.save(study);
            });
            associationRepository.findByEfoTraitsId(efoTrait.getId()).forEach(asscn -> {
                //asscn.setEfoTraits(null);
                log.info("Calling removeAsscnEFOMappings()");
                asscn.getEfoTraits().remove(efoTrait);
                associationRepository.save(asscn);
            });
            associationRepository.findByBkgEfoTraitsId(efoTrait.getId()).forEach(asscn -> {
                //asscn.setEfoTraits(null);
                log.info("Calling removeAsscnBGEFOMappings()");
                asscn.getBkgEfoTraits().remove(efoTrait);
                associationRepository.save(asscn);
            });
            efoTraitService.delete(efoTrait);
        });
    }



}
