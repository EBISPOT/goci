package uk.ac.ebi.spot.goci.curation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.curation.util.Sorting;
import uk.ac.ebi.spot.goci.model.DiseaseTrait;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.util.List;

@Slf4j
@Service
public class EfoTraitService {

    private final EfoTraitRepository efoTraitRepository;

    public EfoTraitService(EfoTraitRepository efoTraitRepository) {
        this.efoTraitRepository = efoTraitRepository;
    }

    @CachePut(value= {"efoTraits", "efoTraitsHtml"})
    public EfoTrait save(EfoTrait efoTrait){
        return efoTraitRepository.save(efoTrait);
    }

    @CachePut(value= {"efoTraits", "efoTraitsHtml"})
    public List<EfoTrait> saveAll(List<EfoTrait> efoTraits){
        return efoTraitRepository.save(efoTraits);
    }

    @CacheEvict(value= {"efoTraits", "efoTraitsHtml"})
    public void delete(EfoTrait efoTrait){
        efoTraitRepository.delete(efoTrait);
    }

    @Cacheable("efoTraits")
    public List<EfoTrait> getAllEFOTraits() {
        log.info("Caching Efo Traits ... ");
        List<EfoTrait> efoTraits = efoTraitRepository.findAll(Sorting.sortByTraitAsc());
        log.info("{} efo traits found", efoTraits.size());
        return efoTraits;
    }

    @Cacheable("efoTraitsHtml")
    public String getAllEFOTraitsHtml() {
        List<EfoTrait> efoTraits = this.getAllEFOTraits();
        StringBuilder traitString = new StringBuilder();
        for (EfoTrait efoTrait : efoTraits){
            traitString.append(String.format("<option value='%s'> %s </option>", efoTrait.getId(), efoTrait.getTrait()));
        }
        return traitString.toString();
    }
}
