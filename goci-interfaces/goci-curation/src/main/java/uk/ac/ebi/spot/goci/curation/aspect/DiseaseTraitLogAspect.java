package uk.ac.ebi.spot.goci.curation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.goci.curation.constants.EntityType;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class DiseaseTraitLogAspect {

    Logger log = LoggerFactory.getLogger(DiseaseTraitLogAspect.class);

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.getDiseaseTrait(..))")
    public void getDiseaseTrait() {
        /* GetDiseaseTrait Pointcut */
    }

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.getDiseaseTraits(..))")
    public void getDiseaseTraits() {
        /* GetDiseaseTraits Pointcut */
    }

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.createDiseaseTrait(..))")
    public void createDiseaseTrait() {
        /* CreateDiseaseTrait Pointcut */
    }

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.updateDiseaseTrait(..))")
    public void updateDiseaseTrait() {
        /* updateDiseaseTraits Pointcut */
    }

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.createDiseaseTraits(..))")
    public void createDiseaseTraits() {
        /* CreateDiseaseTraits Pointcut */
    }

    @Pointcut("execution(* uk.ac.ebi.spot.goci.curation.service.DiseaseTraitService.deleteDiseaseTrait(..))")
    public void deleteDiseaseTrait() {
        /* deleteDiseaseTraits Pointcut */
    }

    @SuppressWarnings("unchecked")
    @Around("getDiseaseTraits()")
    public Object getDiseaseTraitsLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = timeNow();
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Retrieving {} for user request: {} ", EntityType.DISEASE_TRAIT, arguments);
        Page<DiseaseTrait> pagedDiseaseTraits = (Page<DiseaseTrait>) joinPoint.proceed();
        log.info("Returning {} disease traits. in {}s", pagedDiseaseTraits.getTotalElements(), timeNow()-start);
        return pagedDiseaseTraits;
    }

    @SuppressWarnings("unchecked")
    @Around("getDiseaseTrait()")
    public Object getDiseaseTraitLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = timeNow();
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Retrieving {}: {}", EntityType.DISEASE_TRAIT, arguments);
        Optional<DiseaseTrait> diseaseTrait = (Optional<DiseaseTrait>) joinPoint.proceed();
        if (diseaseTrait.isPresent()) {
            log.info("Found Disease Traits: {} in {}s", diseaseTrait.get().getTrait(), timeNow()-start);
        } else {
            log.error("Unable to find Disease Trait: {}", arguments);
        }
        return diseaseTrait;
    }

    @Around("createDiseaseTrait()")
    public Object createDiseaseTraitLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = timeNow();
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Creating {}: {}", EntityType.DISEASE_TRAIT, arguments);
        DiseaseTrait diseaseTrait = (DiseaseTrait) joinPoint.proceed();
        log.info("{} created: {} in {}s", EntityType.DISEASE_TRAIT, diseaseTrait.getId(), timeNow()-start);
        return diseaseTrait;
    }

    @SuppressWarnings("unchecked")
    @Around("createDiseaseTraits()")
    public Object createDiseaseTraitsLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = timeNow();
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Creating {}: {}", EntityType.DISEASE_TRAIT, arguments);
        List<DiseaseTrait>  diseaseTrait = (List<DiseaseTrait> ) joinPoint.proceed();
        log.info("Bulk {} created in {}s", EntityType.DISEASE_TRAIT, timeNow()-start);
        return diseaseTrait;
    }

    @SuppressWarnings("unchecked")
    @Around("updateDiseaseTrait()")
    public Object updateDiseaseTraitLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = timeNow();
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Request to update {}: {}", EntityType.DISEASE_TRAIT, arguments.get(1));
        Optional<DiseaseTrait> diseaseTraitOption = (Optional<DiseaseTrait>) joinPoint.proceed();
        return diseaseTraitOption
                .map(diseaseTrait -> {
                    log.info("{}: {} was successfully updated in {}s", EntityType.DISEASE_TRAIT, diseaseTrait.getTrait(), timeNow()-start);
                    return diseaseTraitOption;
                })
                .orElseGet(() -> {
                    log.error("Update not possible as disease Trait: {} does not exist", arguments.get(1));
                    return diseaseTraitOption;
                });
    }

    @SuppressWarnings("unchecked")
    @Around("deleteDiseaseTrait()")
    public Object deleteDiseaseTraitLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        List<Object> arguments = Arrays.asList(joinPoint.getArgs());
        log.info("Attempting to delete {}: {}", EntityType.DISEASE_TRAIT, arguments);
        Map<String, String> response = (Map<String, String>) joinPoint.proceed();
        if (response.get("status").equals("deleted")) {
            log.info("Delete Trait: {} was successful", arguments);
        } else if (response.get("status").equals("DATA_IN_USE")){
            log.error("Trait: {} is in use, has {} studies, so cannot be deleted", arguments, response.get("studyCount"));
        }else {
            log.error("Unable to find Disease Trait: {}", arguments);
        }
        return response;
    }

    private long timeNow(){
        return System.currentTimeMillis();
    }

}