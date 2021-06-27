package uk.ac.ebi.spot.goci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.goci.model.BackgroundEfoDocument;
import uk.ac.ebi.spot.goci.model.EfoDocument;
import uk.ac.ebi.spot.goci.model.EfoTrait;
import uk.ac.ebi.spot.goci.repository.EfoTraitRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EfoDocumentCache {
    private Map<String, EfoDocument> documentMap = new HashMap<>();

    private EfoTraitRepository efoTraitRepository;

    public EfoDocumentCache(@Autowired EfoTraitRepository efoTraitRepository) {
        this.efoTraitRepository = efoTraitRepository;
        initCache();
    }

    private void initCache(){
        System.out.println("caching efos...");
        List<EfoTrait> traits = efoTraitRepository.findAll();
        for(EfoTrait trait: traits){
            EfoDocument document = new EfoDocument(trait);
            documentMap.put(trait.getTrait(), document);
        }
        System.out.println("efos cached: " + documentMap.size());
    }

    public EfoDocument getDocument(String efoId){
        return documentMap.get(efoId);
    }

    public BackgroundEfoDocument getBkgDocument(String efoId){
        EfoDocument efoDoc = documentMap.get(efoId);
        EfoTrait efoTrait = new EfoTrait(efoDoc.getMappedLabel(), efoDoc.getMappedUri(), null, null, null);
        // id is irrelevant, but not setting it causes a runtime error by a generic method
        efoTrait.setId(-1L);
        return new BackgroundEfoDocument(efoTrait);
    }
}
